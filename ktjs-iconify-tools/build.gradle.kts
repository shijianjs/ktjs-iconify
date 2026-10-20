import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }

        nodejs()
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":ktjs-iconify-utils"))
            implementation(npm("@iconify/tools", "5.0.14"))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
