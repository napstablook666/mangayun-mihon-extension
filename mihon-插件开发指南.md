# Mihon 插件开发指南

> 这里的“插件”指 Mihon 的内容源扩展，也就是从漫画、图书或其他内容站点获取目录、详情、章节和图片的 Android Extension。
>
> Mihon 官方站点主要提供安装和使用文档，较完整的扩展开发资料分布在 Mihon 的源码、`extensions-lib` 兼容库，以及 Keiyoushi 的扩展仓库贡献指南中。本文按当前推荐的 `KeiSource` / `libVersion = "1.6"` 方式整理。

## 1. 资料入口

| 用途 | 地址 |
| --- | --- |
| Mihon 主项目 | <https://github.com/mihonapp/mihon> |
| Mihon 官方使用文档 | <https://mihon.app/docs/guides/getting-started> |
| 扩展编译兼容库 | <https://github.com/mihonapp/extensions-lib> |
| 扩展 API 实现参考 | <https://github.com/mihonapp/mihon/tree/main/app/src/main/java/eu/kanade/tachiyomi/source> |
| Keiyoushi 扩展源码仓库 | <https://github.com/keiyoushi/extensions-source> |
| 开发与贡献指南 | <https://github.com/keiyoushi/extensions-source/blob/main/CONTRIBUTING.md> |
| Keiyoushi 扩展仓库文档站 | <https://keiyoushi.github.io/docs/> |

### 资料之间的关系

- `mihon` 是宿主应用，包含扩展加载、网络、缓存、阅读器和数据库逻辑。
- `extensions-lib` 提供扩展编译时需要的接口和模型存根。扩展运行时使用 Mihon 宿主提供的实际实现。
- `extensions-source` 是目前最完整的扩展示例和开发规范集合。
- `Keiyoushi` 是扩展社区仓库，不属于 Mihon 官方项目。提交扩展时应遵守目标仓库自己的规则。

## 2. 开发前提

需要具备以下基础知识

- Kotlin
- Android Studio 和 Android 项目构建
- Gradle 和 Kotlin DSL
- HTML、CSS 选择器和网页调试
- OkHttp
- Jsoup
- 基本的 JSON、序列化和 HTTP 调试能力

推荐工具

- Android Studio
- 一台启用开发者选项的 Android 设备或模拟器
- 已安装 Mihon 的测试版本
- Chrome DevTools 或其他网页开发者工具
- `try.jsoup.org`，用于快速验证 CSS 选择器
- `mitmweb` 或其他 HTTP 代理工具，用于排查请求

## 3. 扩展项目结构

Keiyoushi 仓库的单个扩展通常放在

```text
src/<lang>/<source-name>/
├── build.gradle.kts
├── res/
│   └── mipmap-*/ic_launcher.png
└── src/
    └── eu/kanade/tachiyomi/extension/<lang>/<source-name>/
        ├── MySource.kt
        ├── Dto.kt       # 可选
        └── Filters.kt   # 可选
```

约定

- `<lang>` 使用 ISO 639-1 两字母语言代码，或使用 `all`。
- `<source-name>` 只能使用小写 ASCII 字母和数字。
- Kotlin 包名必须为 `eu.kanade.tachiyomi.extension.<lang>.<source-name>`。
- `Dto.kt` 和 `Filters.kt` 等辅助文件不需要重复扩展名。
- 扩展图标应为带圆角的正方形图标。

### 用脚本创建模块

在 `extensions-source` 根目录执行

```bash
python ext-bootstrap.py \
  -n "My Source" \
  -l en \
  -u https://example.com
```

常用参数

| 参数 | 作用 |
| --- | --- |
| `-n`, `--extname` | 扩展名称 |
| `-l`, `--lang` | 语言代码或 `all` |
| `-u`, `--baseurl` | HTTPS 基础地址 |
| `--source-name` | Mihon 中显示的源名称 |
| `-c`, `--content-warning` | `SAFE`、`MIXED` 或 `NSFW` |
| `-m`, `--multisrc` | 继承已有多源主题 |
| `--path` | 扩展仓库路径 |

