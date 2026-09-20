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

dependencies {
    implementation(project(":lib:randomua"))
}

keiyoushi {
    name = "MangaYun"
    versionCode = 8
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "MangaYun"
        lang = "zh"
        baseUrl { custom("https://mangayun.com") }
    }
}
