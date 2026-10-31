# Fix MangaYun Detail HTTP 403

## Goal

Restore MangaYun detail refresh and chapter discovery for current `/m/{key}` links, including the user-provided link, without changing unrelated source behavior.

## User value

Users can open a MangaYun manga link in Mihon, see its metadata, and refresh the chapter list instead of receiving the WebView-style `HTTP 403` error.

## Confirmed facts

- The reported link is `https://mangayun.com/m/WyJtYW5odWFndWkiLCJodHRwczovL3d3dy5tYW5odWFndWkuY29tL2NvbWljLzQ1MzM4LyJd`.
- Its current JSON-array key decodes to `siteId = "manhuagui"` and `detailUrl = "https://www.manhuagui.com/comic/45338/"`.
- The current MangaYun SPA route renders the manga and shows 55 chapters in the browser, while Mihon reports `HTTP 403，请在 WebView 中检查网站`.
- `MangaYun.getMangaByUrl` decodes the key and calls `MangaYunApi.details`; `fetchMangaUpdate` uses the same details response for metadata and chapters.
- `MangaYunApi.details` currently sends `POST /api/details` with JSON `{ "siteId": ..., "detailUrl": ... }` and the signed headers `x-ym-ts`, `x-ym-nonce`, `x-ym-sign`, and `Content-Type: application/json`.
- A manually constructed request with the current signature contract returned HTTP 200 and a details payload containing chapters, so the investigation must verify the actual extension request path and runtime behavior before changing the API contract.
- The maintained source contract requires SHA-256 signing over `{path}|{body}|{timestamp}|{nonce}|ym-salt-883a0f7e-29f1-4b72-9ad2` and requires chapter URLs to pass through unchanged to `/api/chapter-images`.

## Requirements

1. Identify the root cause of the detail request failure in the extension path, distinguishing client request construction, key decoding, response handling, and upstream access blocking.
2. Apply the smallest source change that makes the supplied current detail link load successfully through the extension.
3. Preserve support for existing stored MangaYun links and the existing `/api/details` and `/api/chapter-images` contracts unless evidence proves a contract change is required.
4. Keep metadata and chapter refresh on the existing shared details response; do not add a second scraping implementation.
5. Add or update the smallest meaningful regression check for the discovered failure mode when the repository's test/build structure permits it.
6. Do not add dependencies or alter unrelated MangaYun features.

## Acceptance criteria

- [x] The supplied `/m/` link is accepted by `getMangaByUrl` and reaches the details request with the expected `siteId` and `detailUrl`.
- [x] Refreshing the supplied manga returns non-empty metadata and a non-empty chapter list from the current MangaYun API; the observed upstream payload contains 55 chapters.
- [x] The details request uses the exact JSON body and signature inputs required by the maintained MangaYun contract, with no body/signature mismatch.
- [x] Chapter URLs returned by details remain usable by `/api/chapter-images`, and the existing chapter image flow is not regressed.
- [x] The MangaYun module passes `JAVA_HOME=<JDK 21> ./gradlew :src:zh:mangayun:assembleDebug`.
- [x] The final diff is limited to the root cause, its regression check, and necessary task/spec documentation.

## Out of scope

- Rebuilding the MangaYun WebView or adding browser automation to the extension.
- Authentication, CAPTCHA, IP reputation, or a server-side proxy if the 403 is caused by upstream access policy rather than the extension request.
- Redesigning the signing protocol or changing unrelated search, home-section, shelf, cover, or image behavior.
- Publishing a new release; release work is a separate task after the fix is verified.

## Risks and deferred items

- If the same signed request succeeds outside Mihon but is blocked only from the app's network identity, code changes may not be able to remove the 403. In that case, document the evidence and stop at the smallest viable client-side mitigation instead of adding speculative retries or a proxy.

## Open questions

None blocking planning.
- Lightweight tasks can remain PRD-only.
- For complex tasks, add `design.md` for technical design and `implement.md` for execution planning before `task.py start`.
