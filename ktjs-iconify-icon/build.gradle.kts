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
            api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
            implementation(npm("iconify-icon", "3.0.4"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
