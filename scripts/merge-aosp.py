#!/usr/bin/env python3
#
#
# Copyright (C) 2019-2021 The StatiXOS Project
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

 1. Get the absolute path of the script with os.path.realpath in case there is a symlink
    This script may be symlinked by a manifest so we need to account for that
 2. Get the folder containing the script with dirname
 3. Move into the folder that is three folders above that one and print it

"""

import argparse
import os
import shutil
import subprocess
import xml.etree.ElementTree as Et

import git
from git.exc import GitCommandError

BASE_URL = "https://android.googlesource.com/platform/"
WORKING_DIR = "{0}/../../..".format(os.path.dirname(os.path.realpath(__file__)))
MANIFEST_NAME = "include.xml"
REPOS_TO_MERGE = ["manifest"]
REPOS_RESULTS = {}


# useful helpers
def list_aosp_repos():
    """ Gathers all repos from AOSP """
    aosp_repos = []
    with open("{0}/.repo/manifests/default.xml".format(WORKING_DIR)) as aosp_manifest:
        aosp_root = Et.parse(aosp_manifest).getroot()
        for child in aosp_root:
            path = child.get("path")
            if path:
                aosp_repos.append(path)
    return aosp_repos


def get_manual_repos(args):
    """ Get all manually (optional) specified repos from arguments """
    ret_lst = []
    aosp_repos = list_aosp_repos()
    if args.repos_to_merge:
        for repo in args.repos_to_merge:
            if repo in aosp_repos:
                ret_lst.append(repo)
    return ret_lst, aosp_repos


def read_custom_manifest(aosp_repos):
    """ Finds all repos that need to be merged """
    print("Finding repos to merge...")
    with open("{0}/.repo/manifests/{1}".format(WORKING_DIR, MANIFEST_NAME)) as manifest:
        root = Et.parse(manifest).getroot()
        for custom in root:
            custom_path = custom.get("path")
            if custom_path and custom_path in aosp_repos:
                REPOS_TO_MERGE.append(custom_path)


def force_sync(repo_lst):
    """ Force syncs all the repos that need to be merged """
    print("Syncing repos")
    for repo in repo_lst:
        if os.path.isdir("{}{}".format(WORKING_DIR, repo)):
            shutil.rmtree("{}{}".format(WORKING_DIR, repo))

    cpu_count = str(os.cpu_count())
    args = [
        "repo",
        "sync",
        "-c",
        "--force-sync",
        "-f",
        "--no-clone-bundle",
        "--no-tag",
        "-j",
        cpu_count,
        "-q",
    ] + repo_lst
    subprocess.run(args, check=False)


def merge(repo_lst, branch):
    """ Merges the necessary repos and lists if a repo succeeds or fails """
    failures = []
    successes = []
    for repo in repo_lst:
        print("Merging " + repo)
        repo_str = repo
        os.chdir("{0}/{1}".format(WORKING_DIR, repo))
        if repo == "build/make":
            repo_str = "build"
        if repo == "packages/apps/PermissionController":
            repo_str = "packages/apps/PackageInstaller"
        try:
            git.cmd.Git().pull("{}{}".format(BASE_URL, repo_str), branch)
            successes.append(repo)
        except GitCommandError as git_error:
            print(git_error)
            failures.append(repo)

    REPOS_RESULTS.update({"Successes": successes, "Failures": failures})


def get_actual_merged_repos(branch):
    """Gets all the repos that were actually merged and
    not the ones that were just up-to-date"""
    status_zero_repos = REPOS_RESULTS["Successes"]
    good_repos = []
    for repo in status_zero_repos:
        git_repo = git.Repo("{0}/{1}".format(WORKING_DIR, repo))
        commits = list(git_repo.iter_commits("HEAD", max_count=1))
        result = commits[0].message
        if branch in result:
            good_repos.append(repo)
    REPOS_RESULTS["Successes"] = good_repos


def print_results(branch):
    """ Prints all all repos that will need to be manually fixed """
    get_actual_merged_repos(branch)
    if REPOS_RESULTS["Failures"]:
        print("\nThese repos failed to merge, fix manually: ")
        for failure in REPOS_RESULTS["Failures"]:
            print(failure)
    if REPOS_RESULTS["Successes"]:
        print("\nRepos that merged successfully: ")
        for success in REPOS_RESULTS["Successes"]:
            print(success)
    if not REPOS_RESULTS["Failures"] and REPOS_RESULTS["Successes"]:
        print(
            "{0} merged successfully! Compile and test before pushing to GitHub.".format(
                branch
            )
        )
    elif not REPOS_RESULTS["Failures"] and not REPOS_RESULTS["Successes"]:
        print("Unable to retrieve any results")


def main():
    """Gathers and merges all repos from AOSP and
    reports all repos that need to be fixed manually"""
    parser = argparse.ArgumentParser(description="Merge an AOSP revision.")
    parser.add_argument(
        "branch_to_merge",
        metavar="branch",
        type=str,
        help="a tag to merge from android.googlesource.com",
    )
    parser.add_argument(
        "--repos",
        dest="repos_to_merge",
        nargs="*",
        type=str,
        help="path of repos to merge",
    )
    args = parser.parse_args()
    branch = "android-{}".format(args.branch_to_merge)

    repo_lst, aosp_repos = get_manual_repos(args)
    if not repo_lst:
        read_custom_manifest(aosp_repos)
        if REPOS_TO_MERGE:
            force_sync(REPOS_TO_MERGE)
            merge(REPOS_TO_MERGE, branch)
            os.chdir(WORKING_DIR)
            print_results(branch)
        else:
            print("No repos to sync")
    else:
        force_sync(repo_lst)
        merge(repo_lst, branch)
        os.chdir(WORKING_DIR)
        print_results(branch)


if __name__ == "__main__":
    # execute only if run as a script
    main()
