---
status: observed
scope: MangaYun release CI / APK signing / GitHub Actions
date: 2026-09-20
---
规则：正式 APK 签名必须使用仓库外持久化的 `signingkey.jks`；GitHub Actions 通过 `SIGNING_KEYSTORE_BASE64` 恢复文件，并通过 `KEY_STORE_PASSWORD`、`ALIAS`、`KEY_PASSWORD` 注入构建插件，不能提交 keystore 或密码。
适用 / 不适用：适用于当前 `.github/workflows/release-mangayun.yml` 的 `v*` 标签发布；若更换签名密钥，已安装 APK 可能无法直接覆盖升级，必须先评估迁移方案。
证据：`gradle/build-logic/src/main/kotlin/ExtensionPlugin.kt`；`.github/workflows/release-mangayun.yml`；v1.6.8 Release 的 APK 签名验证结果为 `Verified using v2 scheme (APK Signature Scheme v2): true`。
候选归宿: attention
