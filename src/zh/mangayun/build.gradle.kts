import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

android {
    defaultConfig {
        ndk {
            abiFilters += "arm64-v8a"
        }
    }
}

keiyoushi {
    name = "MangaYun"
    versionCode = 5
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "MangaYun"
        lang = "zh"
        baseUrl = "https://mangayun.com"
    }
}
