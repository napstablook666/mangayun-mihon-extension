<p align="center">
  <h1 align="center">MangaYun Mihon / Komikku 扩展</h1>
</p>

<p align="center">Mihon / Komikku 的中文漫画聚合源。</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release.yml"><img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK 21"></a>
  <a href="https://img.shields.io/badge/arch-arm64--v8a-green"><img src="https://img.shields.io/badge/arch-arm64--v8a-green" alt="arm64-v8a"></a>
</p>

MangaYun 是基于[云漫](https://mangayun.com)聚合接口的漫画源扩展，适用于 [Mihon](https://github.com/mihonapp/mihon) 和 [Komikku](https://github.com/komikku-app/komikku)。一次搜索即可浏览多个中文漫画站点的结果。

## 亮点

| 亮点 | 说明 |
|---|---|
| 多站聚合搜索 | 输入关键词一次，接口自动聚合多个中文漫画站的搜索结果 |
| 稳定标识 | 使用源站点 ID + 远端 URL 生成漫画和章节的唯一标识，刷新不会漂移 |
| 签名 JSON API | 请求通过 HMAC-SHA256 签名，不依赖页面抓取或 HTML 解析 |
| arm64 专用构建 | 仅编译 arm64-v8a，减少 APK 体积 |
| 基于 Keiyoushi 框架 | 兼容 Mihon/Komikku 的标准扩展仓库机制 |

## 架构

```text
┌──────────────────┐     Extension API     ┌──────────────────────────────┐
│   Mihon /        │◀─────────────────────▶│     MangaYun 扩展             │
│   Komikku        │                       │  ┌────────────────────────┐  │
│   (阅读器)       │                       │  │  MangaYun.kt           │  │
└──────────────────┘                       │  │  (KeiSource 实现)      │  │
                                            │  └──────────┬─────────────┘  │
                                            │             │                │
                                            │  ┌──────────▼─────────────┐  │
                                            │  │  MangaYunApi.kt        │  │
                                            │  │  (签名 JSON 客户端)    │  │
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
                                            │  │站点│ │站点│ │站点│ │…  │  │
                                            │  │ 1 │ │ 2 │ │ 3 │ │   │  │
                                            │  └───┘ └───┘ └───┘ └───┘  │
                                            └──────────────────────────────┘
```

## 使用示例

在 Mihon 或 Komikku 中添加 MangaYun 扩展仓库、安装扩展并搜索漫画：

1. 打开应用 → **设置** → **扩展** → **添加仓库**
2. 输入仓库 URL：
   ```
   https://github.com/napstablook666/mangayun-mihon-extension/releases
   ```
3. 在 **扩展** 列表中找到 **MangaYun**，点击 **安装**
4. 从扩展列表打开 **MangaYun**，搜索任何漫画

多个站点的聚合结果会一起显示在搜索结果中，每条结果标注来源站点。阅读进度由应用本地保存。

## 快速安装

### 通过扩展仓库

在上方添加仓库 URL 后，从扩展列表安装 MangaYun 即可。

### 从源码构建

**环境要求：** JDK 21、Android SDK。

```bash
./gradlew :src:zh:mangayun:lintRelease :src:zh:mangayun:assembleRelease --no-daemon
```

APK 输出路径：

```
src/zh/mangayun/build/outputs/apk/release/
```

扩展版本由 `src/zh/mangayun/build.gradle.kts` 中的 `libVersion` 和 `versionCode` 控制。当前版本：`1.6.3`。

## 快速上手

安装扩展后：

1. 在 Mihon 或 Komikku 中打开 **扩展**
2. 点击 **MangaYun**
3. 在搜索栏输入漫画名称
4. 浏览聚合结果——每条会标注来源站点
5. 点击结果查看详情和章节

阅读进度由应用自动在本地保存。

## 自动发布

推送 `v*` 标签会触发 GitHub Actions 工作流，使用 JDK 21 构建 arm64 APK，同时打包源码归档，创建 GitHub Release，并自动清理仅保留最新 3 个 Release。

## 源码

扩展源码位于 `src/zh/mangayun/`。本项目基于 [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) 框架开发。

## 许可

本仓库未包含独立许可证文件。使用、修改和再分发前请确认上游项目及相关服务的适用条款。