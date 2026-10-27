import org.jetbrains.kotlin.gradle.ExperimentalJsTestDsl

plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
@Suppress("UNCHECKED_CAST")
val wrapperRevisions = rootProject.extra["wrapperRevisions"] as Map<String, Int>
val upstreamVersion = npmVersions.getValue("iconify-icon")
val wrapperRevision = wrapperRevisions.getValue(project.name)
version = "$upstreamVersion-kt.$wrapperRevision"
description = "Kotlin/JS wrapper for the iconify-icon web component."

kotlin {
    js {
        useEsModules()

        browser {
            @OptIn(ExperimentalJsTestDsl::class)
            test {
                chromium()
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":ktjs-iconify-types"))
            api("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.3")
            implementation(npm("iconify-icon", upstreamVersion))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
