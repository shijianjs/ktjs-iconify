import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
version = "2.0.0-kt.1"

kotlin {
    js {
        compilerOptions {
            moduleKind.set(JsModuleKind.MODULE_COMMONJS)
        }

        nodejs()
    }

    sourceSets {
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
