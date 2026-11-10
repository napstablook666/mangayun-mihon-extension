package eu.kanade.tachiyomi.extension.zh.mangayun

import keiyoushi.network.post
import keiyoushi.utils.JSON_MEDIA_TYPE
import keiyoushi.utils.parseAs
import keiyoushi.utils.toJsonString
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest

internal class MangaYunApi(private val source: MangaYun) {

    suspend fun search(keyword: String): List<MangaYunSearch> {
        val normalizedKeyword = keyword.normalizeSearchText()
        return post<SearchRequest, MangaYunSearchResponse>("/api/search", SearchRequest(keyword))
            .data
            .flatMap { group -> group.results.map { it.copy(siteId = group.siteId, siteName = group.siteName) } }
            .sortedWith(
                compareBy<MangaYunSearch> { result ->
                    val title = result.title.normalizeSearchText()
                    when {
                        normalizedKeyword.isNotEmpty() && title == normalizedKeyword -> 0
                        normalizedKeyword.isNotEmpty() && title.contains(normalizedKeyword) -> 1
                        else -> 2
                    }
                }.thenBy { it.title.length },
            )
    }

    private fun String.normalizeSearchText(): String = lowercase().filterNot { it.isWhitespace() }

    suspend fun sites(): List<MangaYunSite> = get<MangaYunSitesResponse>("/api/sites").data

    suspend fun homeSections(): MangaYunHomeSections = get<MangaYunHomeSectionsResponse>("/api/home-sections").data

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
        val nonce = buildString(12) {
            repeat(12) { append(POW_CHARS.random()) }
        }
        val challenge = sha256("$SALT|${timestamp.toLong() / POW_EPOCH_MS}|${path.take(12)}")
        val proof = solveProof(challenge, timestamp, nonce)
        val bodyHash = if (body.isEmpty()) "" else sha256(body)
        val signature = sha256("$path|$bodyHash|$timestamp|$nonce|$FEAT|$proof|$challenge")

        return source.headers.newBuilder()
            .set("Content-Type", "application/json")
            .set("x-ym-ts", timestamp)
            .set("x-ym-nonce", nonce)
            .set("x-ym-pow", proof.toString())
            .set("x-ym-feat", FEAT)
            .set("x-ym-sign", signature)
            .build()
    }

    private fun solveProof(challenge: String, timestamp: String, nonce: String): Int {
        repeat(POW_MAX_ITERATIONS) { counter ->
            val digest = sha256("$challenge:$timestamp:$nonce:$FEAT:$counter")
            if (digest[0] == '0' && digest[1] == '0') return counter
        }
        return POW_MAX_ITERATIONS
    }

    private fun sha256(input: String): String = MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }

    private companion object {
        const val SALT = "ym-cloud_ream-2026_v9-aegis_3c"
        const val FEAT = "f"
        const val POW_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789"
        const val POW_EPOCH_MS = 300_000L
        const val POW_MAX_ITERATIONS = 4096
    }
}
