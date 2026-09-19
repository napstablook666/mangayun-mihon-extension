package eu.kanade.tachiyomi.extension.zh.mangayun

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(val keyword: String)

@Serializable
data class MangaYunSearchResponse(val data: List<MangaYunSearchGroup> = emptyList())

@Serializable
data class MangaYunSitesResponse(val data: List<MangaYunSite> = emptyList())

@Serializable
data class MangaYunSite(val siteId: String, val siteName: String? = null)

@Serializable
data class MangaYunProfileResponse(val user: MangaYunUser? = null)

@Serializable
data class MangaYunUser(
    val id: String? = null,
    val username: String? = null,
    val name: String? = null,
    val avatarUrl: String? = null,
    val trustLevel: Int? = null,
)

@Serializable
data class MangaYunShelfResponse(val data: List<MangaYunShelfItem> = emptyList())

@Serializable
data class MangaYunShelfItem(
    val siteId: String,
    val siteName: String? = null,
    val title: String,
    val detailUrl: String,
    val coverUrl: String? = null,
    val chapterName: String? = null,
    val chapterUrl: String? = null,
    val openedAt: Long = 0,
)

@Serializable
data class MangaYunSearchGroup(
    val siteId: String,
    val siteName: String? = null,
    val results: List<MangaYunSearch> = emptyList(),
)

@Serializable
data class MangaYunSearch(
    val title: String,
    val detailUrl: String,
    val coverUrl: String? = null,
    val description: String? = null,
    val siteId: String = "",
    val siteName: String? = null,
)

@Serializable
data class DetailsRequest(val siteId: String, val detailUrl: String)

@Serializable
data class ChapterImagesRequest(val siteId: String, val chapterUrl: String)

@Serializable
data class ShelfRequest(val item: MangaYunShelfItem)

@Serializable
data class ShelfItemsRequest(val items: List<MangaYunShelfItem>)

@Serializable
data class ShelfDeleteRequest(val siteId: String, val detailUrl: String)

@Serializable
data class MangaYunDetailsResponse(val data: MangaYunDetails)

@Serializable
data class MangaYunDetails(
    val title: String,
    val author: String? = null,
    val status: String? = null,
    val categories: List<String>? = null,
    val description: String? = null,
    val coverUrl: String? = null,
    val sourceUrl: String? = null,
    val chapters: List<MangaYunChapter> = emptyList(),
)

@Serializable
data class MangaYunChapter(
    val id: String? = null,
    val name: String,
    val url: String,
    val order: Int = 0,
)

@Serializable
data class MangaYunChapterImagesResponse(val data: MangaYunImages)

@Serializable
data class MangaYunImages(val images: List<String> = emptyList())
