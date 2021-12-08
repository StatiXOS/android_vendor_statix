#!/usr/bin/env python
#
# Copyright 2021 StatiXOS
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
#

import json
import yaml
import sys

def main():
    """ Validates device statix.dependencies files to make sure nothing is missing when roomservice is run """
    if len(sys.argv) == 1:
        print("No dependencies file to validate!")
        return
    dependencies_file = sys.argv[1]
    try:
        dependencies = json.loads(open(dependencies_file, 'r').read())
    except json.decoder.JSONDecodeError:
        print("Invalid dependency file syntax! Make sure you don't have any commas at the end of your last dependency.")
        return
    for dependency in dependencies:
        if 'target_path' in dependency and 'repository' in dependency:
            print("Validated {}".format(dependency['target_path']))
            suggest_edits(dependency)
        elif 'target_path' not in dependency and 'repository' in dependency:
            print("Define target_path for dependency {}".format(dependency['repository']))
            return
        elif 'repository' not in dependency and 'target_path' in dependency:
            print("Define repository for dependency {}".format(dependency['target_path']))
            return
        else:
            print("Invalid format, missing repository and target_path for dependency {}".format(dependencies.index(dependency)))
            return
    convert_to_yaml(dependencies)

def suggest_edits(dependency):
    """ Suggests edits inline with roomservice expectations """
    if dependency['repository'].startswith("StatiXOS"):
        print("For dependency {}, consider setting remote as 'statix' and removing the StatiXOS/ prefix".format(dependency['repository']))
    if 'revision' in dependency:
        print("For dependency {}, change 'revision' to 'branch'".format(dependency['repository']))

def convert_to_yaml(dependencies):
    """ Converts dictionary to yaml and writes to dependencies.yml"""
    f = open("dependencies.yml", 'w')
    dep_dict = {}
    dep_dict["projects"] = dependencies
    yaml.dump(dep_dict, f)


if __name__ == '__main__':
    main()
