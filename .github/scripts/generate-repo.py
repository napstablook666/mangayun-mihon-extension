#!/usr/bin/env python3
"""Build the Mihon extension-repository metadata for a release."""

from __future__ import annotations

import argparse
import json
import re
from pathlib import Path


CONTENT_WARNINGS = {
    0: "CONTENT_WARNING_UNSPECIFIED",
    1: "CONTENT_WARNING_SAFE",
    2: "CONTENT_WARNING_MIXED",
    3: "CONTENT_WARNING_NSFW",
}

CONTENT_WARNING_SAFE = 1


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-info", type=Path, required=True)
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--tag", required=True)
    parser.add_argument("--repository", required=True)
    parser.add_argument("--signing-key", required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--legacy-index", type=Path, help="also write a Keiyoushi-style index.min.json")
    return parser.parse_args()


def build_legacy_index(source_info: dict, apk_name: str) -> list[dict]:
    """Keiyoushi-style index.min.json entries, read by Mihon's legacy store URL check."""
    return [
        {
            "name": source_info["name"],
            "pkg": source_info["packageName"],
            "apk": apk_name,
            "lang": source_info["sources"][0]["lang"],
            "code": source_info["versionCode"],
            "version": source_info["versionName"],
            "nsfw": 0 if source_info["contentWarning"] == CONTENT_WARNING_SAFE else 1,
            "sources": [
                {
                    "id": source["id"],
                    "lang": source["lang"],
                    "name": source["name"],
                    "baseUrl": source["baseUrl"],
                }
                for source in source_info["sources"]
            ],
        }
    ]


def main() -> None:
    args = parse_args()
    source_info = json.loads(args.source_info.read_text(encoding="utf-8"))

    if not re.fullmatch(r"[0-9a-fA-F]{64}", args.signing_key):
        raise SystemExit("--signing-key must be a 64-character SHA-256 fingerprint")
    if not args.apk.name.endswith(".apk"):
        raise SystemExit("--apk must point to an APK")
    if not source_info["sources"]:
        raise SystemExit("source info must contain at least one source")

    release_base = (
        f"https://github.com/{args.repository}/releases/download/{args.tag}"
    )
    icon_url = (
        f"https://raw.githubusercontent.com/{args.repository}/{args.tag}/"
        "src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png"
    )
    extension = {
        "name": source_info["name"],
        "packageName": source_info["packageName"],
        "resources": {
            "apkUrl": f"{release_base}/{args.apk.name}",
            "iconUrl": icon_url,
        },
        "extensionLib": source_info["extensionLib"],
        "versionCode": source_info["versionCode"],
        "versionName": source_info["versionName"],
        "contentWarning": CONTENT_WARNINGS[source_info["contentWarning"]],
        "sources": [
            {
                "id": source["id"],
                "name": source["name"],
                "language": source["lang"],
                "homeUrl": source["baseUrl"],
                "mirrorUrls": source.get("mirrorUrls", []),
            }
            for source in source_info["sources"]
        ],
    }
    repository = {
        "name": "MangaYun",
        "badgeLabel": "MANGA",
        "signingKey": args.signing_key.lower(),
        "contact": {
            "website": f"https://github.com/{args.repository}",
            "discord": None,
        },
        "extensionList": {"extensions": [extension]},
        "extensionListUrl": None,
    }

    args.output.write_text(
        json.dumps(repository, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    if args.legacy_index is not None:
        args.legacy_index.write_text(
            json.dumps(build_legacy_index(source_info, args.apk.name), ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )


if __name__ == "__main__":
    main()