仓库很大时，可以使用部分克隆和稀疏检出

```bash
git clone --filter=blob:none --sparse <fork-repo-url>
cd extensions-source
git sparse-checkout set --cone --sparse-index
git sparse-checkout add common compiler core gradle lib lib-multisrc
git sparse-checkout add src/<lang>/<source-name>
```

## 4. Gradle 配置

当前推荐的最小配置如下

```kotlin
import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "My Source"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        lang = "en"
        baseUrl = "https://example.com"
    }
}
```

关键字段

| 字段 | 说明 |
| --- | --- |
| `name` | 扩展名称，非英文站点通常填写罗马化名称 |
| `versionCode` | 正整数。用户可见的代码变更都应递增 |
| `contentWarning` | `SAFE`、`MIXED` 或 `NSFW` |
| `libVersion` | 新扩展使用 `"1.6"` |
| `source {}` | 至少声明一个源 |
| `source.name` | 源名称，默认继承扩展名称 |
| `source.lang` | 源语言 |
| `source.baseUrl` | 源站基础地址 |
| `source.id` | 可选。重命名或改语言时用于保持用户书架关联 |
| `source.versionId` | 可选。只有 URL 结构发生根本变化且无法重定向时才递增 |

扩展版本名会由 `libVersion` 和计算后的版本号组合生成。例如 `1.6` 加版本码 `1` 会生成 `1.6.1`。

### 多镜像和自定义地址

单一地址

```kotlin
source {
    lang = "en"
    baseUrl = "https://example.com"
}
```

镜像地址

```kotlin
source {
    lang = "en"
    baseUrl {
        mirrors(
            "https://example.com",
            "https://mirror.example.com",
        )
    }
}
```

带显示名称的镜像

```kotlin
source {
    lang = "en"
    baseUrl {
        mirrors(
            "Main" to "https://example.com",
            "Mirror" to "https://mirror.example.com",
        )
    }
}
```

用户可填写任意地址

```kotlin
source {
    lang = "en"
    baseUrl {
        custom("https://example.com")
    }
}
```

`mirrors` 和 `custom` 会自动生成设置页面及偏好存储。不要再手写 `SharedPreferences`、`ListPreference` 或镜像切换逻辑，否则会出现重复设置项。

## 5. 源类和入口

新扩展应使用 `KeiSource`，不要直接继承旧的 `HttpSource`

```kotlin
package eu.kanade.tachiyomi.extension.en.mysource

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SMangaUpdate
import keiyoushi.annotation.Source
import keiyoushi.source.KeiSource

@Source
abstract class MySource : KeiSource() {
    override suspend fun getPopularManga(page: Int): MangasPage {
        TODO("实现热门列表")
    }

    override suspend fun getLatestUpdates(page: Int): MangasPage {
        TODO("实现最新列表")
    }

    override suspend fun getSearchMangaList(
        page: Int,
        query: String,
        filters: FilterList,
    ): MangasPage {
        TODO("实现搜索")
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        TODO("实现详情和章节")
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        TODO("实现页面列表")
    }
}
```

`@Source` 配合 Gradle 中的 `source {}` 使用。构建系统会生成入口类，并注入以下属性

- `name`
- `lang`
- `id`
- `baseUrl`

因此不要在 Kotlin 源类中重复声明或覆盖这些属性。

### 旧 API 和新 API

| API | 状态 |
| --- | --- |
| `KeiSource` | 新扩展推荐使用 |
| `HttpSource` | 底层 HTTP 源接口，旧扩展常直接继承 |
| `ParsedHttpSource` | 已废弃 |
| `SourceFactory` | 新的多源场景由多个 `source {}` 自动生成 |

