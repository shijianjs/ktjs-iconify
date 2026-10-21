import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }

        // The demo is a standalone page, so the JS target needs an executable webpack bundle.
        binaries.executable()
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":ktjs-iconify-icon"))
            implementation(project(":ktjs-iconify-utils"))
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.1")
        }
    }
}
