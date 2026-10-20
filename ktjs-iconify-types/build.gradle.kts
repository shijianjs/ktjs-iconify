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
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