一个类可以通过多个 `source {}` 声明暴露多个语言或镜像源，通常不需要自己实现 `SourceFactory`。

## 6. 必须实现的业务方法

### 热门列表

```kotlin
suspend fun getPopularManga(page: Int): MangasPage
```

应用会从 `page = 1` 开始分页调用。列表项至少要有

- `SManga.url`
- `SManga.title`
- `SManga.thumbnail_url`

`hasNextPage` 应准确表示是否还有下一页。没有下一页时返回 `false`，不要只依赖列表是否为空。

### 最新列表

```kotlin
suspend fun getLatestUpdates(page: Int): MangasPage
```

如果站点没有独立的最新列表，可以将

```kotlin
override val supportsLatest = false
```

### 搜索

```kotlin
suspend fun getSearchMangaList(
    page: Int,
    query: String,
    filters: FilterList,
): MangasPage
```

普通文本和筛选条件会进入这个方法。URL 搜索会由 `KeiSource` 路由到 `getMangaByUrl` 或 `getMangasByUrl`，不需要在普通搜索方法中重复处理。

没有搜索功能时返回

```kotlin
MangasPage(emptyList(), false)
```

### 详情和章节

```kotlin
suspend fun fetchMangaUpdate(
    manga: SManga,
    chapters: List<SChapter>,
    fetchDetails: Boolean,
    fetchChapters: Boolean,
): SMangaUpdate
```

这里可以返回更新后的 `SManga`、章节列表，或两者同时返回。

- `fetchDetails` 表示是否需要详情。
- `fetchChapters` 表示是否需要章节。
- 如果详情和章节来自同一个响应，可以一次请求后同时返回，不必为了严格区分两个标志而重复请求。
- 章节列表应按源站顺序倒序排列，通常最新章节在前。
- `SManga.title` 和 `SManga.url` 是必填字段。
- `SChapter.name` 是必填字段。
- 不要用 `"Untitled"`、`"Unknown"` 或空字符串掩盖站点缺失数据。

### 页面列表

```kotlin
suspend fun getPageList(chapter: SChapter): List<Page>
```

在这个方法中请求并解析完整页面列表

```kotlin
return document.select(".pages img").mapIndexed { index, image ->
    Page(index, imageUrl = image.attr("abs:src"))
}
```

注意

- 页面列表顺序必须正确，`Page.index` 本身不会替你排序。
- 如果图片地址已知，直接填写 `imageUrl`。
- 如果必须打开章节页或调用接口后才能得到图片地址，可以先返回空的 `imageUrl`，再覆盖 `getImageUrl`。
- 没有页面时返回 `emptyList()`，不要抛出写死的异常。

## 7. 推荐的调用流程

```text
热门入口       -> getPopularManga(page)
最新入口       -> getLatestUpdates(page)
文本搜索       -> getSearchMangaList(page, query, filters)
URL 搜索       -> getMangaByUrl(url) / getMangasByUrl(url, page)
打开详情       -> fetchMangaUpdate(..., fetchDetails = true, ...)
刷新章节       -> fetchMangaUpdate(..., fetchChapters = true, ...)
打开章节       -> getPageList(chapter)
懒加载图片     -> getImageUrl(page)
打开网页       -> getMangaUrl(manga) / getChapterUrl(chapter)
```

默认情况下，网页地址由 `baseUrl + manga.url` 或 `baseUrl + chapter.url` 生成。如果 `url` 只保存了 ID 或 slug，需要覆盖对应方法

```kotlin
override fun getMangaUrl(manga: SManga): String =
    "$baseUrl/manga/${manga.url}"

override fun getChapterUrl(chapter: SChapter): String =
    "$baseUrl/chapter/${chapter.url}"
```

### 章节日期

`SChapter.date_upload` 使用 Unix Epoch 的毫秒值。解析失败时返回 `0L`，让应用使用默认日期。优先使用 `keiyoushi.utils` 的日期解析帮助函数，不要为新代码使用 `SimpleDateFormat`。

