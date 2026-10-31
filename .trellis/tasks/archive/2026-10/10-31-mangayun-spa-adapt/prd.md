# MangaYun 站点适配（新版 SPA 接口）

## Goal

让 `src/zh/mangayun` 扩展跟得上 mangayun.com 当前的 Vue3 SPA 版本：可用上「热门 / 最新」入口、封面能正常显示、新路由能被 `getMangaByUrl` 识别，发布 `versionCode = 9`。

## 背景（已实测确认的站点现状）

- 站点是 Vue3 + Vite 单页应用，入口 `assets/index-DiDA33Lo.js`。
- 签名算法**没变**：`SHA-256("{path}|{body}|{ts}|{nonce}|{salt}")`，salt 仍是 `ym-salt-883a0f7e-29f1-4b72-9ad2`，请求头 `x-ym-ts` / `x-ym-nonce` / `x-ym-sign` + `Content-Type: application/json`。现有 `MangaYunApi.signedHeaders` 直接可用。
- 路由改了两条：
  - `/m/<key>` 详情页，旧 `/manga/<key>` 仍返回 200（SPA fallback），新 key 格式是 `base64url(JSON.stringify([siteId, detailUrl]))`。旧的 `base64url("siteId\nurl")` 由扩展继续兼容解码，但新版网页不能用它打开详情。
  - `/read/<mangaKey>/<chapterKey>` 阅读页。
  - `/s/<keyword>` 搜索页，未变。
- 接口现状：
  - `GET /api/sites?v=3` — 可用，`?v=` 实际被忽略。
  - `POST /api/search {keyword}` — 可用，**不支持分页**，`page` / `offset` 传了也返回同样的第一页。
  - `POST /api/details {siteId, detailUrl}` — 可用，返回 `title/author/status/description/coverUrl/sourceUrl/chapters`。
  - `POST /api/chapter-images {siteId, chapterUrl}` — 可用。
  - `GET /api/home-sections` — **新增**，返回 `{popular: [...], recent: [...]}`，元素字段 `id/title/coverUrl/siteId/siteName/detailUrl/latestChapter/views/addedAt/updatedAt`。
  - `GET /api/image?url=&siteId=` — 图片代理，**不需要签名**，返回 `image/jpeg`。
- 数据结构新增（都可选）：
  - 单条搜索结果多了 `extra`（如 `{"作者":"熊之股鍵次"}`）。
  - 搜索分组多了 `error`（如 `源站返回 HTTP 403`）。
  - 章节多了 `updatedAt`（ISO 8601）。
  - `chapters[].url` 不再一定是 URL：`hipmh` 返回纯 ID（如 `bTo4MDgzLWM6OTIwMw-ODA4MzoxLjAw`），原样回传给 `chapter-images` 即可。
  - `details.status` 取值有 `连载` / `完结` / `未知` / 缺失。
- 图片：
  - 源站图片直链大多可直连，但 `cdndm5.com` 用自身域名当 Referer 会直接断连（curl `000`），必须用 `https://www.dm5.com/`。
  - `tuku.cc` 图片必须用图片自身域名当 Referer，`https://mangayun.com/` 会被 403。
  - 封面现在大多走 `https://mangayun.com/api/image?url=...&siteId=...`；白名单 `hamreus.com` / `cdndm5.com` / `ccavbox.com` / `bgm.tv` / `tuku.cc` 不走代理。

## Requirements

1. `getPopularManga(page)` 接 `GET /api/home-sections` 的 `popular`；`getLatestUpdates(page)` 接 `recent`。`supportsLatest` 改为 `true`（现在是 `false` 且两个方法都返回空列表）。分页：`page > 1` 返回空（上游只给一页）。
2. 搜索结果和详情页的封面统一走 `/api/image` 代理：域名命中白名单则原样返回，否则拼成 `https://mangayun.com/api/image?url={encoded}&siteId={siteId}`。`/api/image` 自身返回的 URL 不重复包装。
3. `imageRequest(page)` 的 `Referer` 按图片域名映射，不再是单纯的 `https://$host/`：
   - `*.cdndm5.com` → `https://www.dm5.com/`
   - 其他 → `https://$host/`（保持现状即可，`tuku.cc` 等已实测 200）
4. `getMangaByUrl(url)` 支持 `/m/{key}` 路径；`/manga/{key}` 保留兼容。
5. DTO 补齐：搜索单条 `extra`、搜索分组 `error`、章节 `updatedAt`、`home-sections` 的新模型；`status` 映射增加 `未知`。
6. `versionCode` 8 → 9。

## 非目标

- 不做登录 / 书架 / `/api/user/*` 的接入（现状就没接）。
- 不做 `/api/recommendations`（内容与 `home-sections.popular` 完全重复）。
- 不给搜索做分页（上游没有）。
- 不改签名算法；详情 URL 使用站点新版 JSON 数组 key，同时保留旧换行 key 的解码兼容。

## Acceptance Criteria

- [x] `./gradlew :src:zh:mangayun:assembleDebug` 通过
- [x] 搜索「天命大反派」返回结果，`manhuazhijia` / `hipmh` / `tuku` / `rumanhua` 至少 4 个站点有结果
- [x] 「热门」页能列出 6 部（来自 `popular`），「最新」页能列出 3 部（来自 `recent`）
- [x] 用 `manhuazhijia` 的详情页 key 走 `/m/<key>` 能被 `getMangaByUrl` 解析出 manga
- [x] 详情页章节列表完整（`manhuazhijia` 343 话、`hipmh` 368 话），章节顺序为倒序
- [x] 章节图片能打开：至少覆盖 `manhuazhijia`（24 张）、`hipmh`（150 张）、`tuku`（67 张）、`rumanhua`（18 张）
- [x] `dm5` 的封面在阅读器里能显示（Referer 修正生效）
- [x] 封面 URL 形如 `https://mangayun.com/api/image?url=...&siteId=...`（非白名单域名时）

## Notes

- 代码改动集中在 `src/zh/mangayun/src/eu/kanade/tachiyomi/extension/zh/mangayun/` 下三个文件：`MangaYun.kt`、`MangaYunApi.kt`、`MangaYunDto.kt`。
- 签名算法不动；`decodeKey` 保留旧格式兼容，详情 `mangaUrl` 改用新版 JSON 数组 key，章节内部 key 仍使用旧格式。
- 轻量任务，PRD-only。
