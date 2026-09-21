<p align="center">
  <img src="./src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png" alt="MangaYun icon" width="96">
</p>

<h1 align="center">MangaYun</h1>

<p align="center">A Mihon extension that aggregates Chinese manga from multiple sites through a signed cloud API.</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?style=for-the-badge&label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release-mangayun.yml"><img src="https://img.shields.io/badge/JDK-21-3776AB?style=for-the-badge" alt="JDK 21"></a>
  <a href="./LICENSE"><img src="https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge" alt="MIT License"></a>
  <a href="https://linux.do/"><img src="https://img.shields.io/badge/LINUX.DO-社区-2EA44F?style=for-the-badge" alt="LINUX DO"></a>
</p>

Search once, browse results from a dozen Chinese manga sites without installing a separate source for each one. MangaYun wraps the YunMan cloud aggregation API into a standard Mihon extension — requests are HMAC-SHA256 signed, responses are JSON, and every manga and chapter gets a stable identifier that won't drift between refreshes.

## Highlights

| Highlight | Why it matters |
|---|---|
| Multi-site search | One query returns aggregated results from multiple Chinese manga sources |
| Stable identifiers | Each manga and chapter uses the source site ID plus remote URL — keys stay consistent across refreshes |
| Signed JSON API | HMAC-SHA256 signed requests, no scraping, no HTML parsing |
| arm64-only build | Smaller APK, targets modern Android devices |
| Keiyoushi-based | Built on the standard Mihon extension framework, compatible with any Mihon extension repo |

## Screenshots

<p align="center">
  <img src="./docs/screenshots/search-bar.jpg" alt="Search in MangaYun" width="720">
</p>

<p align="center">
  <em>Search results aggregated from multiple Chinese manga sources.</em>
</p>

<p align="center">
  <img src="./docs/screenshots/search-results.jpg" alt="Aggregated search results" width="360">
  <img src="./docs/screenshots/manga-detail.jpg" alt="Manga detail and chapter list" width="360">
</p>

<p align="center">
  <em>Left: aggregated results from multiple sites. Right: manga details with chapter list.</em>
</p>

## Quick Install

### Via extension repository

Add the repository URL in Mihon → **Settings** → **Extensions** → **Add repository**:

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

## Automatic releases

Pushing a `v*` tag triggers a GitHub Actions workflow that builds the arm64 APK with JDK 21, creates a release with the APK and source archive, and keeps only the latest 3 releases.

## Contributing

PRs are welcome. Open an issue or submit a pull request for improvements.

> This project was largely generated with the assistance of AI (DeepSeek, GPT) and manually refined.

## Source code

The extension source lives in `src/zh/mangayun/`. This project builds on the [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) framework.

## License

[MIT](LICENSE) © 2026 napstablook666

---

<p align="center">
  <a href="https://linux.do/"><img src="https://ld.xh.do/ld-badge.svg" width="200" alt="LINUX DO"></a>
</p>

<p align="center">
  <a href="https://linux.do/">LINUX DO</a> — 新的理想型社区 · Where possible begins.
</p>