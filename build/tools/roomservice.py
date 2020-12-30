#!/usr/bin/env python3

# Copyright (C) 2013 Cybojenix <anthonydking@gmail.com>
# Copyright (C) 2013 The OmniROM Project
# Copyright (C) 2020 Nicholas Christian  <ndchristian@gmail.com> & The StatiXOS Project
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.


import json
import os
import os.path
import re
import subprocess
import sys
import urllib.request
from xml.etree import ElementTree


PRODUCT = sys.argv[1]
BRANCH = "11"
ORGANIZATION_NAME = "StatiXOS"
DEPENDENCIES_FILE_NAME = "statix.dependencies"
LOCAL_MANIFESTS_PATH = ".repo/local_manifests/"
LOCAL_MANIFESTS_FILE_NAME = "electric_manifest.xml"

try:
    DEVICE = PRODUCT[PRODUCT.index("_") + 1:]
except ValueError:
    DEVICE = sys.argv[1]


def exists_in_tree(lm, repo):
    """ Checks if the repository exists in the tree. """
    for child in lm.iter("project"):
        if child.attrib["path"].endswith(repo):
            return child


def exists_in_tree_device(lm, repo):
    """ Checks if the repository exists in the device tree. """
    for child in lm.iter("project"):
        if child.attrib["name"].endswith(repo):
            return child


