# Implementation Plan

1. Update `src/zh/mangayun/build.gradle.kts` from `versionCode = 10` to `versionCode = 11`.
2. Add a `v1.6.11` release-notes branch to `.github/workflows/release-mangayun.yml` with the Hamreus HTTP 403 fix.
3. Run `git diff --check` and `JAVA_HOME=<JDK 21> ./gradlew :src:zh:mangayun:assembleDebug --no-daemon`.
4. Commit only the two release files, push `main`, and verify `origin/main`.
5. Create and push `v1.6.11` only after confirming the remote tag is absent.
6. Use `gh run watch` for the tag workflow, then verify the Release body and APK/source assets with `gh release view`.
7. Mark acceptance criteria, archive the task, and commit the task artifacts separately if needed.

## Validation commands

```bash
git diff --check
JAVA_HOME='/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot' ./gradlew :src:zh:mangayun:assembleDebug --no-daemon
gh auth status
git ls-remote --tags origin refs/tags/v1.6.11
```

## Review gates

- Do not stage any pre-existing untracked Trellis/bootstrap files.
- Confirm the release commit is pushed before pushing the tag.
- Stop on an existing remote tag or Release instead of overwriting it.
- Verify the built filename is `tachiyomi-zh.mangayun-v1.6.11.apk`.
