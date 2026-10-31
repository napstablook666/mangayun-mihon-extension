# 推送 MangaYun 更新并触发 APK 构建

## Goal

将已提交的 MangaYun 适配更新推送到 GitHub，并确认仓库现有 GitHub Actions 自动构建 APK 已触发。

## Requirements

- 推送当前 `main` 分支的提交 `b93c3b0` 到其 GitHub 远程仓库。
- 不提交当前工作区中与本次发布无关的未跟踪文件。
- 检查 GitHub Actions 工作流是否因 push 自动触发。
- 获取构建任务的运行编号、状态和 APK 下载位置；如果构建失败，记录失败步骤和错误摘要。
- 当 push 到 `main` 时，workflow 必须构建 release APK 并上传 artifact；只有推送 `v*` 标签时才创建 GitHub Release。

## Constraints

- 不修改 MangaYun 适配代码。
- 不强制推送，不覆盖远程历史。
- 推送前确认远程地址、当前分支和待推送提交。

## Acceptance Criteria

- [ ] `b93c3b0` 已成功推送到 GitHub 的 `main` 分支。
- [ ] 对应 GitHub Actions workflow run 已创建。
- [ ] workflow 最终成功，或已明确报告失败步骤和原因。
- [ ] 若构建成功，提供 APK artifact 或 release 下载地址。

## Notes

- 本次任务只处理发布和构建，不重新实现代码。
- 工作区原有未跟踪的 Trellis、agent 和配置文件不纳入推送，除非用户另行要求。
