# 发布 MangaYun 1.6.11

## Goal

发布包含 Hamreus 章节图片 Referer 修复的 MangaYun 扩展 1.6.11，并让 GitHub Release 自动生成可安装 APK。

## User value

用户升级后，MangaYun 章节图片请求会使用源站要求的 Referer，减少 `*.hamreus.com` 图片返回 HTTP 403 的情况。

## Confirmed facts

- 当前最新标签是 `v1.6.10`。
- `src/zh/mangayun/build.gradle.kts` 当前 `versionCode = 10`，结合 `libVersion = "1.6"` 生成扩展版本 `1.6.10`。
- 当前 `main` 包含修复提交 `ef2fb89 fix(mangayun): use source referer for hamreus images`，本地比 `origin/main` 超前 1 个提交。
- `.github/workflows/release-mangayun.yml` 在 `main` 和 `v*` 标签推送时运行；标签构建签名 Release APK，并创建或更新 GitHub Release。
- 当前 workflow 对 `v1.6.10` 有专用中文说明，其他版本走提交日志生成逻辑。
- `v1.6.11` 尚不存在，发布流程可以使用新标签触发。

## Requirements

1. 将 MangaYun 扩展版本从 `1.6.10` 更新为 `1.6.11`，只递增版本号，不改业务适配逻辑。
2. 为 `v1.6.11` 生成明确的中文 Release 更新说明，至少包含 Hamreus 章节图片 Referer 修复和 HTTP 403 缓解。
3. 提交版本与 Release workflow 变更，推送 `main`。
4. 创建并推送 `v1.6.11` 标签，不强制推送、不覆盖已有远程历史。
5. 等待并核验 GitHub Actions、GitHub Release、APK asset、source archive 和更新说明。

## Constraints

- 不提交工作区中已有的无关未跟踪文件。
- 不修改已验证的 MangaYun 业务实现。
- 不删除或重建已有 Release 和标签。
- 如果远程已出现 `v1.6.11` 或同名 Release，先停止并报告，不覆盖它。

## Acceptance Criteria

- [x] `src/zh/mangayun/build.gradle.kts` 的扩展版本为 `1.6.11`，版本代码为 `11`。
- [x] 本地 MangaYun 模块构建通过。
- [x] 版本与 workflow 变更已提交并推送到 `origin/main`。
- [x] `v1.6.11` 标签指向发布提交并已推送到远程。
- [x] `v1.6.11` 对应的 GitHub Actions workflow 成功完成。
- [x] GitHub Release `v1.6.11` 存在，标题、中文更新说明、`tachiyomi-zh.mangayun-v1.6.11.apk` 和 `source.zip` 均可见。
- [x] 本次提交没有纳入其他 Trellis 初始化产生的未跟踪文件。

## Out of scope

- 重新设计 MangaYun API、签名、章节读取或图片请求逻辑。
- 修改 README、发布历史或其他扩展。
- 创建第二个发布渠道或手工替代 GitHub Actions 构建。

## Open questions

无。版本号按现有 `1.6.10` 顺延为 `1.6.11`。
