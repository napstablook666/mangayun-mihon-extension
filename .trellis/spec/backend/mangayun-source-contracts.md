# MangaYun Source Contracts

## 1. Scope / Trigger

This contract applies to the `src/zh/mangayun` extension when integrating with `https://mangayun.com`. It records the API and URL behavior verified against the current Vue/Vite SPA.

## 2. Signatures

- `GET /api/sites?v=3`
- `GET /api/home-sections`
- `POST /api/search` with `{ "keyword": String }`
- `POST /api/details` with `{ "siteId": String, "detailUrl": String }`
- `POST /api/chapter-images` with `{ "siteId": String, "chapterUrl": String }`
- `GET /api/image?url={sourceUrl}&siteId={siteId}` is an unsigned image proxy request.
- API requests use `SHA-256("{path}|{body}|{timestamp}|{nonce}|ym-salt-883a0f7e-29f1-4b72-9ad2")` in `x-ym-sign`, with `x-ym-ts`, `x-ym-nonce`, and `Content-Type: application/json`.

## 3. Contracts

- `/api/home-sections` responds with `{ "data": { "popular": [...], "recent": [...] } }`.
- Search responds with `{ "data": [{ "siteId": String, "siteName": String?, "results": [...], "error": String? }] }`. Result items may contain `extra: Map<String, String>`.
- Details responds with `{ "data": { "title": String, "author": String?, "status": String?, "categories": [String]?, "description": String?, "coverUrl": String?, "sourceUrl": String?, "chapters": [...] } }`.
- Chapter items contain `name`, `url`, `order`, and optional `updatedAt`. `url` may be a source URL or an opaque source-specific ID and must be passed through unchanged to `/api/chapter-images`.
- Manga detail URLs use `/m/{base64url(JSON.stringify([siteId, detailUrl]))}`. The extension may continue decoding legacy `base64url("siteId\\nurl")` records for stored entries.
- Covers should use `/api/image` unless the source host is `hamreus.com`, `cdndm5.com`, `ccavbox.com`, `bgm.tv`, or `tuku.cc`; existing `/api/image` URLs must not be wrapped again.
- Image requests use `https://www.dm5.com/` as Referer for `*.cdndm5.com`; otherwise use the image host root.

## 4. Validation & Error Matrix

| Condition | Required behavior |
|---|---|
| Popular/latest page > 1 | Return an empty page with no next page; the upstream endpoint is not paginated. |
| Search group has `error` | Preserve the group response; do not fail the entire search response. |
| Unknown or missing manga status | Map to `SManga.UNKNOWN`. |
| Existing `/api/image` cover | Return it unchanged. |
| Invalid manga key | Return `null` from `getMangaByUrl` instead of throwing. |
| DM5 image host | Send `Referer: https://www.dm5.com/`. |

## 5. Good / Base / Bad Cases

- Good: `/api/home-sections` produces six popular and three recent entries that become manga records.
- Base: A search response has results from several sites and one site has `error`; usable results remain visible.
- Bad: Treating `data.popular` as a top-level response field produces empty sections or a deserialization failure.
- Bad: Emitting the legacy newline key in `/m/` makes the current MangaYun SPA show its home page instead of the manga.

## 6. Tests Required

- Run `JAVA_HOME=<JDK 21> ./gradlew :src:zh:mangayun:assembleDebug`.
- Verify `/api/home-sections` deserializes through the `data` wrapper and maps both sections.
- Verify a new JSON-array `/m/` key and a legacy newline key both decode to the same `siteId` and `detailUrl`.
- Verify cover proxy normalization, existing proxy passthrough, DM5 Referer mapping, and non-DM5 host Referer mapping.

## 7. Wrong vs Correct

### Wrong

```kotlin
suspend fun homeSections(): MangaYunHomeSections = get("/api/home-sections")
```

This expects `popular` and `recent` at the response root, but the API nests them under `data`.

### Correct

```kotlin
suspend fun homeSections(): MangaYunHomeSections =
    get<MangaYunHomeSectionsResponse>("/api/home-sections").data
```

The response wrapper is modeled explicitly and the source receives the inner sections object.
