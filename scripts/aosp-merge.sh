#!/bin/bash
# Copyright (C) 2016 DirtyUnicorns
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

#Your source directory goes here
WORKING_DIR=/home/mazda/du-mm

#The tag you want to merge in goes here
BRANCH=android-6.0.1_r22

#Google source url
REPO=https://android.googlesource.com/platform

function merge_packages () {
  declare -a array=($@)
  for i in `seq 0 $(( ${#array[@]} - 1 ))`
  do
    cd $WORKING_DIR/packages/${array[i]}
    git pull $REPO/packages/${array[i]}.git -t $BRANCH
  done
}

function merge_hardware () {
  declare -a array=($@)
  for i in `seq 0 $(( ${#array[@]} - 1 ))`
  do
    cd $WORKING_DIR/hardware/${array[i]}
    git pull $REPO/hardware/${array[i]}.git -t $BRANCH
  done
}

function merge_frameworks () {
  declare -a array=($@)
  for i in `seq 0 $(( ${#array[@]} - 1 ))`
  do
    cd $WORKING_DIR/frameworks/${array[i]}
    git pull $REPO/frameworks/${array[i]}.git -t $BRANCH
  done
}

function merge_misc () {
  declare -a array=($@)
  for i in `seq 0 $(( ${#array[@]} - 1 ))`
  do
    cd $WORKING_DIR/${array[i]}
    git pull $REPO/${array[i]}.git -t $BRANCH
  done
}

#This is what repos you want to remove prior to repo syncing
declare -a root=('DU-Scripts' 'abi' 'bionic' 'art' 'bootable' 'build' 'dalvik' 'development' 'device' 'external' 'frameworks' 'hardware' 'kernel' 'libcore' 'libnativehelper' 'manifest' 'ndk' 'out' 'packages' 'pdk' 'prebuilts'
                 'sdk' 'system' 'tools' 'vendor')

#These are the repos you track that are under SOURCE/packages/
declare -a packages=('apps/Bluetooth' 'apps/Calculator' 'apps/Calendar' 'apps/Camera2' 'apps/CellBroadcastReceiver' 'apps/Contacts'
                          'apps/ContactsCommon' 'apps/Dialer' 'apps/Email' 'apps/Gallery2' 'apps/InCallUI' 'apps/Launcher3' 'apps/Messaging'
                          'apps/PackageInstaller' 'apps/PhoneCommon' 'apps/Settings' 'providers/DownloadProvider' 'providers/MediaProvider'
                          'providers/TelephonyProvider' 'inputmethods/LatinIME' 'wallpapers/LivePicker' 'wallpapers/PhaseBeam'
                          'services/Telecomm' 'services/Telephony')

#These are the repos you track that are under SOURCE/hardware/
declare -a hardware=('broadcom/libbt' 'broadcom/wlan' 'libhardware' 'libhardware_legacy' 'qcom/audio' 'qcom/bt' 'qcom/camera' 'qcom/display'
                     'qcom/gps' 'qcom/keymaster' 'qcom/media' 'qcom/power' 'qcom/wlan' 'ril' 'ti/omap3' 'ti/omap4xxx')

#These are the repos you track that are under SOURCE/frameworks/
declare -a frameworks=('av' 'base' 'native' 'opt/datetimepicker' 'opt/telephony' 'opt/timezonepicker' 'opt/net/ims' 'opt/net/wifi' 'webview')

#These are the repos you track that are not covered above
declare -a misc=('build' 'development' 'external/sepolicy' 'libcore' 'system/bt' 'system/core' 'system/extras' 'system/media'
                       'system/netd' 'system/vold')

cd $WORKING_DIR
echo "================================================"
echo "     Removing all the repos except for .repo    "
echo ""
echo " This is done so we make sure you're up to date "
echo "================================================"
delete_useless ${root[@]}
cd .repo
rm -rf local_manifests
cd ..
echo "Repo Syncing........."
sleep 10
repo sync >> /dev/null
if [ $? -eq 0 ]; then
  echo "Repo Sync success"
else
  echo "Repo Sync failure"
  exit 1
fi
cd $WORKING_DIR
echo "Merging packages with AOSP"
merge_packages ${packages[@]}
cd $WORKING_DIR
echo "Merging hardware repos with AOSP"
merge_hardware ${hardware[@]}
cd $WORKING_DIR
echo "Merging framework repos with AOSP"
merge_frameworks ${frameworks[@]}
cd $WORKING_DIR
echo "Merging misc repos with AOSP"
merge_misc ${misc[@]}
cd $WORKING_DIR
echo ""
echo "============ "$BRANCH" is merged ============"
echo "===== Compile and test before pushing to github ====="
echo ""
