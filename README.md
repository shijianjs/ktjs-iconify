# ktjs-iconify

Kotlin/JS wrappers for the [Iconify](https://iconify.design/) ecosystem.

[English](README.md) | [简体中文](README.zh-CN.md)

The repository follows the multi-module layout of JetBrains
[kotlin-wrappers](https://github.com/JetBrains/kotlin-wrappers): each npm package gets its own
`ktjs-*` Gradle module, and shared declarations live in a single module instead of being
duplicated.

## Modules

| Module | npm package | Upstream version | Wrapper version | Runtime |
| --- | --- | --- | --- | --- |
| [ktjs-iconify-types](ktjs-iconify-types/README.md) | `@iconify/types` | 2.0.0 | 2.0.0-kt.1 | Types only, no runtime code |
| [ktjs-iconify-utils](ktjs-iconify-utils/README.md) | `@iconify/utils` | 3.1.7 | 3.1.7-kt.1 | Browser and Node.js |
| [ktjs-iconify-tools](ktjs-iconify-tools/README.md) | `@iconify/tools` | 5.0.14 | 5.0.14-kt.1 | Node.js only |
| [ktjs-iconify-icon](ktjs-iconify-icon/README.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.1 | Browser, Web Component |

`ktjs-iconify-utils` depends on `ktjs-iconify-types`, and `ktjs-iconify-tools` depends on
`ktjs-iconify-utils`; the dependencies are exposed with `api`, so a single dependency brings the
shared icon types along.

## Versioning

Each module is versioned as `{upstream npm version}-kt.{revision}`, declared in the module's
`build.gradle.kts`:

- Only `-kt.N` versions are published; a version without the suffix never exists.
- Upstream upgrade: the base version follows upstream and the revision resets to `kt.1`, even when
  the wrapper code itself did not change.
- Wrapper-only fix: the revision increments (`kt.2`, `kt.3`, ...) while the base version stays.
- Bumping an `npm(...)` dependency requires updating the base version in the same change.

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

## Status

The modules are not published to a Maven repository yet. Use them as project dependencies inside
this build:

```kotlin
dependencies {
    implementation(project(":ktjs-iconify-utils"))
}
```

## License

Released under the [MIT License](LICENSE).
