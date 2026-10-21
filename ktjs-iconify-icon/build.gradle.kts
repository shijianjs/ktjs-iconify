import org.jetbrains.kotlin.gradle.ExperimentalJsTestDsl
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
val upstreamVersion = npmVersions.getValue("iconify-icon")
version = "$upstreamVersion-kt.2"
description = "Kotlin/JS wrapper for the iconify-icon web component."

kotlin {
    js {
        compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }

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
            api("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.1")
            implementation(npm("iconify-icon", upstreamVersion))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
