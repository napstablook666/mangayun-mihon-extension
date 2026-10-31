# 发布 MangaYun 1.6.10 并补充更新说明

## Goal

发布 MangaYun 扩展 1.6.10，给 GitHub Release 写清楚本次新版 SPA 适配内容，并让正式标签触发 APK Release workflow。

## Requirements

- 将 `src/zh/mangayun/build.gradle.kts` 的扩展版本从 1.6.9 更新为 1.6.10，遵循仓库版本配置方式。
- 更新 Release workflow 的版本说明生成逻辑，确保本次发布内容清楚写出：新版热门/最新列表、封面代理、DM5 图片 Referer、新 `/m/<key>` 路由、新旧 key 兼容、章节与搜索 DTO 兼容、签名算法保持不变。
- 提交版本更新和 workflow 变更到 `main`。
- 创建并推送 `v1.6.10` 标签，触发 GitHub Release。
- 验证 Release、APK asset 和更新说明可见。

## Constraints

- 不改已完成的 MangaYun 业务适配逻辑。
- 不提交工作区中其他无关未跟踪文件。
- 不强制推送、不覆盖远程历史。

## Acceptance Criteria

- [ ] 扩展版本为 1.6.10。
- [ ] `v1.6.10` 已推送。
- [ ] GitHub Release `v1.6.10` 已创建。
- [ ] Release 页面包含明确的中文更新内容。
- [ ] Release 附件包含 1.6.10 APK。
- [ ] APK workflow 成功。
