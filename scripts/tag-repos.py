import argparse
import git
import os
import sys
import xml.etree.ElementTree as Et

MANIFEST_NAME = "include-caf.xml"
WORKING_DIR = "{0}/../../..".format(os.path.dirname(os.path.realpath(__file__)))
GERRIT_URL = "ssh://{username}@review.statixos.com:29418/"

def read_custom_manifest() -> dict:
    """ Gets all repos to be tagged """
    repo_dict = {}
    with open("{0}/.repo/manifests/{1}".format(WORKING_DIR, MANIFEST_NAME)) as manifest:
        root = Et.parse(manifest).getroot()
        for custom in root:
            # we are skipping HAL/custom revision tagging for now
            if custom.get("remote") == "statix" and not custom.get("revision"):
                repo_dict[custom.get("name")] = custom.get("path")
    return repo_dict


def tag_repos(repos: dict, tag_name: str, url: str):
    for repo, path in repos.items():
        print(path)
        g_repo = git.Repo(path)
        tag = g_repo.create_tag(tag_name, message="tag: {}".format(tag_name))
        if "gerrit" not in g_repo.remotes:
            g_remote = g_repo.create_remote('gerrit', url + repo)
        g_remote.push(tag.path)


def rewrite_manifest(tag: str):
    manifest_file = "{0}/manifest/{1}".format(WORKING_DIR, MANIFEST_NAME)
    print(manifest_file)
    manifest = open(manifest_file, "r")
    root = Et.parse(manifest).getroot()
    manifest.close()
    stx_remote = root.find("./remote[@name='statix']")
    stx_remote.set("revision", "refs/tags/{}".format(tag))
    tree = Et.ElementTree(root)
    Et.indent(tree, space="    ", level=0)
    tree.write(manifest_file)
    manifest_repo = git.Repo("{0}/manifest".format(WORKING_DIR))
    manifest_repo.index.add([MANIFEST_NAME])
    manifest_repo.index.commit("Generate manifest for tag {}\n".format(tag))


def main():
    parser = argparse.ArgumentParser(description="Tag and push custom repos before release")
    parser.add_argument(
        "gerrit_username",
        metavar="username",
        type=str,
        help="your username on review.statixos.com"
    )
    parser.add_argument(
        "tag_name",
        metavar="tag",
        type=str,
        help="the tag name you want to use for this release"
    )
    args = parser.parse_args()
    formatted_gerrit_url = GERRIT_URL.format(username=args.gerrit_username)
    repo_dict = read_custom_manifest()
    #rewrite_manifest(args.tag_name)
    tag_repos(repo_dict, args.tag_name, formatted_gerrit_url)
    print("done.")

if __name__ == "__main__":
    # execute only if run as a script
    main()
