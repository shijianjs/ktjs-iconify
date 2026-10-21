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

// End-to-end checks for the bundled page. Playwright and Chromium come from the Kotlin/JS npm
// workspace under build/js, so the icon module's browser install task provides them.
tasks.register<Exec>("e2eTest") {
    group = "verification"
    description = "Runs the Playwright checks against the bundled demo page."
    dependsOn("jsBrowserDistribution", ":ktjs-iconify-icon:kotlinInstallPlaywrightBrowsers")

    val dist = layout.buildDirectory.dir("dist/js/productionExecutable")
    inputs.dir(dist)
    inputs.file(layout.projectDirectory.file("e2e/demo-page.test.mjs"))
    environment("DEMO_DIST", dist.get().asFile.absolutePath)
    commandLine("node", "e2e/demo-page.test.mjs")
}
