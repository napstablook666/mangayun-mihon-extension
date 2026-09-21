<p align="center">
  <img src="./src/zh/mangayun/res/mipmap-xxxhdpi/ic_launcher.png" alt="云漫图标" width="96">
</p>

<h1 align="center">MangaYun</h1>

<p align="center">通过签名云 API 聚合多个中文漫画站的 Mihon 扩展。</p>

<p align="center">
  <a href="./README.md">English</a> | <a href="./README.zh-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/releases"><img src="https://img.shields.io/github/v/release/napstablook666/mangayun-mihon-extension?style=for-the-badge&label=release" alt="GitHub release"></a>
  <a href="https://github.com/napstablook666/mangayun-mihon-extension/actions/workflows/release-mangayun.yml"><img src="https://img.shields.io/badge/JDK-21-3776AB?style=for-the-badge" alt="JDK 21"></a>
  <a href="./LICENSE"><img src="https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge" alt="MIT License"></a>
  <a href="https://linux.do/"><img src="https://img.shields.io/badge/LINUX.DO-社区-2EA44F?style=for-the-badge" alt="LINUX DO"></a>
</p>

搜一次，翻十几个中文漫画站的结果，不用每个站单独装一个扩展。MangaYun 把云漫的聚合接口封装成标准 Mihon 扩展——请求走 HMAC-SHA256 签名，响应是 JSON，每本漫画和每个章节用源站 ID 加远端 URL 生成稳定标识，刷新不会漂移。

## 亮点

| 亮点 | 说明 |
|---|---|
| 多站聚合搜索 | 输入关键词一次，接口自动聚合多个中文漫画站的搜索结果 |
| 稳定标识 | 用源站点 ID + 远端 URL 生成唯一标识，刷新不漂移 |
| 签名 JSON API | 请求通过 HMAC-SHA256 签名，不依赖页面抓取或 HTML 解析 |
| arm64 专用构建 | 仅编译 arm64-v8a，APK 体积更小 |
| 基于 Keiyoushi 框架 | 兼容 Mihon 标准扩展仓库机制 |

## 截图

<p align="center">
  <img src="./docs/screenshots/search-bar.jpg" alt="MangaYun 搜索" width="720">
</p>

<p align="center">
  <em>搜索栏输入关键词，自动聚合多站结果。</em>
</p>

<p align="center">
  <img src="./docs/screenshots/search-results.jpg" alt="聚合搜索结果" width="360">
  <img src="./docs/screenshots/manga-detail.jpg" alt="漫画详情与章节列表" width="360">
</p>

<p align="center">
  <em>左：多站聚合结果。右：漫画详情与章节列表。</em>
</p>

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

## 自动发布

推送 `v*` 标签会触发 GitHub Actions 工作流，使用 JDK 21 构建 arm64 APK，同时打包源码归档，创建 Release，并自动清理仅保留最近 3 个 Release。

## 参与贡献

欢迎提交 PR。如有问题或建议，请提交 Issue 或直接发起 Pull Request。

> 本项目大部分内容由 AI（DeepSeek、GPT）辅助生成，并经过人工校调。

## 源码

扩展源码位于 `src/zh/mangayun/`。本项目基于 [Keiyoushi extensions-source](https://github.com/keiyoushi/extensions-source) 框架开发。

## 许可

[MIT](LICENSE) © 2026 napstablook666

---

<p align="center">
  <a href="https://linux.do/"><img src="https://ld.xh.do/ld-badge.svg" width="200" alt="LINUX DO"></a>
</p>

<p align="center">
  <a href="https://linux.do/">LINUX DO</a> — 新的理想型社区 · Where possible begins.
</p>