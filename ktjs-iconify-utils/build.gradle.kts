import org.jetbrains.kotlin.gradle.ExperimentalJsTestDsl

plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
@Suppress("UNCHECKED_CAST")
val wrapperRevisions = rootProject.extra["wrapperRevisions"] as Map<String, Int>
val upstreamVersion = npmVersions.getValue("@iconify/utils")
val wrapperRevision = wrapperRevisions.getValue(project.name)
version = "$upstreamVersion-kt.$wrapperRevision"
description = "Kotlin/JS wrapper for the @iconify/utils package."

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
            implementation(npm("@iconify/utils", upstreamVersion))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