## 8. 网络请求和解析

### 使用挂起式 OkHttp 扩展

```kotlin
import keiyoushi.network.get

val response = client.get("$baseUrl/api/manga")
val document = response.asJsoup()
```

JSON 请求可以使用 `post`、`put` 等 `keiyoushi.network` 扩展。不要在 `suspend` 方法里手动调用 `client.newCall(request).execute()` 或自行阻塞线程。

### JSON DTO

优先定义 `@Serializable` DTO，再使用 `parseAs<T>()`

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import keiyoushi.utils.parseAs

@Serializable
class MangaDto(
    @SerialName("manga_id")
    val id: Int,
    val title: String,
    @SerialName("cover_img")
    val coverImage: String? = null,
)

val dto = response.parseAs<MangaDto>()
```

建议

- 只映射真正使用的字段。
- JSON 字段名和 Kotlin 属性名一致时不需要 `@SerialName`。
- 必填字段不要设置空值默认值，让解析尽早暴露数据变化。
- JSON 请求体使用 `toJsonRequestBody()`。
- 不要手动遍历 `JsonObject`、`JsonArray` 来替代 DTO。

### HTML 解析

```kotlin
val document = response.asJsoup()
val title = document.selectFirst("h1.title")?.text()
val cover = document.selectFirst("img.cover")?.absUrl("src")
```

如果 JSON 中包含 HTML 片段，使用

```kotlin
Jsoup.parseBodyFragment(html, baseUrl)
```

这样相对链接可以通过 `absUrl()` 正确解析。

### URL 保存方式

推荐把相对路径或去域名后的 URL 存在 `SManga.url`、`SChapter.url` 中，减少域名变更对本地数据的影响。需要存完整 URL 时，可以使用 `setUrlWithoutDomain` 等工具函数，并在 `getMangaUrl` 或 `getChapterUrl` 中还原。

### Cookie、WebView 和反爬

- Cookie 优先使用仓库提供的 `addCookie` 工具，不要手动覆盖整个 `Cookie` 请求头。
- 需要真实浏览器环境、JavaScript 挑战或读取 `localStorage` 时，使用 `runWebView`，不要手动创建 WebView。
- 只为解决实际站点需求添加复杂的解密、混淆或拦截逻辑。
- 图片解密、拼图等处理尽量使用流式读写，避免把大图全部加载进 `ByteArray`。
- 先检查 `lib/` 中是否已有可复用的库，再考虑自己实现。

## 9. DTO 到 Mihon 模型的映射

```kotlin
import eu.kanade.tachiyomi.source.model.SManga

