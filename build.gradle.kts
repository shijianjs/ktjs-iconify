import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins {
    kotlin("multiplatform") version "2.4.21" apply false
}

// npm package versions live in package.json so that Dependabot can update them.
// Subprojects read this map to declare npm(...) dependencies and derive their version.
@Suppress("UNCHECKED_CAST")
val npmVersions: Map<String, String> = run {
    val manifest = JsonSlurper().parse(file("package.json")) as Map<*, *>
    (manifest["dependencies"] as Map<String, String>).mapValues { it.value.toString() }
}
extra["npmVersions"] = npmVersions

// Keep a single webpack instance across the npm workspace; npm otherwise installs
// a second copy for source-map-loader's peer dependency.
plugins.withType<NodeJsRootPlugin> {
    extensions.getByType(NpmExtension::class.java).override("webpack", "5.108.1")
}
