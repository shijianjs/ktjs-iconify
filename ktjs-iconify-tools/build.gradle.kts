plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
@Suppress("UNCHECKED_CAST")
val wrapperRevisions = rootProject.extra["wrapperRevisions"] as Map<String, Int>
val upstreamVersion = npmVersions.getValue("@iconify/tools")
val wrapperRevision = wrapperRevisions.getValue(project.name)
// Empty in a release build, `-SNAPSHOT` in a working tree: see the note in the root build script.
@Suppress("UNCHECKED_CAST")
val versionSuffix = rootProject.extra["versionSuffix"] as String
version = "$upstreamVersion-kt.$wrapperRevision$versionSuffix"
description = "Kotlin/JS wrapper for the @iconify/tools package (Node.js only)."

kotlin {
    js {
        useEsModules()

        nodejs()
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":ktjs-iconify-utils"))
            // `DownloadOptions` exposes the global `URLSearchParams`, which the public API takes
            // from `web.url` rather than declaring a copy of it.
            api("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.2")
            implementation(npm("@iconify/tools", upstreamVersion))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