fun MangaDto.toSManga(): SManga = SManga.create().apply {
    url = id.toString()
    title = this@toSManga.title
    thumbnail_url = coverImage
}
```

章节映射同理。常用字段包括

| 模型 | 字段 | 说明 |
| --- | --- | --- |
| `SManga` | `url` | 源内唯一标识，必填 |
| `SManga` | `title` | 标题，必填 |
| `SManga` | `thumbnail_url` | 封面地址 |
| `SManga` | `author`、`artist` | 作者和作画 |
| `SManga` | `description` | 简介 |
| `SManga` | `genres` | 类型列表 |
| `SManga` | `status` | `SManga.ONGOING`、`COMPLETED` 等 |
| `SChapter` | `url` | 章节地址或标识 |
| `SChapter` | `name` | 章节名称，必填 |
| `SChapter` | `number` | 新 API 的章节号字符串 |
| `SChapter` | `date_upload` | Unix Epoch 毫秒值 |
| `Page` | `index` | 列表位置，列表本身仍需正确排序 |
| `Page` | `imageUrl` | 图片地址，可延迟解析 |

备份会保存很多漫画字段。能在详情请求中得到的字段都应尽量填写，不要只填写标题和 URL。

## 10. 筛选器

筛选器在 `getFilterList()` 中声明，在 `getSearchMangaList()` 中读取状态

```kotlin
override fun getFilterList() = FilterList(
    Filter.Title("Genre"),
    Filter.Select("Status", arrayOf("All", "Ongoing", "Completed")),
)
```

实际类型和构造函数以当前 `extensions-lib` 版本为准。读取筛选器时，使用 `filter.state` 构建请求参数。对于自定义筛选器，可以继承 `Filter.Select`、`Filter.CheckBox`、`Filter.Text` 等已有类型。

如果筛选项需要联网动态获取，`KeiSource` 支持通过 `supportsFilterFetching` 和 `fetchFilterData()` 获取并缓存数据。`getFilterList(data)` 应保持同步且只负责把缓存数据转换成筛选器，不应在里面执行网络请求。

## 11. 高级能力

### 自定义设置

除镜像和自定义基础地址以外的设置，例如图片质量、语言或阅读模式，可以实现 `ConfigurableSource`，并使用

```kotlin
private val preferences by getPreferencesLazy()
```

设置值由 Android Preference 框架保存，一般不需要在监听器中手动写入 `SharedPreferences`。

### URL 深链接

在 `build.gradle.kts` 中声明 `deeplink {}`，用户从浏览器打开匹配 URL 时，Mihon 会启动并将 URL 作为搜索输入传给源。

```kotlin
keiyoushi {
    // 其他配置

    deeplink {
        host("example.com")
        path("/manga/..*")
    }
}
```

测试示例

```bash
adb shell am start \
  -d "https://example.com/manga/demo" \
  -a android.intent.action.VIEW
```

判断域名时优先和 `baseUrl` 动态比较，不要写死站点域名，这样才兼容镜像。

### 更新策略

默认是 `UpdateStrategy.ALWAYS_UPDATE`。对于上传后章节列表不会变化的画廊或一次性内容，可以设置

```kotlin
manga.update_strategy = UpdateStrategy.ONLY_FETCH_ONCE
```

这样会跳过后续全局更新检查，减少请求。

### 多源主题

如果多个站点使用相同 CMS，可以把公共逻辑放到 `lib-multisrc/<theme>`，然后在扩展中设置

```kotlin
keiyoushi {
    theme = "example-theme"
    // 其他配置
}
```

主题适合确实存在多个相似站点时使用。单个扩展不要为了复用几行代码提前创建主题模块。

### 保留源 ID

如果已经发布的源更改了名称或语言，自动计算的 ID 可能变化，用户现有书架会与新源断开。应在 `source {}` 中显式保留旧 ID

```kotlin
source {
    id = 123456789L
    name = "New Name"
    lang = "en"
    baseUrl = "https://example.com"
}
```

旧 ID 可以从扩展仓库的索引文件中查找。只有站点 URL 结构彻底改变且无法兼容时，才考虑增加 `versionId`。

## 12. 本地运行和调试

### 运行 Mihon

在 Android Studio 的 Mihon Debug 配置中添加启动参数

```text
-W -S -n app.mihon.dev/eu.kanade.tachiyomi.ui.main.MainActivity -a eu.kanade.tachiyomi.SHOW_CATALOGUES
```

包名对应关系

| 构建类型 | 包名 |
| --- | --- |
| Debug | `app.mihon.dev` |
| Release | `app.mihon` |
| Preview | `app.mihon.debug` |

Android 11 及以上设备建议在运行配置中启用 `Always install with package manager`，避免 Android Studio 继续安装旧版扩展。

### 日志

优先使用日志和 OkHttp 日志，而不是一开始就使用 Android Debugger。抓取站点时，重点检查

- 请求 URL 和查询参数
- HTTP 状态码
- `Referer`、`Origin`、`User-Agent` 和 Cookie
- 重定向后的地址
- HTML 实际结构是否已经改变
- JSON 字段是否缺失或改名

### 代理抓包

Android 模拟器访问宿主机通常使用 `10.0.2.2`。根据本机代理端口配置客户端

```kotlin
override fun OkHttpClient.Builder.configureClient(): OkHttpClient.Builder =
    proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("10.0.2.2", 8080)))
