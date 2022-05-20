"""Creates a versioned manifest based on the current HEAD of every custom repo."""

import argparse
import xml.etree.ElementTree as Et
import os
import git

MANIFESTS = ["include.xml", "include-caf.xml"]
WORKING_DIR = f"{os.path.dirname(os.path.realpath(__file__))}/../../.."


def rewrite_manifest(tag: str):
    """Writes and commits a versioned manifest with the SHAs of each custom repo."""
    for manifest in MANIFESTS:
        manifest_file = f"{WORKING_DIR}/manifest/{manifest}"
        print(manifest_file)
        with open(manifest_file, "r", encoding="utf-8") as manifest:
            root = Et.parse(manifest).getroot()
        tree = Et.ElementTree(root)
        for repo in tree.findall("project"):
            cur_sha = git.Repo(repo.get("path")).head.object.hexsha
            repo.set("revision", cur_sha)
        Et.indent(tree, space="    ", level=0)
        tree.write(manifest_file)
    manifest_repo = git.Repo(f"{WORKING_DIR}/manifest")
    manifest_repo.index.add(MANIFESTS)
    manifest_repo.index.commit(f"Generate manifest for tag {tag}\n")


def main():
    """The runner."""
    parser = argparse.ArgumentParser(
        description="Tag and push custom repos before release"
    )
    parser.add_argument(
        "tag_name",
        metavar="tag",
        type=str,
        help="the tag name you want to use for this release",
    )
    args = parser.parse_args()
    rewrite_manifest(args.tag_name)
    print(f"generated versioned manifest for tag {args.tag_name}.")


if __name__ == "__main__":
    # execute only if run as a script
    main()
