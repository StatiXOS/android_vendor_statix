#!/usr/bin/env python3


# Copyright (C) 2019 The StatiXOS Project
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
Merge script for StatiXOS

 The source directory; this is automatically two folder up because the script
 is located in vendor/statix/scripts. Other ROMs will need to change this. The logic is
 as follows:

 1. Get the absolute path of the script with readlink in case there is a symlink
    This script may be symlinked by a manifest so we need to account for that
 2. Get the folder containing the script with dirname
 3. Move into the folder that is two folder above that one and print it

"""

import glob
import os
import sys
import xml.etree.ElementTree as Et

BASE_URL = "https://android.googlesource.com/platform/"
BLACKLIST = glob.glob("hardware/qcom/*")
WORKING_DIR = "{0}/../../..".format(os.path.dirname(os.path.realpath(__file__)))
MANIFEST_NAME = "include.xml"
REPOS_TO_MERGE = ["manifest"]
BRANCH_STR = "android-{}".format(sys.argv[1])
REPOS_RESULTS = {}


# useful helpers
def list_aosp_repos():
    """ Gathers all repos from AOSP """
    aosp_repos = []
    with open('{0}/.repo/manifests/default.xml'.format(WORKING_DIR)) as aosp_manifest:
        aosp_root = Et.parse(aosp_manifest).getroot()
        for child in aosp_root:
            path = child.get('path')
            if path:
                aosp_repos.append(path)
    return aosp_repos


def read_custom_manifest():
    """ Finds all repos that need to be merged """
    print("Finding repos to merge...")
    with open('{0}/.repo/manifests/{1}'.format(WORKING_DIR, MANIFEST_NAME)) as manifest:
        root = Et.parse(manifest).getroot()
        aosp_repos = list_aosp_repos()
        for custom in root:
            custom_path = custom.get('path')
            if custom_path and custom_path in aosp_repos:
                if custom_path not in BLACKLIST:
                    REPOS_TO_MERGE.append(custom_path)


def force_sync():
    """ Force syncs all the repos that need to be merged """
    print("Syncing repos")
    for repo in REPOS_TO_MERGE:
        os.system('rm -rf {0}'.format(repo))
    os.system('repo sync -c --force-sync -f --no-clone-bundle --no-tag -j$(nproc --all) -q')
    for repo in REPOS_TO_MERGE:
        os.chdir("{0}/{1}".format(WORKING_DIR, repo))
        os.system('git fetch statix --unshallow')


def merge():
    """ Merges the necessary repos and lists if a repo succeeds or fails """
    failures = []
    successes = []
    for repo in REPOS_TO_MERGE:
        repo_str = repo
        os.chdir("{0}/{1}".format(WORKING_DIR, repo))
        if repo == "build/make":
            repo_str = "build"
        cmd = 'git pull {0}{1} {2}'.format(BASE_URL, repo_str, BRANCH_STR)
        ret_val = os.system(cmd)
        if ret_val:
            failures.append(repo)
        else:
            successes.append(repo)
    REPOS_RESULTS.update({'Successes': successes, 'Failures': failures})


def print_results():
    """ Prints all all repos that will need to be manually fixed """
    if REPOS_RESULTS['Failures']:
        print("\nThese repos failed to merge, fix manually: ")
        for failure in REPOS_RESULTS['Failures']:
            print(failure)
    if REPOS_RESULTS['Successes']:
        print("\nRepos that merged successfully: ")
        for success in REPOS_RESULTS['Successes']:
            print(success)
    if not REPOS_RESULTS['Failures'] and REPOS_RESULTS['Successes']:
       	print("{0} merged successfully! Compile and test before pushing to GitHub.".format(branch_str))
    elif not REPOS_RESULTS['Failures'] and not REPOS_RESULTS['Successes']:
        print("Unable to retrieve any results")

def main():
    """ Gathers and merges all repos from AOSP and
    reports all repos that need to be fixed manually"""

    read_custom_manifest()
    if REPOS_TO_MERGE:
        force_sync()
        merge()
        os.chdir(WORKING_DIR)
        print_results()
    else:
        print("No repos to sync")


if __name__ == "__main__":
    # execute only if run as a script
    main()

