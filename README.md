<p align="center">
  <img src="./src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png" alt="MangaYun icon" width="96">
  <h1 align="center">MangaYun Mihon Extension</h1>
</p>

<p align="center">Aggregated Chinese manga source extension for <a href="https://github.com/mihonapp/mihon">Mihon</a>.</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release-mangayun.yml"><img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK 21"></a>
  <a href="https://img.shields.io/badge/arch-arm64--v8a-green"><img src="https://img.shields.io/badge/arch-arm64--v8a-green" alt="arm64-v8a"></a>
  <a href="https://linux.do/"><img src="https://img.shields.io/badge/LINUX.DO-社区-2EA44F" alt="LINUX DO"></a>
</p>

MangaYun is a Mihon extension that aggregates manga from multiple Chinese sites through the [YunMan](https://mangayun.com) cloud API. Search once, browse results from all available sites in one place.

## Highlights

| Highlight | Why it matters |
|---|---|
| Multi-site search | Search once and get results aggregated from multiple Chinese manga sites |
| Stable identifiers | Each manga and chapter uses the source site ID + remote URL to generate consistent keys across refreshes |
| Signed JSON API | Requests are HMAC-SHA256 signed — no scraping, no HTML parsing |
| arm64-only build | Optimized binary size for modern Android devices |
| Keiyoushi-based | Built on the Keiyoushi extension framework, compatible with standard Mihon extension repos |

## Architecture

```text
┌──────────────────┐     Extension API     ┌──────────────────────────────┐
│   Mihon          │◀─────────────────────▶│     MangaYun Extension       │
│   (Reader App)   │                       │  ┌────────────────────────┐  │
└──────────────────┘                       │  │  MangaYun.kt           │  │
                                            │  │  (KeiSource impl)      │  │
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

## Quick Install

### Via extension repository

Add the following repository URL in Mihon → **Settings** → **Extensions** → **Add repository**:

```
https://github.com/napstablook666/mangayun-mihon-extension/releases
```

Then find **MangaYun** in the extensions list and tap **Install**.

### Build from source

**Requirements:** JDK 21, Android SDK.

```bash
./gradlew :src:zh:mangayun:lintRelease :src:zh:mangayun:assembleRelease --no-daemon
```

APK output: `src/zh/mangayun/build/outputs/apk/release/`

Current version: `1.6.7` (see `src/zh/mangayun/build.gradle.kts`).

## Usage

1. Open **Extensions** in Mihon
2. Tap **MangaYun**
3. Enter a manga title in the search bar
4. Browse the aggregated results — each entry shows which source site it comes from
5. Tap a result to view details and chapters

Reading progress is saved locally by Mihon.

## Recommend: Komikku

If you prefer a dedicated manga reader, check out [Komikku](https://github.com/komikku-app/komikku) — a community-driven manga reading app that also supports Mihon extensions. It offers a clean reading experience with the same extension ecosystem.

## Automatic releases

Pushing a `v*` tag triggers a GitHub Actions workflow that builds the arm64 APK with JDK 21, creates a GitHub Release with the APK and source archive, and keeps only the latest 3 releases.

## Contributing

PRs are welcome! Feel free to open issues or submit pull requests for improvements.

> This project was largely generated with the assistance of AI (DeepSeek, GPT) and manually refined.

## Source code

The extension source lives in `src/zh/mangayun/`. This project builds on the [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) framework.

## License

[MIT](LICENSE) © 2026 napstablook666

---

<p align="center">
  <a href="https://linux.do/"><img src="https://ld.xh.do/ld-badge.svg" width="200" alt="认可 LINUX DO"></a>
</p>
<p align="center">
  <a href="https://linux.do/">LINUX DO</a> — 新的理想型社区 · Where possible begins.
</p>