```

真机不能直接照搬 `10.0.2.2`，需要替换成电脑在局域网中的地址，并确保设备能够访问该端口。

## 13. 构建和检查

构建单个扩展

```bash
./gradlew src:<lang>:<source>:assembleDebug
```

运行 Release lint

```bash
./gradlew :src:<lang>:<source>:lintRelease
```

如果修改了公共库，还要直接检查对应模块

```bash
./gradlew :lib-multisrc:<theme>:lintRelease
./gradlew :lib:<name>:lintRelease
./gradlew :core:lintRelease
```

Android Studio 也可以通过以下菜单构建 APK

```text
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

## 14. 提交前检查清单

- [ ] `versionCode` 已递增
- [ ] `contentWarning` 设置正确
- [ ] `libVersion` 与使用的 API 一致
- [ ] `source {}` 的语言和 HTTPS 地址正确
- [ ] 没有意外改变已发布源的名称
- [ ] 如果修改名称或语言，已显式保留旧 `id`
- [ ] 热门、最新、搜索、详情、章节和页面流程均已实际测试
- [ ] 漫画标题、漫画 URL、章节名称不为空
- [ ] 分页的 `hasNextPage` 正确
- [ ] 章节顺序正确
- [ ] 日期使用毫秒值
- [ ] 图片地址能在下载和阅读器中访问
- [ ] 未重复实现已有的 `keiyoushi.utils` 或 `lib` 工具
- [ ] 已运行扩展模块的 `assembleDebug`
- [ ] 已运行相关模块的 `lintRelease`
- [ ] 已在真实 Mihon 或本地 Debug Mihon 中测试
- [ ] 图标符合仓库规范，并删除不需要的 `web_hi_res_512.png`
- [ ] Pull Request 中关联相关 issue

## 15. 常见错误

### 直接继承旧 `HttpSource`

新扩展应继承 `KeiSource` 并使用 `@Source`。直接继承 `HttpSource` 通常意味着沿用旧版 API，除非是在维护尚未迁移的旧扩展。

### 在源类中重复声明 `name`、`lang`、`id` 或 `baseUrl`

这些值由 `source {}` 和 KSP 生成代码负责。重复声明会造成构建错误或元数据不一致。

### 把完整网页地址硬编码到每个模型

优先保存相对路径或去域名后的路径，并集中在 `getMangaUrl`、`getChapterUrl` 中生成完整地址。

### 用空字符串掩盖解析失败

标题、ID、章节名称等关键字段缺失时应尽早暴露问题。静默返回空值会导致无法搜索、下载或恢复书架数据。

### 在 `getFilterList()` 中联网

动态筛选器应使用 `fetchFilterData()` 的后台获取和缓存机制。普通 `getFilterList()` 必须快速完成。

### 手动管理镜像偏好

`mirrors(...)` 和 `custom(...)` 已经自动处理设置界面、存储和迁移，不要再写一套 `SharedPreferences`。

### 没有测试分页和图片懒加载

列表能显示不代表下载可用。至少应测试下一页、详情刷新、章节排序、阅读器打开、图片下载和站点 Cookie 状态。

## 来源

- Mihon 官方使用文档，<https://mihon.app/docs/guides/getting-started>
- Mihon 主项目，<https://github.com/mihonapp/mihon>
- `extensions-lib`，<https://github.com/mihonapp/extensions-lib>
- Keiyoushi 扩展仓库，<https://github.com/keiyoushi/extensions-source>
- Keiyoushi 开发指南，<https://github.com/keiyoushi/extensions-source/blob/main/CONTRIBUTING.md>
