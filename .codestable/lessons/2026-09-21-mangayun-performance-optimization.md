# MangaYun Extension Performance Optimization Patterns

日期: 2026-09-21
标签: mangayun, performance, networking, okhttp

## 背景

在 MangaYun 扩展开发中，为提升图片下载速度与稳定性，系统性地应用了 Keiyoushi 框架内置的网络优化基础设施。

## 教训 / 发现

### Keiyoushi 内置基础设施

Keiyoushi 框架已内置丰富的网络优化组件，扩展无需自行实现，只需在对应方法中配置：

- **ConnectionPool** — 连接池复用，减少 TCP 握手开销
- **Dispatcher** — 控制并发请求数
- **CacheControlInterceptor** — 响应缓存控制
- **RandomUA** — 随机 User-Agent 防封禁
- **RateLimitInterceptor** — 速率限制（滑动窗口）
- **ImageRetryInterceptor** — 图片请求自动重试（仅对 IOException 和 5xx）

### 关键配置

- **connectionPool(8, 60s) + dispatcher.maxRequestsPerHost = 8**：显著提升图片下载并行度，适合多章节批量缓存场景
- **ImageRetryInterceptor**：对图片请求（检测 `Accept: image/` 头）自动重试最多 3 次，提升不稳定 CDN 下的成功率。注意配置时机——必须在 `configureClient()` 中 `addInterceptor(ImageRetryInterceptor())`，而非 `configureHeaders()` 或 `imageRequest()` 中
- **动态 Referer**：从图片 URL 提取 host 拼接 Referer 头（`imageUrl.host`），而非硬编码 `baseUrl`，避免图片 CDN 因 Referer 不匹配而拒绝请求
- **随机 UA（Mobile 类型）**：降低 CDN / CloudFront 按 UA 封禁的概率

### 用户可配置性

- **baseUrl { custom("...") }**：允许用户在 Mihon 设置中填入自定义代理 / CDN 地址，零代码改动即可切换镜像或加速节点

## 涉及的代码位置

- `src/zh/mangayun/src/.../MangaYun.kt` — `configureClient()`, `configureHeaders()`, `imageRequest()` 方法

## 参考

- Keiyoushi 框架 `ConnectionPool`, `Dispatcher`, `CacheControlInterceptor`, `RandomUA`, `RateLimitInterceptor`, `ImageRetryInterceptor` 等类