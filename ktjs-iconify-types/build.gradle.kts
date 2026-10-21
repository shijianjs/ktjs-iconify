plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
val wrapperRevision = rootProject.extra["wrapperRevision"] as Int
val upstreamVersion = npmVersions.getValue("@iconify/types")
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