def indent(elem, level=0):
    """ In-place pretty print formatter. """
    i = "\n" + level * "  "
    if elem:
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            indent(elem, level + 1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i


def get_from_manifest():
    """ Gets a repository path from the manifest. """
    try:
        lm = ElementTree.parse(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}")
        lm = lm.getroot()
    except ElementTree.ParseError:
        lm = ElementTree.Element("manifest")

    for local_path in lm.findall("project"):
        if re.search(f"android_device_.*_{DEVICE}$", local_path.get("name")):
            return local_path.get("path")

    try:  # Devices originally from AOSP are in the main manifest
        mm = ElementTree.parse(".repo/manifest.xml")
        mm = mm.getroot()
    except ElementTree.ParseError:
        mm = ElementTree.Element("manifest")

    for local_path in mm.findall("project"):
        if re.search(f"android_device_.*_{DEVICE}$", local_path.get("name")):
            return local_path.get("path")


def is_in_manifest(repo_name, branch):
    """ Checks if a repository is in the manifest. """
    try:
        lm = ElementTree.parse(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}")
        lm = lm.getroot()
    except ElementTree.ParseError:
        lm = ElementTree.Element("manifest")

    for local_path in lm.findall("project"):
        if local_path.get("name") == repo_name and local_path.get("revision") == branch:
            return True


def add_to_manifest_dependencies(repos):
    """ Adds repositories to local manifest. """
    try:
        lm = ElementTree.parse(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}")
        lm = lm.getroot()
    except ElementTree.ParseError:
        lm = ElementTree.Element("manifest")
    for repo in repos:
        repo_name = repo["repository"]
        repo_target = repo["target_path"]
        existing_project = exists_in_tree(lm, repo_target)
        if existing_project is not None:
            if existing_project.attrib["name"] != repo["repository"]:
                print(f"Updating dependency {repo_name}")
                existing_project.set("name", repo["repository"])
            if existing_project.attrib["revision"] == repo["branch"]:
                print(f"{ORGANIZATION_NAME}/{repo_name} already exists")
            else:
                print(f"Updating branch for {repo_name} to {repo['branch']}")
                existing_project.set("revision", repo["branch"])
        else:
            print(f"Adding dependency: {repo_name} -> {repo_target}")
            project = ElementTree.Element(
                "project",
                attrib={
                    "path": repo_target,
                    "remote": repo["remote"],
                    "name": repo_name,
                    "revision": BRANCH,
                },
            )
            if "branch" in repo:
                project.set("revision", repo["branch"])

            lm.append(project)

    indent(lm, 0)
    raw_xml = "\n".join(
        ('<?xml version="1.0" encoding="UTF-8"?>', ElementTree.tostring(lm).decode(),)
    )
    with open(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}", "w") as f:
        f.write(raw_xml)


def add_to_manifest(repos):
    """ Adds repositories to the manifest. """
    try:
        lm = ElementTree.parse(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}")
        lm = lm.getroot()
    except ElementTree.ParseError:
        lm = ElementTree.Element("manifest")

    for repo in repos:
        repo_name = repo["repository"]
        repo_target = repo["target_path"]
        existing_project = exists_in_tree_device(lm, repo_name)
        if existing_project is not None:
            if existing_project.attrib["revision"] == repo["branch"]:
                print(f"{ORGANIZATION_NAME}/{repo_name} already exists")
            else:
                print(f"Updating branch for {ORGANIZATION_NAME}/{repo_name} to {repo['branch']}")
                existing_project.set("revision", repo["branch"])
            continue

        print(f"Adding dependency: {ORGANIZATION_NAME}/{repo_name} -> {repo_target}")
        project = ElementTree.Element(
            "project",
            attrib={
                "path": repo_target,
                "remote": repo["remote"],
                "name": f"{ORGANIZATION_NAME}/{repo_name}",
                "revision": BRANCH,
            },
        )

        if "branch" in repo:
            project.set("revision", repo["branch"])

        lm.append(project)

    indent(lm, 0)
    raw_xml = "\n".join(
        ('<?xml version="1.0" encoding="UTF-8"?>', ElementTree.tostring(lm).decode(),)
    )
    with open(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}", "w") as manifest:
        manifest.write(raw_xml)


def fetch_dependencies(repo_path):
    """ Adds repos that are in the dependency file to the manifest, and syncs them. """
    syncable_repos = []
    verify_repos = []

    dependencies_path = f"{repo_path}/{DEPENDENCIES_FILE_NAME}"

    print(f"Looking for dependencies in {repo_path}")
    if os.path.exists(dependencies_path):
        with open(dependencies_path, "r") as dependencies_file:
            dependencies = json.loads(dependencies_file.read())
            fetch_list = []
            for dependency in dependencies:
                if not is_in_manifest(dependency["repository"], dependency["branch"]):
                    fetch_list.append(dependency)
                    syncable_repos.append(dependency["target_path"])
                    verify_repos.append(dependency["target_path"])
                elif re.search("android_device_.*_.*$", dependency["repository"]):
                    verify_repos.append(dependency["target_path"])
            if fetch_list:
                print("Adding dependencies to manifest")
                add_to_manifest_dependencies(fetch_list)
    else:
        print("Dependencies file not found, bailing out.")

    if syncable_repos:
        print("Syncing dependencies")
        sync_command = f"repo sync --force-sync {' '.join(syncable_repos)}".split()
        subprocess.run(sync_command, check=False)

    for device_dependencies in verify_repos:
        fetch_dependencies(device_dependencies)


def get_repositories():
    """
    Retrieves all repositories from a project.
    If there is not an API token, then requests may be rate-limited.
    """
    page = 1
    repositories = []
    while True:
        request = urllib.request.Request(
            f"https://api.github.com/users/{ORGANIZATION_NAME}/repos?page={page:d}"
        )
        token_file = f"{os.getenv('HOME')}/api_token"
        if os.path.isfile(token_file):
            with open(token_file, "r") as file_contents:
                token = file_contents.readline()
                request.add_header("Authorization", f"token {token.strip()}")

        response = urllib.request.urlopen(request).read().decode()
        result = json.loads(response)

        if result:
            repositories += result
            page += 1
        else:
            break
    return repositories


def main():
    """
    Entry point. Creates a local manifest directory and/or local manifest file if there isn't one,
    then creates a local manifest and repo syncs.
     """

    if not os.path.isdir(LOCAL_MANIFESTS_PATH):
        os.makedirs(LOCAL_MANIFESTS_PATH)
    if not os.path.isfile(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}"):
        with open(f"{LOCAL_MANIFESTS_PATH}{LOCAL_MANIFESTS_FILE_NAME}", "w"):
            pass

    print(
        f"Device {DEVICE} not found. "
        f"Attempting to retrieve device repository from {ORGANIZATION_NAME} "
        f"Github (http://github.com/{ORGANIZATION_NAME})."
    )

    for repository in get_repositories():
        repository_name = repository["name"]
        if repository_name.startswith("android_device_") and repository_name.endswith(
            "_" + DEVICE
        ):
            print(f"Found repository: {repository['name']}")
            manufacturer = repository_name.replace("android_device_", "").replace(
                "_{0}".format(DEVICE), ""
            )
            repository_path = f"device/{manufacturer}/{DEVICE}"
            add_to_manifest(
                [
                    {
                        "repository": repository_name,
                        "target_path": repository_path,
                        "branch": BRANCH,
                    }
                ]
            )
            print("Syncing repository to retrieve project.")
            subprocess.run(
                ["repo", "sync", "--force-sync", "--no-tag", "--no-clone-bundle", repository_path], check=False,
            )

            fetch_dependencies(repository_path)
            print("Done")
main()
