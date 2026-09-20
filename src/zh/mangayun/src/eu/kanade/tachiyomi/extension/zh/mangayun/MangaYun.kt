package eu.kanade.tachiyomi.extension.zh.mangayun

import android.util.Base64
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SMangaUpdate
import keiyoushi.annotation.Source
import keiyoushi.source.KeiSource
import keiyoushi.network.rateLimit
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

@Source
abstract class MangaYun : KeiSource() {

    override fun OkHttpClient.Builder.configureClient(): OkHttpClient.Builder = apply {
        rateLimit(permits = 30, period = 60.seconds, interval = 1.seconds) { it.host == "mangayun.com" }
    }

    private val api = MangaYunApi(this)

    override val supportsLatest = false
    override val supportsFilterFetching: Boolean get() = true

    private class SiteCheckBox(name: String, val siteId: String, state: Boolean = true) : Filter.CheckBox(name, state)
    override suspend fun fetchFilterData(): JsonElement {
        val sites = api.sites()
        return buildJsonArray {
            sites.forEach { site ->
                addJsonObject {
                    put("siteId", site.siteId)
                    site.siteName?.let { put("siteName", it) }
                }
            }
        }
    }

    override fun getFilterList(data: JsonElement?): FilterList {
        if (data == null || data is JsonNull) {
            return FilterList(listOf(Filter.Header("Tap Search to load sources")))
        }

        val arr = data.jsonArray
        if (arr.isEmpty()) return FilterList(listOf(Filter.Header("No sources available")))

        return FilterList(
            buildList {
                add(Filter.Header("Sources — uncheck to exclude"))
                arr.forEach { element ->
                    val obj = element.jsonObject
                    val siteId = obj["siteId"]!!.jsonPrimitive.content
                    val siteName = obj["siteName"]?.jsonPrimitive?.content ?: siteId
                    add(SiteCheckBox(siteName, siteId))
                }
            },
        )
    }
    override suspend fun getPopularManga(page: Int): MangasPage = MangasPage(emptyList(), false)

    override suspend fun getLatestUpdates(page: Int): MangasPage = MangasPage(emptyList(), false)

    override suspend fun getSearchMangaList(page: Int, query: String, filters: FilterList): MangasPage {
        if (page > 1 || query.isBlank()) return MangasPage(emptyList(), false)

        val results = api.search(query)

        val siteFilters = filters.filterIsInstance<SiteCheckBox>()
        val filteredResults = if (siteFilters.isNotEmpty()) {
            val checkedSiteIds = siteFilters
                .filter { it.state }
                .map { it.siteId }
                .toSet()
            if (checkedSiteIds.size < siteFilters.size) {
                results.filter { it.siteId in checkedSiteIds }
            } else {
                results
            }
        } else {
            results
        }

        return MangasPage(filteredResults.map { it.toSManga() }, false)
    }

    override suspend fun fetchMangaUpdate(
        manga: SManga,
        chapters: List<SChapter>,
        fetchDetails: Boolean,
        fetchChapters: Boolean,
    ): SMangaUpdate {
        val key = manga.url.decodeMangaKey()
        val details = if (fetchDetails || fetchChapters) {
            api.details(key.siteId, key.detailUrl)
        } else {
            null
        }
        val updatedManga = if (fetchDetails) details!!.toSManga(manga.url) else manga
        val updatedChapters = if (fetchChapters) {
            details!!.chapters
                .sortedByDescending { it.order }
                .map { it.toSChapter(key.siteId) }
        } else {
            chapters
        }
        return SMangaUpdate(updatedManga, updatedChapters)
    }

    override suspend fun getPageList(chapter: SChapter): List<Page> {
        val key = chapter.url.decodeChapterKey()
        return api.chapterImages(key.siteId, key.chapterUrl).mapIndexed { index, imageUrl ->
            Page(index, chapter.url, imageUrl)
        }
    }

    override fun imageRequest(page: Page): Request = super.imageRequest(page).newBuilder()
        .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
        .header("Referer", baseUrl + "/")
        .build()

    override suspend fun getMangaByUrl(url: HttpUrl): SManga? {
        if (url.host != baseUrl.toHttpUrl().host || url.pathSegments.firstOrNull() != "manga") return null
        val key = url.pathSegments.getOrNull(1)?.let {
            try {
                it.decodeMangaKey()
            } catch (_: IllegalArgumentException) {
                null
            }
        } ?: return null
        return api.details(key.siteId, key.detailUrl).toSManga(url.encodedPath)
    }

    private fun MangaYunSearch.toSManga() = SManga.create().apply {
        url = mangaUrl(siteId, detailUrl)
        val sourceLabel = this@toSManga.siteName
            ?.takeIf { it.isNotBlank() }
            ?: this@toSManga.siteId.takeIf { it.isNotBlank() }
        title = sourceLabel?.let { "${this@toSManga.title} [$it]" } ?: this@toSManga.title
        description = this@toSManga.description
        thumbnail_url = coverUrl
    }

    private fun MangaYunDetails.toSManga(url: String) = SManga.create().apply {
        this.url = url
        title = this@toSManga.title
        author = this@toSManga.author
        description = this@toSManga.description
        genre = this@toSManga.categories?.joinToString()
        thumbnail_url = this@toSManga.coverUrl
        status = when (this@toSManga.status?.trim()) {
            "完结", "已完结" -> SManga.COMPLETED
            "连载", "更新中" -> SManga.ONGOING
            else -> SManga.UNKNOWN
        }
        initialized = true
    }

    private fun MangaYunChapter.toSChapter(siteId: String) = SChapter.create().apply {
        url = chapterUrl(siteId, this@toSChapter.url)
        name = this@toSChapter.name
        chapter_number = order.toFloat() + 1
    }

    private data class MangaKey(val siteId: String, val detailUrl: String)

    private data class ChapterKey(val siteId: String, val chapterUrl: String)

    private fun mangaUrl(siteId: String, detailUrl: String) = "/manga/${encodeKey(siteId, detailUrl)}"

    private fun chapterUrl(siteId: String, chapterUrl: String) = "/chapter/${encodeKey(siteId, chapterUrl)}"

    private fun encodeKey(siteId: String, url: String): String = Base64.encodeToString("$siteId\n$url".toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    private fun String.decodeMangaKey(): MangaKey {
        val (siteId, detailUrl) = decodeKey()
        return MangaKey(siteId, detailUrl)
    }

    private fun String.decodeChapterKey(): ChapterKey {
        val (siteId, chapterUrl) = decodeKey()
        return ChapterKey(siteId, chapterUrl)
    }

    private fun String.decodeKey(): Pair<String, String> {
        val decoded = Base64.decode(substringAfterLast('/'), Base64.URL_SAFE).toString(Charsets.UTF_8)
        val separator = decoded.indexOf('\n')
        require(separator > 0) { "Invalid MangaYun source URL" }
        return decoded.substring(0, separator) to decoded.substring(separator + 1)
    }
}
