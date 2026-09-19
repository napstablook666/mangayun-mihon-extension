package eu.kanade.tachiyomi.extension.zh.mangayun

import keiyoushi.network.post
import keiyoushi.utils.JSON_MEDIA_TYPE
import keiyoushi.utils.parseAs
import keiyoushi.utils.toJsonString
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.UUID

internal class MangaYunApi(private val source: MangaYun) {

    suspend fun search(keyword: String): List<MangaYunSearch> = post<SearchRequest, MangaYunSearchResponse>("/api/search", SearchRequest(keyword))
        .data
        .flatMap { group -> group.results.map { it.copy(siteId = group.siteId, siteName = group.siteName) } }

    suspend fun sites(): List<MangaYunSite> = get<MangaYunSitesResponse>("/api/sites").data

    suspend fun profile(): MangaYunUser? = get<MangaYunProfileResponse>("/api/user/profile").user

    suspend fun shelf(): List<MangaYunShelfItem> = get<MangaYunShelfResponse>("/api/user/shelf").data

    suspend fun addShelf(item: MangaYunShelfItem) {
        post<ShelfRequest, Unit>("/api/user/shelf", ShelfRequest(item))
    }

    suspend fun replaceShelf(items: List<MangaYunShelfItem>) {
        post<ShelfItemsRequest, Unit>("/api/user/shelf", ShelfItemsRequest(items))
    }

    suspend fun removeShelf(siteId: String, detailUrl: String) {
        delete<Unit>("/api/user/shelf", ShelfDeleteRequest(siteId, detailUrl))
    }

    suspend fun logout() {
        post<Unit, Unit>("/api/user/logout", Unit)
    }
    suspend fun details(siteId: String, detailUrl: String): MangaYunDetails = post<DetailsRequest, MangaYunDetailsResponse>(
        "/api/details",
        DetailsRequest(siteId, detailUrl),
    ).data

    suspend fun chapterImages(siteId: String, chapterUrl: String): List<String> = post<ChapterImagesRequest, MangaYunChapterImagesResponse>(
        "/api/chapter-images",
        ChapterImagesRequest(siteId, chapterUrl),
    ).data.images

    private suspend inline fun <reified T : Any, reified R : Any> post(path: String, request: T): R {
        val body = request.toJsonString()
        val response = source.client.post(
            source.baseUrl + path,
            signedHeaders(path, body),
            body.toRequestBody(JSON_MEDIA_TYPE),
        )
        return response.parseAs()
    }

    private suspend inline fun <reified R : Any> get(path: String): R {
        val response = source.client.newCall(
            Request.Builder()
                .url(source.baseUrl + path)
                .headers(signedHeaders(path, ""))
                .get()
                .build(),
        ).execute()
        return response.parseAs()
    }

    private suspend inline fun <reified R : Any> delete(path: String, request: Any): R {
        val body = request.toJsonString()
        val response = source.client.newCall(
            Request.Builder()
                .url(source.baseUrl + path)
                .headers(signedHeaders(path, body))
                .delete(body.toRequestBody(JSON_MEDIA_TYPE))
                .build(),
        ).execute()
        return response.parseAs()
    }

    private fun signedHeaders(path: String, body: String): Headers {
        val timestamp = System.currentTimeMillis().toString()
        val nonce = UUID.randomUUID().toString().replace("-", "").take(16)
        val raw = "$path|$body|$timestamp|$nonce|$SALT"
        val signature = MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        return source.headers.newBuilder()
            .set("Content-Type", "application/json")
            .set("x-ym-ts", timestamp)
            .set("x-ym-nonce", nonce)
            .set("x-ym-sign", signature)
            .build()
    }

    private companion object {
        const val SALT = "ym-salt-883a0f7e-29f1-4b72-9ad2"
    }
}
