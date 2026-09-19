# MangaYun Mihon Extension

MangaYun 是面向 Mihon/Komikku 的中文漫画扩展，使用云漫聚合接口搜索漫画、获取详情、章节和图片。

## 功能

- 通过云漫搜索接口查询漫画
- 保留同名漫画在不同站点的独立结果
- 使用站点 ID 和远端 URL 生成稳定的漫画、章节标识
- 支持从云漫接口获取章节图片
- 构建产物仅包含 `arm64-v8a`

## 构建

环境要求：JDK 21。

```bash
./gradlew :src:zh:mangayun:lintRelease :src:zh:mangayun:assembleRelease --no-daemon
```

APK 位于：

```text
src/zh/mangayun/build/outputs/apk/release/
```

扩展版本由 `src/zh/mangayun/build.gradle.kts` 中的 `libVersion` 和 `versionCode` 生成。当前版本为 `1.6.3`。

## 自动发布

推送 `v*` 标签会触发 GitHub Actions，使用 JDK 21 构建并发布 arm64 APK，同时上传当前源码 `source.zip`。发布流程会删除较旧的 Release，仅保留最新 3 个 Release。阅读进度由 Komikku 本地记录。

## 源码

本项目基于 Keiyoushi extensions-source 的扩展结构开发，源代码位于 `src/zh/mangayun/`。

## 许可

本仓库未包含独立许可证文件。使用、修改和再分发前请确认上游项目及相关服务的适用条款。
