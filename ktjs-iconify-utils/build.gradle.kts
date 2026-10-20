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
            implementation(npm("@iconify/utils", "3.1.7"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
