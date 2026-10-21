# ktjs-iconify

[Iconify](https://iconify.design/) 生态的 Kotlin/JS wrapper。

[English](README.md) | [简体中文](README.zh-CN.md)

仓库仿照 JetBrains [kotlin-wrappers](https://github.com/JetBrains/kotlin-wrappers) 的多模块布局：
每个 npm 包对应一个 `ktjs-*` Gradle 子模块，共享声明只保留一份。

## 模块

| 模块 | npm 包 | 上游版本 | Wrapper 版本 | 运行环境 |
| --- | --- | --- | --- | --- |
| [ktjs-iconify-types](ktjs-iconify-types/README.zh-CN.md) | `@iconify/types` | 2.0.0 | 2.0.0-kt.1 | 纯类型，无运行时代码 |
| [ktjs-iconify-utils](ktjs-iconify-utils/README.zh-CN.md) | `@iconify/utils` | 3.1.7 | 3.1.7-kt.1 | 浏览器与 Node.js |
| [ktjs-iconify-tools](ktjs-iconify-tools/README.zh-CN.md) | `@iconify/tools` | 5.0.14 | 5.0.14-kt.1 | 仅 Node.js |
| [ktjs-iconify-icon](ktjs-iconify-icon/README.zh-CN.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.2 | 浏览器，Web Component |

`ktjs-iconify-utils` 依赖 `ktjs-iconify-types`，`ktjs-iconify-tools` 依赖 `ktjs-iconify-utils`；
依赖均以 `api` 暴露，引入一个模块即可同时获得共享的图标类型。

## 版本约定

每个模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`。上游 npm 版本统一声明在根目录
[`package.json`](package.json) 中，Gradle 读取后自动推导各模块的基础版本与 `npm(...)` 依赖：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：只改 `package.json`，基础版本自动跟随，修订号重置为 `kt.1`；即使 wrapper 代码没变，也按新上游版本发布。
- 上游不变、只修订 wrapper：修订号递增（`kt.2`、`kt.3`…），基础版本保持不变。
- 各模块 `build.gradle.kts` 只声明修订号。

## 环境要求

- JDK 17 及以上（构建使用 Gradle 9.4.1）。
- Node.js 由 Kotlin Gradle 插件自动下载。`gradle.properties` 中禁用了 Yarn，因此使用 npm。
- 首次运行浏览器测试时，Playwright 会下载 Chromium。

## 构建与测试

```bash
./gradlew build

# 按模块运行测试
./gradlew :ktjs-iconify-types:jsNodeTest
./gradlew :ktjs-iconify-utils:jsBrowserTest
./gradlew :ktjs-iconify-tools:jsNodeTest
./gradlew :ktjs-iconify-icon:jsBrowserTest
```

在模块中新增或升级 npm 依赖后，需要刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```

## 设计说明

- Kotlin 2.4.21，Kotlin/JS IR，输出 CommonJS（`JsModuleKind.MODULE_COMMONJS`）。Node 测试
  直接 `require` 编译产物，npm 的 `@JsModule` 导入依赖 CommonJS。
- 面向前端的公开 API 统一使用 JetBrains `kotlin-browser` 的类型体系：DOM 元素为
  `web.html.*`，全局 document 为 `web.dom.document`。不要在公开声明中暴露
  `org.w3c.dom.*` 或 `kotlinx.browser.*`，否则无法与 kotlin-browser 项目组合使用。
- npm 依赖固定在 `kotlin-js-store/package-lock.json`。
- `kotlin-wrappers` 的构件通过 `settings.gradle.kts` 中的 `exclusiveContent` 从 Maven Central
  解析，因为阿里云镜像同步滞后，且有时只有元数据没有对应的 klib。

## 安装

发布版本位于 Maven Central，group 为 `io.github.shijianjs`。把版本换成你需要的
`{上游版本}-kt.{修订号}`：

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-iconify-utils:3.1.7-kt.1")
    implementation("io.github.shijianjs:ktjs-iconify-tools:5.0.14-kt.1")
    implementation("io.github.shijianjs:ktjs-iconify-icon:3.0.4-kt.2")
}
```

各模块的 POM 包含指向本 GitHub 仓库的 `url` 与 `scm.url`，并且作为 Kotlin Multiplatform
项目发布了 `kotlin-tooling-metadata.json`。这是 [klibs.io](https://klibs.io/) 自动发现库的
条件；版本进入 Google 的 Maven Central 镜像后会被自动收录。

## 发布

发布配置统一写在根目录 [`build.gradle.kts`](build.gradle.kts)，通过 GitHub Actions 的
**Publish to Maven Central** 工作流执行。一次性的 Central Portal 与 GPG 配置见
[发布到 Maven Central](AGENTS.md#发布到-maven-central)。

## 许可证

采用 [MIT 许可证](LICENSE)发布。
