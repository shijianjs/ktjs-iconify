plugins {
    kotlin("multiplatform")
    id("dev.opensavvy.vite.kotlin")
}

kotlin {
    js {
        // Vite serves native ES modules in development; the wrapper modules stay CommonJS because
        // their Node tests require it, so this setting only affects the demo executable.
        useEsModules()

        // The demo is a standalone page, so the JS target needs an executable bundle.
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
