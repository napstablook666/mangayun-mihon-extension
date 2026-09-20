# CodeStable 项目注意事项
本文件记录每次会话需要优先确认的项目事实。

- 这是 Keiyoushi extensions-source 风格的 Mihon/Komikku 扩展仓库；新源继承 `KeiSource`，使用 `libVersion = "1.6"`，模块位于 `src/<lang>/<source>/`。
- MangaYun 扩展位于 `src/zh/mangayun/`，通过 `https://mangayun.com` 的签名 JSON API 搜索、获取详情、章节和图片。一个漫画的多个站点结果全部保留，站点 ID 随源 URL 传递。
- MangaYun 的 OkHttp client 仅对 `mangayun.com` 启用 `30 requests / 60 seconds` 的滑动窗口和 `1 second` 最小间隔；CDN 等其他 host 不受此规则影响。
- MangaYun 的 `SManga.url` / `SChapter.url` 是相对路径，分别为 `/manga/<encoded-key>` 和 `/chapter/<encoded-key>`；编码内容为 `siteId\nremoteUrl`。Komikku 详情刷新会传入完整相对路径，解析时必须只取最后一个路径段，具体纠偏记录见 `.codestable/lessons/2026-09-19-mangayun-url-key.md`。
- 本机 Gradle 验证使用 JDK 21：`JAVA_HOME='/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot' ./gradlew ...`。
- 发布基线：`src/zh/mangayun/build.gradle.kts` 使用 `versionCode = 8`、`libVersion = "1.6"`，仅编译 `arm64-v8a`；`.github/workflows/release-mangayun.yml` 在 `v*` 标签下用 JDK 21 构建，恢复正式签名密钥后上传 APK 与 `git archive` 生成的 `source.zip`，已存在的 Release 会替换 APK 并保留最新 3 个 Release。阅读进度由 Komikku 本地记录，不实现 LinuxDo 登录或云端进度同步。
- MangaYun 网络优化模式：Keiyoushi 内置 ConnectionPool(8, 60s)、Dispatcher(maxRequestsPerHost=8)、ImageRetryInterceptor（仅对图片请求重试 3 次）、动态 Referer（从图片 URL 提取 host）、RandomUA（Mobile 类型）等基础设施，扩展只需在 configureClient() / configureHeaders() / imageRequest() 中配置即可；baseUrl { custom("...") } 支持用户自定义代理/CDN，具体记录见 `.codestable/lessons/2026-09-21-mangayun-performance-optimization.md`。
