import org.jetbrains.kotlin.gradle.ExperimentalJsTestDsl
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
}

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
            implementation(npm("iconify-icon", "3.0.4"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
