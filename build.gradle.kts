import com.vanniktech.maven.publish.JavadocJar
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

// Wrapper revisions are per module: a release only creates new versions for the modules whose
// revision was bumped, so unchanged modules are not re-uploaded (file/size quota and version
// noise). Release Count is per Central deployment, not per module: publishing every pending
// module in one Gradle invocation makes the plugin upload a single deployment.
// See AGENTS.md.
@Suppress("UNCHECKED_CAST")
val wrapperRevisions: Map<String, Int> = run {
    val properties = java.util.Properties()
    file("wrapper-revisions.properties").inputStream().use { properties.load(it) }
    properties.stringPropertyNames().associate { name ->
        name to properties.getProperty(name).trim().toInt()
    }
}
extra["wrapperRevisions"] = wrapperRevisions

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

    check(wrapperRevisions.containsKey(name)) {
        "Missing '$name' in wrapper-revisions.properties"
    }

    val module = this

    group = publishedGroup

    apply(plugin = "com.vanniktech.maven.publish")

    // The subproject's Kotlin Multiplatform plugin is applied after this root script runs.
    // Choosing the javadoc jar earlier would hit the "no compatible plugin" branch and the
    // plugin's afterEvaluate default (Dokka HTML) would win.
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<MavenPublishBaseExtension> {
            // Maven Central requires a javadoc jar, but Dokka's HTML output duplicates its static
            // assets into every module's jar (~1.1 MB each). The API reference is deployed to the
            // Pages site, so publish the required empty jar instead of the HTML copy.
            configureBasedOnAppliedPlugins(javadocJar = JavadocJar.Empty())
        }
    }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()

        // Gradle writes .md5/.sha1 next to every .asc signature. Maven Central does not need
        // them and they count toward the monthly file quota (KTC-5453, gradle/gradle#20232).
        excludeSignatureChecksums()

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

// Prints "<module>=<version>" for every publishable module. Use it to see which versions a release
// would upload before running publishToMavenCentral, so version derivation stays in the module
// scripts rather than being duplicated in shell.
tasks.register("printPublishedVersions") {
    val publishedModules = subprojects.filter { it.name.startsWith("ktjs-iconify-") }
    doLast {
        publishedModules
            .associate { it.name to it.version.toString() }
            .toSortedMap()
            .forEach { (name, version) -> println("$name=$version") }
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
