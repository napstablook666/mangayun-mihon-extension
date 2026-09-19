<p align="center">
  <img src="./src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png" alt="MangaYun official icon" width="96">
  <h1 align="center">MangaYun Mihon / Komikku Extension</h1>
</p>

<p align="center">Chinese manga aggregator extension for Mihon and Komikku.</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release-mangayun.yml"><img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK 21"></a>
  <a href="https://img.shields.io/badge/arch-arm64--v8a-green"><img src="https://img.shields.io/badge/arch-arm64--v8a-green" alt="arm64-v8a"></a>
</p>

MangaYun is a source extension for [Mihon](https://github.com/mihonapp/mihon) and [Komikku](https://github.com/komikku-app/komikku) that aggregates manga from multiple Chinese sites through the [YunMan](https://mangayun.com) cloud API. Search once, browse results from all available sites in one place.

## Highlights

| Highlight | Why it matters |
|---|---|
| Multi-site search | Search once and get results aggregated from multiple Chinese manga sites |
| Stable identifiers | Each manga and chapter uses the source site ID + remote URL to generate consistent keys across refreshes |
| Signed JSON API | Requests are HMAC-SHA256 signed — no scraping, no HTML parsing |
| arm64-only build | Optimized binary size for modern Android devices |
| Keiyoushi-based | Built on the Keiyoushi extension framework, compatible with standard Mihon/Komikku extension repos |

## Architecture

```text
┌──────────────────┐     Extension API     ┌──────────────────────────────┐
│   Mihon /        │◀─────────────────────▶│     MangaYun Extension       │
│   Komikku        │                       │  ┌────────────────────────┐  │
│   (Reader App)   │                       │  │  MangaYun.kt           │  │
└──────────────────┘                       │  │  (KeiSource impl)      │  │
                                            │  └──────────┬─────────────┘  │
                                            │             │                │
                                            │  ┌──────────▼─────────────┐  │
                                            │  │  MangaYunApi.kt        │  │
                                            │  │  (Signed JSON Client)  │  │
                                            │  └──────────┬─────────────┘  │
                                            └─────────────┼────────────────┘
                                                          │ POST /api/*
                                                          │ HMAC-SHA256
                                                          ▼
                                            ┌──────────────────────────────┐
                                            │     mangayun.com            │
                                            │     云漫聚合 API              │
                                            │                              │
                                            │  ┌───┐ ┌───┐ ┌───┐ ┌───┐  │
                                            │  │ S1│ │ S2│ │ S3│ │ … │  │
                                            │  └───┘ └───┘ └───┘ └───┘  │
                                            └──────────────────────────────┘
```

## Usage example

Add the MangaYun extension repository to Mihon or Komikku, install the extension, and search for manga:

1. Open the app → **Settings** → **Extensions** → **Add repository**
2. Enter the repository URL:
   ```
   https://github.com/napstablook666/mangayun-mihon-extension/releases
   ```
3. Go to **Extensions**, find **MangaYun**, and tap **Install**
4. Open **MangaYun** from the extensions list and search for any manga

Results from all aggregated sites appear together in the search list. Each result carries its source site label, and reading progress is saved locally by the app.

## Quick Install

### Via extension repository

Add the repository URL above in Mihon or Komikku, then install MangaYun from the extensions list.

### Build from source

**Requirements:** JDK 21, Android SDK.

```bash
./gradlew :src:zh:mangayun:lintRelease :src:zh:mangayun:assembleRelease --no-daemon
```

The APK is output to:

```
src/zh/mangayun/build/outputs/apk/release/
```

Extension version is derived from `libVersion` and `versionCode` in `src/zh/mangayun/build.gradle.kts`. Current version: `1.6.4`.

## Quick Start

After installing the extension:

1. Open **Extensions** in Mihon or Komikku
2. Tap **MangaYun**
3. Enter a manga title in the search bar
4. Browse the aggregated results — each entry shows which source site it comes from
5. Tap a result to view details and chapters

That's it. Reading progress syncs through the app's local storage.

## Automatic releases

Pushing a `v*` tag triggers a GitHub Actions workflow that builds the arm64 APK with JDK 21, creates a GitHub Release with the APK and source archive, and keeps only the latest 3 releases.

## Source code

The extension source lives in `src/zh/mangayun/`. This project builds on the [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) framework.

## License

This repository does not include a standalone license file. Please review the applicable terms of upstream projects and related services before use, modification, or redistribution.