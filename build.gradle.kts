import com.vanniktech.maven.publish.MavenPublishBaseExtension
import dev.detekt.gradle.extensions.DetektExtension
import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins {
    kotlin("multiplatform") version "2.4.21" apply false
    id("com.vanniktech.maven.publish") version "0.37.0" apply false
    id("dev.detekt") version "2.0.0-alpha.6" apply false
    id("org.jetbrains.dokka") version "2.2.0"
}

// Dokka aggregates the four wrapper modules into one API reference. The demo module is not a
// published API surface, so it is not included.
dependencies {
    dokka(project(":ktjs-iconify-types"))
    dokka(project(":ktjs-iconify-utils"))
    dokka(project(":ktjs-iconify-tools"))
    dokka(project(":ktjs-iconify-icon"))
}

dokka {
    dokkaPublications.html {
        moduleName.set("ktjs-iconify")
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}

// GitHub-backed namespace; verified once in the Central Portal. All modules share it.
// Setting the root `group` alone does not propagate to subprojects.
val publishedGroup = "io.github.shijianjs"

// npm package versions live in package.json so that Dependabot can update them.
// Subprojects read this map to declare npm(...) dependencies and derive their version.
@Suppress("UNCHECKED_CAST")
val npmVersions: Map<String, String> = run {
    val manifest = JsonSlurper().parse(file("package.json")) as Map<*, *>
    (manifest["dependencies"] as Map<String, String>).mapValues { it.value.toString() }
}
extra["npmVersions"] = npmVersions

// Wrapper revision shared by every module. It identifies the project-wide wrapper release,
// is independent of upstream npm versions and only ever increases. Keeping it here rather
// than in each module prevents the four modules from drifting apart. See AGENTS.md.
val wrapperRevision = 2
extra["wrapperRevision"] = wrapperRevision

// Keep a single webpack instance across the npm workspace; npm otherwise installs
// a second copy for source-map-loader's peer dependency.
plugins.withType<NodeJsRootPlugin> {
    extensions.getByType(NpmExtension::class.java).override("webpack", "5.108.1")
}

// The public API must use kotlin-browser's web.* types. Kotlin/JS's own org.w3c.dom.* and
// kotlinx.browser.* compile and run against the same objects, but the compiler treats them as
// unrelated types, which breaks callers that use kotlin-browser. detekt.yml forbids the imports.
subprojects {
    apply(plugin = "dev.detekt")

    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.files("detekt.yml"))
        // The plain detekt task is wired into check; point it at the multiplatform source tree.
        source.setFrom(files("src"))
    }

    // Only the wrapper modules are published and documented; `demo` is the GitHub Pages site.
    if (name.startsWith("ktjs-iconify-")) {
        apply(plugin = "org.jetbrains.dokka")
    }
}

// Publishing is configured once for every module so that coordinates, POM metadata, signing and
// the Maven Central target cannot drift apart.
//
// klibs.io discovers libraries automatically: it reads the POM (url / scm.url must point at the
// GitHub repository) and requires the kotlin-tooling-metadata.json produced by the Kotlin
// Multiplatform plugin. Keep both intact when changing this block.
subprojects {
    if (!name.startsWith("ktjs-iconify-")) return@subprojects

    val module = this

    group = publishedGroup

    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()

        pom {
            name.set(module.name)
            description.set(
                module.provider {
                    module.description ?: "Kotlin/JS wrapper for the Iconify ecosystem."
                }
            )
            url.set("https://github.com/shijianjs/ktjs-iconify")
            inceptionYear.set("2026")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://github.com/shijianjs/ktjs-iconify/blob/master/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("shijianjs")
                    name.set("shijianjs")
                    url.set("https://github.com/shijianjs")
                }
            }

            scm {
                url.set("https://github.com/shijianjs/ktjs-iconify")
                connection.set("scm:git:git://github.com/shijianjs/ktjs-iconify.git")
                developerConnection.set("scm:git:ssh://git@github.com/shijianjs/ktjs-iconify.git")
            }
        }
    }
}

// Assembles the GitHub Pages site: the demo at the root and the API reference under /api/.
tasks.register<Sync>("pagesSite") {
    dependsOn(":demo:jsBrowserDistribution", "dokkaGeneratePublicationHtml")
    into(layout.buildDirectory.dir("pages"))
    from(project(":demo").layout.buildDirectory.dir("dist/js/productionExecutable"))
    from(layout.buildDirectory.dir("dokka/html")) {
        into("api")
    }
}
