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
