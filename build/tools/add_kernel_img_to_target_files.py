#!/usr/bin/env python
#
# Copyright (C) 2014 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
Given a target-files zipfile that does not contain images (ie, does
not have an IMAGES/ top-level subdirectory), produce the images and
add them to the zipfile.

Usage:  add_img_to_target_files [flag] target_files

  -a  (--add_missing)
      Build and add missing images to "IMAGES/". If this option is
      not specified, this script will simply exit when "IMAGES/"
      directory exists in the target file.

  -r  (--rebuild_recovery)
      Rebuild the recovery patch and write it to the system image. Only
      meaningful when system image needs to be rebuilt and there're separate
      boot / recovery images.

"""

from __future__ import print_function

import datetime
import logging
import os
import shlex
import shutil
import stat
import sys
import uuid
import tempfile
import zipfile

import build_image
import build_super_image
import common
import verity_utils
import ota_metadata_pb2

from apex_utils import GetApexInfoFromTargetFiles
from common import AddCareMapForAbOta, ZipDelete

if sys.hexversion < 0x02070000:
  print("Python 2.7 or newer is required.", file=sys.stderr)
  sys.exit(1)

logger = logging.getLogger(__name__)

OPTIONS = common.OPTIONS
OPTIONS.add_missing = False
OPTIONS.rebuild_recovery = False
OPTIONS.replace_updated_files_list = []

# Use a fixed timestamp (01/01/2009 00:00:00 UTC) for files when packaging
# images. (b/24377993, b/80600931)
FIXED_FILE_TIMESTAMP = int((
    datetime.datetime(2009, 1, 1, 0, 0, 0, 0, None) -
    datetime.datetime.utcfromtimestamp(0)).total_seconds())


class OutputFile(object):
  """A helper class to write a generated file to the given dir or zip.

  When generating images, we want the outputs to go into the given zip file, or
  the given dir.

  Attributes:
    name: The name of the output file, regardless of the final destination.
  """

  def __init__(self, output_zip, input_dir, *args):
    # We write the intermediate output file under the given input_dir, even if
    # the final destination is a zip archive.
    self.name = os.path.join(input_dir, *args)
    self._output_zip = output_zip
    if self._output_zip:
      self._zip_name = os.path.join(*args)

  def Write(self, compress_type=None):
    if self._output_zip:
      common.ZipWrite(self._output_zip, self.name,
                      self._zip_name, compress_type=compress_type)


def AddVendorDlkm(output_zip):
  """Turn the contents of VENDOR_DLKM into an vendor_dlkm image and store it in output_zip."""

  img = OutputFile(output_zip, OPTIONS.input_tmp, "IMAGES", "vendor_dlkm.img")
  if os.path.exists(img.name):
    logger.info("vendor_dlkm.img already exists; no need to rebuild...")
    return img.name

  block_list = OutputFile(
      output_zip, OPTIONS.input_tmp, "IMAGES", "vendor_dlkm.map")
  CreateImage(
      OPTIONS.input_tmp, OPTIONS.info_dict, "vendor_dlkm", img,
      block_list=block_list)
  return img.name


def AddOdmDlkm(output_zip):
  """Turn the contents of OdmDlkm into an odm_dlkm image and store it in output_zip."""

  img = OutputFile(output_zip, OPTIONS.input_tmp, "IMAGES", "odm_dlkm.img")
  if os.path.exists(img.name):
    logger.info("odm_dlkm.img already exists; no need to rebuild...")
    return img.name

  block_list = OutputFile(
      output_zip, OPTIONS.input_tmp, "IMAGES", "odm_dlkm.map")
  CreateImage(
      OPTIONS.input_tmp, OPTIONS.info_dict, "odm_dlkm", img,
      block_list=block_list)
  return img.name

def AddSystemDlkm(output_zip):
  """Turn the contents of SystemDlkm into an system_dlkm image and store it in output_zip."""

  img = OutputFile(output_zip, OPTIONS.input_tmp, "IMAGES", "system_dlkm.img")
  if os.path.exists(img.name):
    logger.info("system_dlkm.img already exists; no need to rebuild...")
    return img.name

  block_list = OutputFile(
      output_zip, OPTIONS.input_tmp, "IMAGES", "system_dlkm.map")
  CreateImage(
      OPTIONS.input_tmp, OPTIONS.info_dict, "system_dlkm", img,
      block_list=block_list)
  return img.name


def AddDtbo(output_zip):
  """Adds the DTBO image.

  Uses the image under IMAGES/ if it already exists. Otherwise looks for the
  image under PREBUILT_IMAGES/, signs it as needed, and returns the image name.
  """
  img = OutputFile(output_zip, OPTIONS.input_tmp, "IMAGES", "dtbo.img")
  if os.path.exists(img.name):
    logger.info("dtbo.img already exists; no need to rebuild...")
    return img.name

  dtbo_prebuilt_path = os.path.join(
      OPTIONS.input_tmp, "PREBUILT_IMAGES", "dtbo.img")
  assert os.path.exists(dtbo_prebuilt_path)
  shutil.copy(dtbo_prebuilt_path, img.name)

  # AVB-sign the image as needed.
  if OPTIONS.info_dict.get("avb_enable") == "true":
    # Signing requires +w
    os.chmod(img.name, os.stat(img.name).st_mode | stat.S_IWUSR)

    avbtool = OPTIONS.info_dict["avb_avbtool"]
    part_size = OPTIONS.info_dict["dtbo_size"]
    # The AVB hash footer will be replaced if already present.
    cmd = [avbtool, "add_hash_footer", "--image", img.name,
           "--partition_size", str(part_size), "--partition_name", "dtbo"]
    common.AppendAVBSigningArgs(cmd, "dtbo")
    args = OPTIONS.info_dict.get("avb_dtbo_add_hash_footer_args")
    if args and args.strip():
      cmd.extend(shlex.split(args))
    common.RunAndCheckOutput(cmd)

  img.Write()
  return img.name


def CreateImage(input_dir, info_dict, what, output_file, block_list=None):
  logger.info("creating %s.img...", what)

  image_props = build_image.ImagePropFromGlobalDict(info_dict, what)
  image_props["timestamp"] = FIXED_FILE_TIMESTAMP

  if what == "system":
    fs_config_prefix = ""
  else:
    fs_config_prefix = what + "_"

  fs_config = os.path.join(
      input_dir, "META/" + fs_config_prefix + "filesystem_config.txt")
  if not os.path.exists(fs_config):
    fs_config = None

  # Override values loaded from info_dict.
  if fs_config:
    image_props["fs_config"] = fs_config
  if block_list:
    image_props["block_list"] = block_list.name

  # Use repeatable ext4 FS UUID and hash_seed UUID (based on partition name and
  # build fingerprint). Also use the legacy build id, because the vbmeta digest
  # isn't available at this point.
  build_info = common.BuildInfo(info_dict, use_legacy_id=True)
  uuid_seed = what + "-" + build_info.GetPartitionFingerprint(what)
  image_props["uuid"] = str(uuid.uuid5(uuid.NAMESPACE_URL, uuid_seed))
  hash_seed = "hash_seed-" + uuid_seed
  image_props["hash_seed"] = str(uuid.uuid5(uuid.NAMESPACE_URL, hash_seed))

  build_image.BuildImage(
      os.path.join(input_dir, what.upper()), image_props, output_file.name)

  output_file.Write()
  if block_list:
    block_list.Write()

  # Set the '_image_size' for given image size.
  is_verity_partition = "verity_block_device" in image_props
  verity_supported = (image_props.get("verity") == "true" or
                      image_props.get("avb_enable") == "true")
  is_avb_enable = image_props.get("avb_hashtree_enable") == "true"
  if verity_supported and (is_verity_partition or is_avb_enable):
    image_size = image_props.get("image_size")
    if image_size:
      image_size_key = what + "_image_size"
      info_dict[image_size_key] = int(image_size)

  use_dynamic_size = (
      info_dict.get("use_dynamic_partition_size") == "true" and
      what in shlex.split(info_dict.get("dynamic_partition_list", "").strip()))
  if use_dynamic_size:
    info_dict.update(build_image.GlobalDictFromImageProp(image_props, what))


def AddPartitionTable(output_zip):
  """Create a partition table image and store it in output_zip."""

  img = OutputFile(
      output_zip, OPTIONS.input_tmp, "IMAGES", "partition-table.img")
  bpt = OutputFile(
      output_zip, OPTIONS.input_tmp, "META", "partition-table.bpt")

  # use BPTTOOL from environ, or "bpttool" if empty or not set.
  bpttool = os.getenv("BPTTOOL") or "bpttool"
  cmd = [bpttool, "make_table", "--output_json", bpt.name,
         "--output_gpt", img.name]
  input_files_str = OPTIONS.info_dict["board_bpt_input_files"]
  input_files = input_files_str.split(" ")
  for i in input_files:
    cmd.extend(["--input", i])
  disk_size = OPTIONS.info_dict.get("board_bpt_disk_size")
  if disk_size:
    cmd.extend(["--disk_size", disk_size])
  args = OPTIONS.info_dict.get("board_bpt_make_table_args")
  if args:
    cmd.extend(shlex.split(args))
  common.RunAndCheckOutput(cmd)

  img.Write()
  bpt.Write()


def ReplaceUpdatedFiles(zip_filename, files_list):
  """Updates all the ZIP entries listed in files_list.

  For now the list includes META/care_map.pb, and the related files under
  SYSTEM/ after rebuilding recovery.
  """
  common.ZipDelete(zip_filename, files_list)
  output_zip = zipfile.ZipFile(zip_filename, "a",
                               compression=zipfile.ZIP_DEFLATED,
                               allowZip64=True)
  for item in files_list:
    file_path = os.path.join(OPTIONS.input_tmp, item)
    assert os.path.exists(file_path)
    common.ZipWrite(output_zip, file_path, arcname=item)
  common.ZipClose(output_zip)


def HasPartition(partition_name):
  """Determines if the target files archive should build a given partition."""

  return ((os.path.isdir(
      os.path.join(OPTIONS.input_tmp, partition_name.upper())) and
      OPTIONS.info_dict.get(
      "building_{}_image".format(partition_name)) == "true") or
      os.path.exists(
      os.path.join(OPTIONS.input_tmp, "IMAGES",
                   "{}.img".format(partition_name))))


def AddImagesToTargetFiles(filename):
  """Creates and adds images (boot/recovery/system/...) to a target_files.zip.

  It works with either a zip file (zip mode), or a directory that contains the
  files to be packed into a target_files.zip (dir mode). The latter is used when
  being called from build/make/core/Makefile.

  The images will be created under IMAGES/ in the input target_files.zip.

  Args:
    filename: the target_files.zip, or the zip root directory.
  """
  if os.path.isdir(filename):
    OPTIONS.input_tmp = os.path.abspath(filename)
  else:
    OPTIONS.input_tmp = common.UnzipTemp(filename)

  if not OPTIONS.add_missing:
    if os.path.isdir(os.path.join(OPTIONS.input_tmp, "IMAGES")):
      logger.warning("target_files appears to already contain images.")
      sys.exit(1)

  OPTIONS.info_dict = common.LoadInfoDict(OPTIONS.input_tmp, repacking=True)

  has_recovery = OPTIONS.info_dict.get("no_recovery") != "true"
  has_boot = OPTIONS.info_dict.get("no_boot") != "true"
  has_init_boot = OPTIONS.info_dict.get("init_boot") == "true"
  has_vendor_boot = OPTIONS.info_dict.get("vendor_boot") == "true"
  has_vendor_kernel_boot = OPTIONS.info_dict.get("vendor_kernel_boot") == "true"

  # {vendor,odm,product,system_ext,vendor_dlkm,odm_dlkm, system_dlkm, system, system_other}.img
  # can be built from source, or  dropped into target_files.zip as a prebuilt blob.
  has_vendor = HasPartition("vendor")
  has_odm = HasPartition("odm")
  has_vendor_dlkm = HasPartition("vendor_dlkm")
  has_odm_dlkm = HasPartition("odm_dlkm")
  has_system_dlkm = HasPartition("system_dlkm")
  has_product = HasPartition("product")
  has_system_ext = HasPartition("system_ext")
  has_system = HasPartition("system")
  has_system_other = HasPartition("system_other")
  has_userdata = OPTIONS.info_dict.get("building_userdata_image") == "true"
  has_cache = OPTIONS.info_dict.get("building_cache_image") == "true"

  # Set up the output destination. It writes to the given directory for dir
  # mode; otherwise appends to the given ZIP.
  if os.path.isdir(filename):
    output_zip = None
  else:
    output_zip = zipfile.ZipFile(filename, "a",
                                 compression=zipfile.ZIP_DEFLATED,
                                 allowZip64=True)

  # Always make input_tmp/IMAGES available, since we may stage boot / recovery
  # images there even under zip mode. The directory will be cleaned up as part
  # of OPTIONS.input_tmp.
  images_dir = os.path.join(OPTIONS.input_tmp, "IMAGES")
  if not os.path.isdir(images_dir):
    os.makedirs(images_dir)

  # A map between partition names and their paths, which could be used when
  # generating AVB vbmeta image.
  partitions = {}

  def banner(s):
    logger.info("\n\n++++ %s  ++++\n\n", s)

  boot_image = None
  if has_boot:
    banner("boot")
    boot_images = OPTIONS.info_dict.get("boot_images")
    if boot_images is None:
      boot_images = "boot.img"
    for index, b in enumerate(boot_images.split()):
      # common.GetBootableImage() returns the image directly if present.
      boot_image = common.GetBootableImage(
          "IMAGES/" + b, b, OPTIONS.input_tmp, "BOOT")
      # boot.img may be unavailable in some targets (e.g. aosp_arm64).
      if boot_image:
        boot_image_path = os.path.join(OPTIONS.input_tmp, "IMAGES", b)
        # Although multiple boot images can be generated, include the image
        # descriptor of only the first boot image in vbmeta
        if index == 0:
          partitions['boot'] = boot_image_path
        if not os.path.exists(boot_image_path):
          boot_image.WriteToDir(OPTIONS.input_tmp)
          if output_zip:
            boot_image.AddToZip(output_zip)

  if has_init_boot:
    banner("init_boot")
    init_boot_image = common.GetBootableImage(
        "IMAGES/init_boot.img", "init_boot.img", OPTIONS.input_tmp, "INIT_BOOT")
    if init_boot_image:
      partitions['init_boot'] = os.path.join(
          OPTIONS.input_tmp, "IMAGES", "init_boot.img")
      if not os.path.exists(partitions['init_boot']):
        init_boot_image.WriteToDir(OPTIONS.input_tmp)
        if output_zip:
          init_boot_image.AddToZip(output_zip)

  if has_vendor_boot:
    banner("vendor_boot")
    vendor_boot_image = common.GetVendorBootImage(
        "IMAGES/vendor_boot.img", "vendor_boot.img", OPTIONS.input_tmp,
        "VENDOR_BOOT")
    if vendor_boot_image:
      partitions['vendor_boot'] = os.path.join(OPTIONS.input_tmp, "IMAGES",
                                               "vendor_boot.img")
      if not os.path.exists(partitions['vendor_boot']):
        vendor_boot_image.WriteToDir(OPTIONS.input_tmp)
        if output_zip:
          vendor_boot_image.AddToZip(output_zip)

  if has_vendor_kernel_boot:
    banner("vendor_kernel_boot")
    vendor_kernel_boot_image = common.GetVendorKernelBootImage(
        "IMAGES/vendor_kernel_boot.img", "vendor_kernel_boot.img", OPTIONS.input_tmp,
        "VENDOR_KERNEL_BOOT")
    if vendor_kernel_boot_image:
      partitions['vendor_kernel_boot'] = os.path.join(OPTIONS.input_tmp, "IMAGES",
                                               "vendor_kernel_boot.img")
      if not os.path.exists(partitions['vendor_kernel_boot']):
        vendor_kernel_boot_image.WriteToDir(OPTIONS.input_tmp)
        if output_zip:
          vendor_kernel_boot_image.AddToZip(output_zip)

  recovery_image = None
  if has_recovery:
    banner("recovery")
    recovery_image = common.GetBootableImage(
        "IMAGES/recovery.img", "recovery.img", OPTIONS.input_tmp, "RECOVERY")
    assert recovery_image, "Failed to create recovery.img."
    partitions['recovery'] = os.path.join(
        OPTIONS.input_tmp, "IMAGES", "recovery.img")
    if not os.path.exists(partitions['recovery']):
      recovery_image.WriteToDir(OPTIONS.input_tmp)
      if output_zip:
        recovery_image.AddToZip(output_zip)

      banner("recovery (two-step image)")
      # The special recovery.img for two-step package use.
      recovery_two_step_image = common.GetBootableImage(
          "OTA/recovery-two-step.img", "recovery-two-step.img",
          OPTIONS.input_tmp, "RECOVERY", two_step_image=True)
      assert recovery_two_step_image, "Failed to create recovery-two-step.img."
      recovery_two_step_image_path = os.path.join(
          OPTIONS.input_tmp, "OTA", "recovery-two-step.img")
      if not os.path.exists(recovery_two_step_image_path):
        recovery_two_step_image.WriteToDir(OPTIONS.input_tmp)
        if output_zip:
          recovery_two_step_image.AddToZip(output_zip)

  def add_partition(partition, has_partition, add_func, add_args):
    if has_partition:
      banner(partition)
      partitions[partition] = add_func(output_zip, *add_args)

  if OPTIONS.info_dict.get("board_bpt_enable") == "true":
    banner("partition-table")
    AddPartitionTable(output_zip)

  add_partition("dtbo",
                OPTIONS.info_dict.get("has_dtbo") == "true", AddDtbo, [])

  if output_zip:
    common.ZipClose(output_zip)
    if OPTIONS.replace_updated_files_list:
      ReplaceUpdatedFiles(output_zip.filename,
                          OPTIONS.replace_updated_files_list)


def OptimizeCompressedEntries(zipfile_path):
  """Convert files that do not compress well to uncompressed storage

  EROFS images tend to be compressed already, so compressing them again
  yields little space savings. Leaving them uncompressed will make
  downstream tooling's job easier, and save compute time.
  """
  if not zipfile.is_zipfile(zipfile_path):
    return
  entries_to_store = []
  with tempfile.TemporaryDirectory() as tmpdir:
    with zipfile.ZipFile(zipfile_path, "r", allowZip64=True) as zfp:
      for zinfo in zfp.filelist:
        if not zinfo.filename.startswith("IMAGES/") and not zinfo.filename.startswith("META"):
          continue
        # Don't try to store userdata.img uncompressed, it's usually huge.
        if zinfo.filename.endswith("userdata.img"):
          continue
        if zinfo.compress_size > zinfo.file_size * 0.80 and zinfo.compress_type != zipfile.ZIP_STORED:
          entries_to_store.append(zinfo)
          zfp.extract(zinfo, tmpdir)
    if len(entries_to_store) == 0:
      return
    # Remove these entries, then re-add them as ZIP_STORED
    ZipDelete(zipfile_path, [entry.filename for entry in entries_to_store])
    with zipfile.ZipFile(zipfile_path, "a", allowZip64=True) as zfp:
      for entry in entries_to_store:
        zfp.write(os.path.join(tmpdir, entry.filename), entry.filename, compress_type=zipfile.ZIP_STORED)


def main(argv):
  def option_handler(o, a):
    if o in ("-a", "--add_missing"):
      OPTIONS.add_missing = True
    elif o in ("-r", "--rebuild_recovery",):
      OPTIONS.rebuild_recovery = True
    else:
      return False
    return True

  args = common.ParseOptions(
      argv, __doc__, extra_opts="ar",
      extra_long_opts=["add_missing"],
      extra_option_handler=option_handler)

  if len(args) != 1:
    common.Usage(__doc__)
    sys.exit(1)

  common.InitLogging()

  AddImagesToTargetFiles(args[0])
  OptimizeCompressedEntries(args[0])
  logger.info("done.")


if __name__ == '__main__':
  try:
    common.CloseInheritedPipes()
    main(sys.argv[1:])
  finally:
    common.Cleanup()
