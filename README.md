# ktjs-iconify

Kotlin/JS wrappers for the [Iconify](https://iconify.design/) ecosystem.

[English](README.md) | [简体中文](README.zh-CN.md)

The repository follows the multi-module layout of JetBrains
[kotlin-wrappers](https://github.com/JetBrains/kotlin-wrappers): each npm package gets its own
`ktjs-*` Gradle module, and shared declarations live in a single module instead of being
duplicated.

## Modules

| Module | npm package | Upstream version | Wrapper version | Maven Central | Runtime |
| --- | --- | --- | --- | --- | --- |
| [ktjs-iconify-types](ktjs-iconify-types/README.md) | `@iconify/types` | 2.0.0 | 2.0.0-kt.2 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-types?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-types) | Types only, no runtime code |
| [ktjs-iconify-utils](ktjs-iconify-utils/README.md) | `@iconify/utils` | 3.1.7 | 3.1.7-kt.2 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-utils?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-utils) | Browser and Node.js |
| [ktjs-iconify-tools](ktjs-iconify-tools/README.md) | `@iconify/tools` | 5.0.14 | 5.0.14-kt.2 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-tools?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-tools) | Node.js only |
| [ktjs-iconify-icon](ktjs-iconify-icon/README.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.2 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-icon?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-icon) | Browser, Web Component |

`ktjs-iconify-utils` depends on `ktjs-iconify-types`, and `ktjs-iconify-tools` depends on
`ktjs-iconify-utils`; the dependencies are exposed with `api`, so a single dependency brings the
shared icon types along.

## Versioning

Each module is versioned as `{upstream npm version}-kt.{revision}`. The upstream npm versions are
declared once in the root [`package.json`](package.json); Gradle reads them and derives each
module's base version and `npm(...)` dependency:

- Only `-kt.N` versions are published; a version without the suffix never exists.
- Upstream upgrade: change the version in `package.json`; the base version follows automatically
  while the revision stays, even when the wrapper code itself did not change.
- Wrapper-only fix: the revision increments (`kt.2`, `kt.3`, ...) while the base version stays.
- The revision is one project-wide counter declared once in the root `build.gradle.kts`; it only
  ever increases and never resets to `kt.1`.

## Requirements

- JDK 17 or newer (the build uses Gradle 9.4.1).
- Node.js is downloaded automatically by the Kotlin Gradle plugin. npm is used because Yarn is
  disabled in `gradle.properties`.
- Chromium is downloaded by Playwright the first time browser tests run.

## Build and test

```bash
./gradlew build

# Per-module tests
./gradlew :ktjs-iconify-types:jsNodeTest
./gradlew :ktjs-iconify-utils:jsBrowserTest
./gradlew :ktjs-iconify-tools:jsNodeTest
./gradlew :ktjs-iconify-icon:jsBrowserTest
```

After adding or upgrading an npm dependency in a module, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```

## Design notes

- Kotlin 2.4.21, Kotlin/JS IR, CommonJS output (`JsModuleKind.MODULE_COMMONJS`). CommonJS is
  required for npm `@JsModule` imports in the Node test harness.
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
    implementation("io.github.shijianjs:ktjs-iconify-utils:3.1.7-kt.2")
    implementation("io.github.shijianjs:ktjs-iconify-tools:5.0.14-kt.2")
    implementation("io.github.shijianjs:ktjs-iconify-icon:3.0.4-kt.2")
}
```

The POMs point at this GitHub repository (`url` and `scm.url`), and every module is a Kotlin
Multiplatform project publishing `kotlin-tooling-metadata.json`. Those are the requirements for
[klibs.io](https://klibs.io/) to discover the libraries automatically; indexing starts once the
exact version reaches the Google Maven Central mirror.

## Publishing

Publishing is configured once in the root [`build.gradle.kts`](build.gradle.kts) for all modules
and runs from the **Publish to Maven Central** GitHub Actions workflow. See the
[publishing section](AGENTS.md#发布到-maven-central) for the one-time Central Portal and GPG setup.

## License

Released under the [MIT License](LICENSE).
