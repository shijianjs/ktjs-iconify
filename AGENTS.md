# AGENTS.md

## 本地代码仓库

以下 GitHub 仓库已克隆到本地，需要查阅源码时直接读本地目录，不要重新克隆：

- `S:\workspace\github\JetBrains\kotlin-toolchain`
- `S:\workspace\github\JetBrains\kotlin-wrappers`
- `S:\workspace\github\JetBrains\kotlin-web-site`

## 拉取 GitHub 仓库

需要反复阅读 GitHub 上的仓库（多个文件、多轮检索）时，优先用 `gh` 把它克隆到 `S:\workspace\github\<owner>\<repo>` 再读本地文件，不要用 WebFetch 逐个页面抓取：

```bash
gh repo clone <owner>/<repo> S:/workspace/github/<owner>/<repo>
```

克隆前先确认 `S:\workspace\github\<owner>\<repo>` 是否已存在；已存在时直接用 `git -C <path> pull` 更新。

## 模块结构

- `ktjs-iconify-types`：`@iconify/types` 类型声明，无运行时依赖。
- `ktjs-iconify-utils`：`@iconify/utils`，浏览器可用，测试跑 Chromium。
- `ktjs-iconify-tools`：`@iconify/tools`，仅 Node.js；ESM-only 的导入方式见 [ktjs-iconify-tools/AGENTS.md](ktjs-iconify-tools/AGENTS.md)。
- `ktjs-iconify-icon`：`iconify-icon` Web Component。

## 浏览器 DOM 类型体系

统一使用 JetBrains `kotlin-browser`（`org.jetbrains.kotlin-wrappers:kotlin-browser`）的 `web.*` 类型，不要在公开 API 中混用 `org.w3c.dom.*`（Kotlin/JS 自带）或 `kotlinx.browser.*`：

- DOM 元素：`web.html.HTMLElement`
- 全局 document：`web.dom.document`
- 创建自定义元素：`document.createElement(TagName<IconifyIconElement>("iconify-icon"))`，无需 `unsafeCast`

两套类型在运行时指向同一个对象，但编译器不视为同一类型；wrapper 的公开 API 一旦用 `org.w3c.dom.*`，kotlin-browser 项目就无法直接使用。

## 版本约定

每个子模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`，在各模块 `build.gradle.kts` 的 `version` 中声明：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：基础版本跟随上游，修订号重置为 `kt.1`；即使 wrapper 代码没变，也要按新上游版本发布。
- 上游不变、只修订 wrapper：递增修订号（`kt.2`、`kt.3`…）。
- 改动模块 `npm(...)` 依赖版本时，必须同步修改 `version` 的基础版本部分。
- `-kt.N` 在 SemVer 中是预发布标识，优先级低于同基础版本的无后缀版本；因为永不发布无后缀版本，发布序列内部不会冲突。

## Maven 镜像

阿里云镜像的 `kotlin-wrappers` 同步滞后，且有时只有 POM 没有 klib，而 Gradle 一旦在某个仓库找到元数据就不会回退。`settings.gradle.kts` 已用 `exclusiveContent` 把 `org.jetbrains.kotlin-wrappers` 和 `kotlin-js-plain-objects` 固定到 Maven Central；新增 wrapper 依赖时如遇 `Could not find ... .klib`，把对应 group/module 加进去。

## 文档约定

- 需要跨会话、跨上下文感知的信息（环境约定、构建方式、模块结构、容易踩的坑）记录在本文件。
- 只与某个目录强相关、篇幅较长的说明，放在该目录下的 `AGENTS.md`（如 `ktjs-iconify-tools/AGENTS.md`），根文件只保留指向它的简短索引。
