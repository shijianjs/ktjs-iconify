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
| [ktjs-iconify-icon](ktjs-iconify-icon/README.zh-CN.md) | `iconify-icon` | 3.0.4 | 3.0.4-kt.1 | 浏览器，Web Component |

`ktjs-iconify-utils` 依赖 `ktjs-iconify-types`，`ktjs-iconify-tools` 依赖 `ktjs-iconify-utils`；
依赖均以 `api` 暴露，引入一个模块即可同时获得共享的图标类型。

## 版本约定

每个模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`，声明在各模块的 `build.gradle.kts` 中：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：基础版本跟随上游，修订号重置为 `kt.1`；即使 wrapper 代码没变，也按新上游版本发布。
- 上游不变、只修订 wrapper：修订号递增（`kt.2`、`kt.3`…），基础版本保持不变。
- 改动 `npm(...)` 依赖版本时，必须在同一次改动中同步基础版本号。

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

## 状态

各模块尚未发布到 Maven 仓库，当前作为构建内的 project 依赖使用：

```kotlin
dependencies {
    implementation(project(":ktjs-iconify-utils"))
}
```

## 许可证

采用 [MIT 许可证](LICENSE)发布。
