plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
val wrapperRevision = rootProject.extra["wrapperRevision"] as Int
val upstreamVersion = npmVersions.getValue("@iconify/tools")
version = "$upstreamVersion-kt.$wrapperRevision"
description = "Kotlin/JS wrapper for the @iconify/tools package (Node.js only)."

kotlin {
    js {
        useEsModules()

        nodejs()
    }

    sourceSets {
        jsMain.dependencies {
            api(project(":ktjs-iconify-utils"))
            implementation(npm("@iconify/tools", upstreamVersion))
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
