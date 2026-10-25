# ktjs-iconify

Kotlin/JS wrappers for the [Iconify](https://iconify.design/) ecosystem.

[English](README.md) | [简体中文](README.zh-CN.md)

**[Live demo](https://shijianjs.github.io/ktjs-iconify/)** ·
**[API reference](https://shijianjs.github.io/ktjs-iconify/api/)**

The repository keeps one `ktjs-*` Gradle module per npm package because the four upstream packages
version independently. For new wrapper projects, group npm packages that share an upstream version
and release cadence into one module; tree shaking removes unused declarations from the final
application bundle anyway.

## Modules

| Module | npm package | Upstream version | Wrapper version | Maven Central | Runtime |
| --- | --- | --- | --- | --- | --- |
| [ktjs-iconify-types](ktjs-iconify-types/README.md) | `@iconify/types` | 2.0.0 | 2.0.0-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-types?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-types) | Types only, no runtime code |
| [ktjs-iconify-utils](ktjs-iconify-utils/README.md) | `@iconify/utils` | 3.1.7 | 3.1.7-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-utils?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-utils) | Browser and Node.js |
| [ktjs-iconify-tools](ktjs-iconify-tools/README.md) | `@iconify/tools` | 5.0.14 | 5.0.14-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-tools?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-tools) | Node.js only |
| [ktjs-iconify-icon](ktjs-iconify-icon/README.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-icon?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-icon) | Browser, Web Component |

`ktjs-iconify-utils` depends on `ktjs-iconify-types`, and `ktjs-iconify-tools` depends on
`ktjs-iconify-utils`; the dependencies are exposed with `api`, so a single dependency brings the
shared icon types along.

## Versioning

Each module is versioned as `{upstream npm version}-kt.{revision}`. The upstream npm versions are
declared once in the root [`package.json`](package.json); the revision is read per module from
[`wrapper-revisions.properties`](wrapper-revisions.properties). Gradle derives the base version and
the `npm(...)` dependency from these two files:

- Only `-kt.N` versions are published; a version without the suffix never exists.
- Upstream upgrade: change the version in `package.json`; the base version follows automatically
  while the revision stays, even when the wrapper code itself did not change.
- Wrapper-only fix: the revision increments (`kt.2`, `kt.3`, ...) while the base version stays.
- Revisions are tracked per module in the root `wrapper-revisions.properties` and only ever
  increase; they never reset to `kt.1`. A release bumps and publishes only the modules that
  changed, so unchanged modules stay at their already-published version.

## Requirements

- JDK 17 or newer (the build uses Gradle 9.4.1).
- Node.js is downloaded automatically by the Kotlin Gradle plugin. npm is used because Yarn is
  disabled in `gradle.properties`.
- Chromium is downloaded by Playwright the first time browser tests run. The demo's end-to-end
  suite is a Kotlin/JVM JUnit 5 test that reuses the same browser.

## Build and test

```bash
./gradlew build

# Per-module tests
./gradlew :ktjs-iconify-types:jsNodeTest
./gradlew :ktjs-iconify-utils:jsBrowserTest
./gradlew :ktjs-iconify-tools:jsNodeTest
./gradlew :ktjs-iconify-icon:jsBrowserTest

# End-to-end checks for the bundled demo page (Playwright through JUnit 5)
./gradlew :demo:e2eTest
```

The GitHub Pages site (demo at the root, Dokka output under `/api/`) is assembled by:

```bash
./gradlew pagesSite
```

After adding or upgrading an npm dependency in a module, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```

## Design notes

- Kotlin 2.4.21, Kotlin/JS IR, ES module output (`useEsModules()`). Browser tests run through
  the Playwright-based test DSL (`browser { test { chromium() } }`), not Karma.
- `@JsModule` declarations must stay ESM-safe: a top-level `external val` with `@JsName` becomes
  a default import, so modules with named exports only (such as `iconify-icon`) are declared as
  an `external object` with a named property.
- Browser-facing public API uses the JetBrains `kotlin-browser` type system: DOM elements are
  `web.html.*` and the document is `web.dom.document`. Do not expose `org.w3c.dom.*` or
  `kotlinx.browser.*` from public declarations, or the wrappers stop composing with
  kotlin-browser projects.
- npm dependencies are pinned in `kotlin-js-store/package-lock.json`.
- `kotlin-wrappers` artifacts are resolved from Maven Central through `exclusiveContent` in
  `settings.gradle.kts`, because the Aliyun mirror lags behind and sometimes serves metadata
  without the corresponding klibs.

## Installation

Releases are published to Maven Central under the `io.github.shijianjs` group. Replace the
version with the one you need (`{upstream version}-kt.{revision}`):

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-iconify-utils:3.1.7-kt.3")
    implementation("io.github.shijianjs:ktjs-iconify-tools:5.0.14-kt.3")
    implementation("io.github.shijianjs:ktjs-iconify-icon:3.0.4-kt.3")
}
```

The POMs point at this GitHub repository (`url` and `scm.url`), and every module is a Kotlin
Multiplatform project publishing `kotlin-tooling-metadata.json`. Those are the requirements for
[klibs.io](https://klibs.io/) to discover the libraries automatically; indexing starts once the
exact version reaches the Google Maven Central mirror.

### Overriding the upstream npm version

The klib records the exact npm version it was built against, and Kotlin/JS propagates that version
to your generated `package.json`. To use a newer upstream release without waiting for a wrapper
release, override it in the root build script (the project that owns the Kotlin/JS npm workspace):

```kotlin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins.withType<NodeJsRootPlugin> {
    extensions.getByType<NpmExtension>().override("@iconify/utils", "3.2.0")
}
```

The generated root `package.json` gets an npm `overrides` entry, which wins over the exact
dependency recorded in the klib for every workspace package. Declaring a second
`npm("@iconify/utils", ...)` dependency is not a substitute: it only changes the `package.json` of
the module that declares it, while other Kotlin modules still pin the wrapper's version, so npm
can install two copies. A newer upstream release may change its JS API, so check the calls you
use.

**Changing an override only takes effect after the lock file is rebuilt.** Kotlin's Gradle plugin
runs npm 11, which does not invalidate an existing lock when the `overrides` map changes:
`npm install` reports `up to date` and keeps the previously locked version, so the new override
silently does nothing. Rebuild the lock instead of relying on the add/upgrade command above:

```bash
rm kotlin-js-store/package-lock.json
./gradlew kotlinNpmInstall
./gradlew kotlinUpgradePackageLock
```

Then confirm the resolved version (for example in `node_modules/@iconify/utils/package.json` or
`build/js/package-lock.json`) before you assume the override is active.

## Publishing

Publishing is configured once in the root [`build.gradle.kts`](build.gradle.kts) for all modules.
There is no automated publishing workflow: releases run from a local machine, listing only the
modules whose revision changed. Every pending module must be listed in a single Gradle invocation,
which Maven Central records as one deployment and counts once against the monthly release limit, no
matter how many modules or versions it contains. See the
[publishing section](AGENTS.md#发布到-maven-central) for the one-time Central Portal and GPG setup and
the manual publish command.

## License

Released under the [MIT License](LICENSE).
