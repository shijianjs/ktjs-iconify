import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins {
    kotlin("multiplatform") version "2.4.21" apply false
}

// Keep a single webpack instance across the npm workspace; npm otherwise installs
// a second copy for source-map-loader's peer dependency.
plugins.withType<NodeJsRootPlugin> {
    extensions.getByType(NpmExtension::class.java).override("webpack", "5.108.1")
}
