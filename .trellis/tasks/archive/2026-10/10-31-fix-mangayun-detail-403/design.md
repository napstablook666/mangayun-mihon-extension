# Technical Design

## Root cause

The supplied `/m/` link and its `/api/details` request work. The failure is in the reader image request for the first chapter:

- `/api/chapter-images` returns images on `*.hamreus.com`.
- The current fallback sends `Referer: https://us.hamreus.com/`.
- The exact image returns HTTP 403 without an upstream-site Referer and HTTP 200 with `Referer: https://www.manhuagui.com/`.
- MangaYun's browser reader avoids this by routing the image through `/api/image`; the extension requests the chapter image directly.

## Change

Update `src/zh/mangayun/src/eu/kanade/tachiyomi/extension/zh/mangayun/MangaYun.kt` in `imageRequest(page)`:

1. Keep the existing `*.cdndm5.com -> https://www.dm5.com/` rule.
2. Add `hamreus.com` and `*.hamreus.com -> https://www.manhuagui.com/`.
3. Keep the generic host-root fallback for all other image hosts.

The mapping is host-based because the affected CDN host is the image origin returned for the `manhuagui` source and the current failure is caused by its hotlink policy.

## Preserved contracts

- Do not change `/api/details`, `/api/chapter-images`, request bodies, signing, or MangaYun key decoding.
- Do not proxy chapter images through `/api/image`; direct chapter image URLs are already part of the existing extension flow.
- Do not alter cover normalization, search, metadata, or chapter ordering.

## Verification

- Re-run the exact chapter-image request for the supplied link and confirm the image is 403 with the old host-root Referer and 200 with the new `www.manhuagui.com` Referer.
- Build the MangaYun module with JDK 21 using `:src:zh:mangayun:assembleDebug`.
- Since no source-module test tree exists, use the exact network reproduction plus the build as the regression check and record the host mapping in the maintained MangaYun source contract.

## Risk and rollback

The change affects only image requests whose host is `hamreus.com` or a subdomain. Rollback is a one-branch deletion if another source hosted on that CDN requires a different Referer.
