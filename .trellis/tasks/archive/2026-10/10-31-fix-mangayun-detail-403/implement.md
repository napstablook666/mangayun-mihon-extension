# Implementation Plan

1. Update the source image Referer mapping in `MangaYun.kt` for `hamreus.com` and its subdomains.
2. Update `mangayun-source-contracts.md` with the verified `hamreus.com -> https://www.manhuagui.com/` rule and required validation case.
3. Run formatting/checks and `JAVA_HOME=<JDK 21> ./gradlew :src:zh:mangayun:assembleDebug`.
4. Review the diff for scope, then start and archive the Trellis task after verification.

## Validation commands

```bash
JAVA_HOME='/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot' ./gradlew :src:zh:mangayun:assembleDebug --no-daemon
```

The browser and Node reproductions already establish the behavior: the supplied details payload has 55 chapters; the first returned `hamreus.com` image is HTTP 403 with the old host-root Referer and HTTP 200 with `https://www.manhuagui.com/`.

## Review gates

- Ensure matching handles both `hamreus.com` and `*.hamreus.com` without changing unrelated hosts.
- Ensure `/api/details` signing and chapter URL handling remain unchanged.
- Ensure only source, contract, and task planning files are in the final diff.
