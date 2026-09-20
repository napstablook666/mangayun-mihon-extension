<p align="center">
  <img src="./src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png" alt="云漫图标" width="96">
  <h1 align="center">MangaYun Mihon 扩展</h1>
</p>

<p align="center">适用于 <a href="https://github.com/mihonapp/mihon">Mihon</a> 的中文漫画聚合源。</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release-mangayun.yml"><img src="https://img.shields.io/badge/JDK-21-blue" alt="JDK 21"></a>
  <a href="https://img.shields.io/badge/arch-arm64--v8a-green"><img src="https://img.shields.io/badge/arch-arm64--v8a-green" alt="arm64-v8a"></a>
  <a href="https://linux.do/"><img src="https://img.shields.io/badge/LINUX.DO-社区-2EA44F" alt="LINUX DO"></a>
</p>

MangaYun 是基于[云漫](https://mangayun.com)聚合接口的漫画源扩展，适用于 [Mihon](https://github.com/mihonapp/mihon)。一次搜索即可浏览多个中文漫画站点的结果。

## 亮点

| 亮点 | 说明 |
|---|---|
| 多站聚合搜索 | 输入关键词一次，接口自动聚合多个中文漫画站的搜索结果 |
| 稳定标识 | 使用源站点 ID + 远端 URL 生成漫画和章节的唯一标识，刷新不会漂移 |
| 签名 JSON API | 请求通过 HMAC-SHA256 签名，不依赖页面抓取或 HTML 解析 |
| arm64 专用构建 | 仅编译 arm64-v8a，减少 APK 体积 |
| 基于 Keiyoushi 框架 | 兼容 Mihon 的标准扩展仓库机制 |

## 架构

```text
┌──────────────────┐     Extension API     ┌──────────────────────────────┐
│   Mihon          │◀─────────────────────▶│     MangaYun 扩展             │
│   (阅读器)       │                       │  ┌────────────────────────┐  │
└──────────────────┘                       │  │  MangaYun.kt           │  │
                                            │  │  (KeiSource 实现)      │  │
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

## 快速安装

### 通过扩展仓库

在 Mihon → **设置** → **扩展** → **添加仓库** 中输入以下 URL：

```
https://github.com/napstablook666/mangayun-mihon-extension/releases
```

然后在扩展列表中找到 **MangaYun** 并安装。

### 从源码构建

**环境要求：** JDK 21、Android SDK。

```bash
./gradlew :src:zh:mangayun:lintRelease :src:zh:mangayun:assembleRelease --no-daemon
```

APK 输出路径：`src/zh/mangayun/build/outputs/apk/release/`

当前版本：`1.6.7`（见 `src/zh/mangayun/build.gradle.kts`）。

## 使用

1. 在 Mihon 中打开 **扩展**
2. 点击 **MangaYun**
3. 在搜索栏输入漫画名称
4. 浏览聚合结果——每条会标注来源站点
5. 点击结果查看详情和章节

阅读进度由 Mihon 本地保存。

## 推荐阅读器：Komikku

如果你更喜欢专用的漫画阅读器，推荐 [Komikku](https://github.com/komikku-app/komikku) —— 一款社区驱动的漫画阅读 App，同样支持 Mihon 扩展体系，提供清爽的阅读体验。

## 自动发布

推送 `v*` 标签会触发 GitHub Actions 工作流，使用 JDK 21 构建 arm64 APK，同时打包源码归档，创建 GitHub Release，并自动清理仅保留最新 3 个 Release。

## 参与贡献

欢迎提交 PR！如有问题或建议，请提交 Issue 或直接发起 Pull Request。

> 本项目大部分内容由 AI（DeepSeek、GPT）辅助生成，并经过人工校调。

## 源码

扩展源码位于 `src/zh/mangayun/`。本项目基于 [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) 框架开发。

## 许可

[MIT](LICENSE) © 2026 napstablook666

---

<p align="center">
  <a href="https://linux.do/"><img src="https://ld.xh.do/ld-badge.svg" width="200" alt="认可 LINUX DO"></a>
</p>
<p align="center">
  <a href="https://linux.do/">LINUX DO</a> — 新的理想型社区 · Where possible begins.
</p>