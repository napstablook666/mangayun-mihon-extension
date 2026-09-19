---
status: observed
scope: MangaYun Mihon/Komikku source URL parsing
date: 2026-09-19
---
规则：当扩展把多个远端站点的信息编码进 `SManga.url` 或 `SChapter.url` 时，保存为相对路径（例如 `/manga/<encoded-key>`）后，解码必须先取最后一个路径段，不能直接对完整路径做 Base64 解码。
适用 / 不适用：适用于 MangaYun 当前的 `/manga/` 与 `/chapter/` URL；如果 URL 格式改为单独的编码值或改用结构化参数，需要重新核对。
证据：`src/zh/mangayun/src/eu/kanade/tachiyomi/extension/zh/mangayun/MangaYun.kt`；Komikku 兼容性错误 `IllegalArgumentException: Invalid MangaYun source URL`；`:src:zh:mangayun:lintRelease` 与 `:src:zh:mangayun:assembleRelease` 均成功。
候选归宿: attention
