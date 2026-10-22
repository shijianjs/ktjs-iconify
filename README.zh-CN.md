# ktjs-iconify

[Iconify](https://iconify.design/) 生态的 Kotlin/JS wrapper。

[English](README.md) | [简体中文](README.zh-CN.md)

**[在线演示](https://shijianjs.github.io/ktjs-iconify/)** ·
**[API 文档](https://shijianjs.github.io/ktjs-iconify/api/)**

本仓库为每个 npm 包保留一个 `ktjs-*` Gradle 子模块，因为这四个上游包的版本相互独立。
新建 wrapper 项目时，按上游版本与发版节奏划分：总是一起发版的包放在同一个模块里；终端应用
打包时本来就会摇树，模块内多出的声明不会进入最终产物。

## 模块

| 模块 | npm 包 | 上游版本 | Wrapper 版本 | Maven Central | 运行环境 |
| --- | --- | --- | --- | --- | --- |
| [ktjs-iconify-types](ktjs-iconify-types/README.zh-CN.md) | `@iconify/types` | 2.0.0 | 2.0.0-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-types?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-types) | 纯类型，无运行时代码 |
| [ktjs-iconify-utils](ktjs-iconify-utils/README.zh-CN.md) | `@iconify/utils` | 3.1.7 | 3.1.7-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-utils?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-utils) | 浏览器与 Node.js |
| [ktjs-iconify-tools](ktjs-iconify-tools/README.zh-CN.md) | `@iconify/tools` | 5.0.14 | 5.0.14-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-tools?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-tools) | 仅 Node.js |
| [ktjs-iconify-icon](ktjs-iconify-icon/README.zh-CN.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.3 | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-icon?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-icon) | 浏览器，Web Component |

`ktjs-iconify-utils` 依赖 `ktjs-iconify-types`，`ktjs-iconify-tools` 依赖 `ktjs-iconify-utils`；
依赖均以 `api` 暴露，引入一个模块即可同时获得共享的图标类型。

## 版本约定

每个模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`。上游 npm 版本统一声明在根目录
[`package.json`](package.json) 中，修订号按模块记录在
[`wrapper-revisions.properties`](wrapper-revisions.properties) 中；Gradle 读取这两份文件后
自动推导各模块的基础版本与 `npm(...)` 依赖：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：只改 `package.json`，基础版本自动跟随，修订号保持不变；即使 wrapper 代码没变，也按新上游版本发布。
- 上游不变、只修订 wrapper：修订号递增（`kt.2`、`kt.3`…），基础版本保持不变。
- 修订号按模块记录在根目录 `wrapper-revisions.properties`，各自持续递增，永不重置为 `kt.1`。
  发布时只递增并发布有变更的模块，未变更模块保持已经发布过的版本。

## 环境要求

- JDK 17 及以上（构建使用 Gradle 9.4.1）。
- Node.js 由 Kotlin Gradle 插件自动下载。`gradle.properties` 中禁用了 Yarn，因此使用 npm。
- 首次运行浏览器测试时，Playwright 会下载 Chromium。
- 演示页的端到端测试是独立的 Node 脚本，因此还要求 `PATH` 上有 `node`。

## 构建与测试

```bash
./gradlew build

# 按模块运行测试
./gradlew :ktjs-iconify-types:jsNodeTest
./gradlew :ktjs-iconify-utils:jsBrowserTest
./gradlew :ktjs-iconify-tools:jsNodeTest
./gradlew :ktjs-iconify-icon:jsBrowserTest

# 演示页的端到端检查（Playwright，需要 PATH 上有 node）
./gradlew :demo:e2eTest
```

GitHub Pages 站点（根目录为演示页，`/api/` 为 Dokka 文档）由以下任务组装：

```bash
./gradlew pagesSite
```

在模块中新增或升级 npm 依赖后，需要刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```

## 设计说明

- Kotlin 2.4.21，Kotlin/JS IR，输出 ES modules（`useEsModules()`）。浏览器测试走新版
  Playwright 测试 DSL（`browser { test { chromium() } }`），不再使用 Karma。
- `@JsModule` 声明必须兼容 ESM：顶层 `external val` 配 `@JsName` 会编译成 default import，
  只有具名导出的包（如 `iconify-icon`）要用 `external object` + 具名属性声明。
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
    implementation("io.github.shijianjs:ktjs-iconify-utils:3.1.7-kt.3")
    implementation("io.github.shijianjs:ktjs-iconify-tools:5.0.14-kt.3")
    implementation("io.github.shijianjs:ktjs-iconify-icon:3.0.4-kt.3")
}
```

各模块的 POM 包含指向本 GitHub 仓库的 `url` 与 `scm.url`，并且作为 Kotlin Multiplatform
项目发布了 `kotlin-tooling-metadata.json`。这是 [klibs.io](https://klibs.io/) 自动发现库的
条件；版本进入 Google 的 Maven Central 镜像后会被自动收录。

### 覆盖上游 npm 版本

klib 会记录构建时使用的上游 npm 精确版本，Kotlin/JS 会把它带进你生成的 `package.json`。
如果不想等 wrapper 发版就想用更新的上游版本，可以在根构建脚本（拥有 Kotlin/JS npm
workspace 的那个项目）里覆盖：

```kotlin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins.withType<NodeJsRootPlugin> {
    extensions.getByType<NpmExtension>().override("@iconify/utils", "3.2.0")
}
```

生成的根 `package.json` 会增加 npm `overrides` 条目，对 workspace 里的所有包生效，优先级
高于 klib 里记录的精确依赖。再加一个 `npm("@iconify/utils", ...")` 依赖不能替代它：那只改
声明它的模块的 `package.json`，其他 Kotlin 模块仍固定 wrapper 的版本，npm 可能因此安装两份
拷贝。更上游的版本可能改变 JS API，调用前请确认实际用法。

## 发布

发布配置统一写在根目录 [`build.gradle.kts`](build.gradle.kts)，通过 GitHub Actions 的
**Publish to Maven Central** 工作流执行。只有修订号变化的模块才会得到新版本，工作流会跳过
Maven Central 上已存在的版本。所有待发布模块在同一次 Gradle 调用中上传，Central 记录为一个
deployment，无论其中包含多少模块或版本，都只占一次月度发布额度。一次性的 Central Portal 与 GPG
配置见 [发布到 Maven Central](AGENTS.md#发布到-maven-central)。

## 许可证

采用 [MIT 许可证](LICENSE)发布。
