plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
@Suppress("UNCHECKED_CAST")
val wrapperRevisions = rootProject.extra["wrapperRevisions"] as Map<String, Int>
val upstreamVersion = npmVersions.getValue("@iconify/types")
val wrapperRevision = wrapperRevisions.getValue(project.name)
version = "$upstreamVersion-kt.$wrapperRevision"
description = "Kotlin/JS type declarations for the @iconify/types package."

kotlin {
    js {
        useEsModules()

        nodejs()
    }

    sourceSets {
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
