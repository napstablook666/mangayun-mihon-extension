# Technical Design

## Release inputs

- `src/zh/mangayun/build.gradle.kts` is the single extension version input: `libVersion = "1.6"` plus `versionCode` produces the visible version name.
- `.github/workflows/release-mangayun.yml` builds with JDK 21, signs the release APK from repository secrets, and creates or updates the GitHub Release for `v*` tags.
- The release tag must be created only after the version commit is on `origin/main`, so the tag workflow builds the intended source.

## Change set

1. Change `versionCode = 10` to `versionCode = 11`; this produces `1.6.11` and `tachiyomi-zh.mangayun-v1.6.11.apk`.
2. Add an explicit `v1.6.11` notes branch in the release workflow. The notes will state that Hamreus chapter-image Referer handling was fixed to reduce HTTP 403 failures.
3. Leave the MangaYun source implementation and all unrelated files unchanged.

## Release flow

1. Confirm `v1.6.11` does not exist remotely and the GitHub CLI is authenticated.
2. Run the local MangaYun build and inspect the diff.
3. Commit only the version/workflow release files, push `main`, and confirm the remote commit.
4. Create an annotated local `v1.6.11` tag at the pushed commit and push that tag.
5. Watch the tag-triggered `release-mangayun.yml` run to completion.
6. Inspect the GitHub Release body and assets, including the APK and `source.zip`.

## Compatibility and rollback

- Existing `v1.6.10` remains untouched.
- No API, signing, source URL, or business behavior changes are included.
- A local build or workflow failure stops the flow before creating a tag where possible. After a tag exists, rerun the failed workflow rather than force-pushing or replacing the tag.
