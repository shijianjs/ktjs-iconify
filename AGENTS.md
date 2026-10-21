# AGENTS.md

## 本地代码仓库

以下 GitHub 仓库已克隆到本地，需要查阅源码时直接读本地目录，不要重新克隆：

- `S:\workspace\github\JetBrains\kotlin-toolchain`
- `S:\workspace\github\JetBrains\kotlin-wrappers`
- `S:\workspace\github\JetBrains\kotlin-web-site`
- `S:\workspace\github\iconify\iconify`（`iconify-icon` Web Component 源码在 `iconify-icon/icon`）
- `S:\workspace\github\iconify\tools`（`@iconify/tools` 源码在 `@iconify/tools/src`）

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
- `demo`：GitHub Pages 演示站点，不属于发布模块，组装方式见「演示页面与 GitHub Pages」。

## 浏览器 DOM 类型体系

统一使用 JetBrains `kotlin-browser`（`org.jetbrains.kotlin-wrappers:kotlin-browser`）的 `web.*` 类型，不要在公开 API 中混用 `org.w3c.dom.*`（Kotlin/JS 自带）或 `kotlinx.browser.*`：

- DOM 元素：`web.html.HTMLElement`
- 全局 document：`web.dom.document`
- 创建自定义元素：`document.createElement(IconifyIconTagName.iconifyIcon)`，无需 `unsafeCast`
- 自定义元素标签名参照 kotlin-browser 的 `web.html.HtmlTagName`，为 Web Component 提供同风格的 `object XxxTagName { inline val xxx: TagName<XxxElement> }`，把 `TagName` 字符串集中在一处，调用方直接用类型化常量

两套类型在运行时指向同一个对象，但编译器不视为同一类型；wrapper 的公开 API 一旦用 `org.w3c.dom.*`，kotlin-browser 项目就无法直接使用。

`build` / `check` 会执行 Detekt 的 `ForbiddenImport` 规则：根脚本为所有子模块应用 `dev.detekt` 并分析 `src`，根目录 `detekt.yml` 禁止 `org.w3c.dom.*` 与 `kotlinx.browser.*`。新增模块无需单独配置。该规则只拦截 import，代码里直接写全限定名（如 `kotlinx.browser.window`）不会被发现。

Detekt 版本固定为 `2.0.0-alpha.6`：1.23.8 在 JDK 25 下直接失败，而 2.0 alpha 明确支持 Kotlin 2.4 / Gradle 9 / JDK 25。升级时注意 2.0 的插件 ID 是 `dev.detekt`，规则配置项也已改名（如 `imports` → `forbiddenImports`）。

## UI 编程约定

以下为维护者的通用 UI 编码偏好（源自 duckfn-docs-kit 的约定，只保留与具体项目无关的部分）。新增任何 UI 代码（本仓库的 Web Component 包装、演示页，以及由本仓库派生的 wrapper 项目）都按此执行；与具体模块绑定的细节不写在这里。

### 保留模式：对原生 DOM 做面向对象封装

不要引入 React / Vue / Lit 的「状态 → render → 重建 DOM」模型。组件应表现为持有内部 DOM 对象的普通类，按以下优先级裁决（上面的赢）：

1. 保留 DOM 节点：节点长期存在，不随状态重建。
2. 类/对象字段持有 DOM 引用。
3. 直接修改 DOM：property / attribute / text / class / 事件监听。
4. 只做局部更新。
5. 必要时才替换局部集合。
6. 禁止整组件 re-render。
7. 禁止用响应式 state 驱动 render。
8. 禁止引入 Lit / React / Vue 等 UI runtime 或其设计模式。

### 禁止拼 HTML 字符串

不许 `innerHTML = "..."`、`insertAdjacentHTML`，也不许任何「模板字符串生成标记再塞进 DOM」的写法。节点用 `document.createElement`（Kotlin/JS 中优先用 kotlin-browser 的类型化 API）逐个构造并组装。

### DOM 用对象操作，字段持有引用

需要反复访问的节点一律存成字段，直接对字段调方法；不要用 `querySelector` 当组件内部的状态管理方式。`querySelector` 只允许用于「从外部挂载点找目标」这类不属于组件自身结构的场景。

### 更新局部、直接、明确，不设通用刷新机制

状态变化只改相关的那几个节点，用命名的领域 setter 表达（如 `setResult` / `setLoading`），而不是把整个 UI 当成 data 的函数重算。用标准 DOM API 直说：`textContent`、`setAttribute` / `removeAttribute`、`classList.add/remove/toggle`、`hidden`、`disabled`、`value`、`href` / `src` 赋值等。

禁止实现 `render()` / `rebuild()` / `rerender()` 这类通用刷新入口；禁止因为一个字段变化就重建整个子树；禁止用「替换子树」模拟响应式更新：

```ts
set data(value) { this.#data = value; this.render(); }        // 禁止
update(data) { this.replaceChildren(); this.render(data); }   // 禁止
```

### `replaceChildren()` 分级使用

它本身不是禁品，禁的是把它当整组件的通用 render 机制：

- 允许：确实需要整体更新的局部集合（一次性静态组装、整批新条目、固定的「图标 + 文本」组合）。
- 禁止：清空组件自己的根子树再重建。

变长列表能增量就增量：按数据长度增删条目、复用已有条目对象，只有条目语义整体失效时才整批替换。

### 内容入口：命名的领域 setter

组件不接受整份 data blob，也不提供通用 `render` / `apply` / `update` 入口。每个组件暴露一组领域 setter，一个方法只碰它负责的那部分节点：

- setter 只做 mutation；变长集合按新长度增删条目、复用已有对象。
- setter 只写自己持有的节点，因此在元素尚未连接、甚至尚未插入文档时调用也安全。
- 不做 attribute reflection：不把 setter 的值镜像回 attribute。
- 新增字段就加对应 setter，不要回头引入 `setData()` / `update()`。

**例外（attribute 种子）**：由构建期插件生成、没有挂载点可调 setter 的元素，可在 `connectedCallback()` 里从 attribute 读一次作初始种子；这与「读取用于初始化」不冲突，仍属保留模式。手写挂载的元素仍走 setter。

### 生命周期：构造时建树

- 静态结构优先在 `constructor()` 中创建并组装完毕，`connectedCallback()` 只做必要的一次性初始化，不要把 `connectedCallback() → rebuild() → build()` 当标准生命周期。
- Custom Elements 规范限制：构造函数里不得给 `this` 加属性或子节点（`this.append(...)`、`this.setAttribute(...)` 都不行）；`attachShadow()` 合法，所以结构挂进 shadow root，light DOM 保持为空。
- 监听器按归属决定挂在哪：挂在自有节点上的写在构造函数里，无需清理；挂在 `document` / `window` / `matchMedia` 等外部对象上的，必须在 `disconnectedCallback()` 里移除，注册用幂等标志（元素移动或框架重挂载会再次触发 `connectedCallback()`）。

### 组件用 class，但不建通用组件基类

一个组件一个类，自带字段、构造时组装、自己的 setter；不做 `create()` / `apply()` 之类的模板方法抽象，那会把状态驱动刷新重新引进来。非元素的小部件（折叠控件、卡片等）同样是 class；函数式导出只留给真正的纯工具。

### 图标一律用官方 `<iconify-icon>`

- 不要自己封装 SVG（不手写路径、不拼 `data:image/svg+xml`、不做 CSS mask 助手）。
- 数据契约里图标就是 Iconify 名字字符串（如 `lucide:arrow-right`）。
- 尺寸/颜色用 CSS 作用在 `iconify-icon` 元素上，字形自带 `currentColor`。
- 尺寸只认 `font-size`：组件内部是 `<svg width="1em" height="1em">`，给宿主设 `width` / `height` 只会撑大空盒子。

### 默认 Shadow DOM

- 组件默认渲染进 `shadow root`（`mode: 'open'`），样式通过 `adoptedStyleSheets` 或 shadow 内样式注入，避免与宿主页面互相污染。
- 主题靠 CSS 自定义属性继承穿过 shadow 边界；这些变量必须声明在文档的 `:root` / `[data-theme]` 上。组件内部不要写 `[data-theme]` 选择器，也不要依赖宿主 class。
- 只有继承属性穿得过边界：`box-sizing` 不是继承属性，shadow 内必须在自己的作用域重新声明 `box-sizing` 重置，不要依赖宿主的 `* { box-sizing: border-box }`。
- 万不得已才退回 light DOM（仅当必须直接复用宿主的 CSS 规则时），类名加项目前缀，新增例外要在评审里说明依赖了宿主的哪条规则。混合形态（部分 shadow、部分 light）要划清边界并记录原因。

### 无 document 环境安全

- 可能被 Node（SSR、`jsNodeTest`）加载的模块，类字段初始化器不要碰 `document` / `window` / `customElements`；这些入口要么带守卫，要么由调用方在浏览器环境触达。
- `CSSStyleSheet` 等浏览器对象必须惰性创建（在构造函数里），不要在模块级初始化。
- 浏览器专用的重依赖用动态 `import()` 按需加载，业务纯数据模块不要反向依赖重依赖。

### React 19 自定义元素

- 对象 prop 不会在 SSR/hydration 阶段设到自定义元素上，挂载用 callback ref 显式喂内容。
- `mount*()` 助手必须是纯 setter 调用（幂等）：StrictMode 双调用、元素移动后重挂载会重复喂同样的数据，重复执行必须无副作用。
- 预渲染 HTML 里自定义元素是空壳是已知取舍；light DOM 恒为空可以顺带消除 hydration mismatch。若控制台出现 hydration 报错指向自定义元素，说明有代码把节点挂回了 light DOM，修原因而不是用 `suppressHydrationWarning` 掩盖。

## 版本约定

每个子模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`。上游 npm 版本只在根目录 `package.json` 的 `dependencies` 中声明，根 `build.gradle.kts` 解析后放入 `extra["npmVersions"]`，各子模块读取它来声明 `npm(...)` 并推导版本基础部分。

`kt.{修订号}` 是整个项目共享的全局发布序号，只在根 `build.gradle.kts` 的 `wrapperRevision` 中维护，各子模块统一读取，禁止在模块脚本里单独硬编码：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 修订号持续递增，永不重置为 `kt.1`。它表示项目的第 N 次 wrapper 发布，与上游 npm 版本各自独立演进，不要按上游版本重新计数。
- wrapper 代码有变化时，把根脚本的 `wrapperRevision` 加一；四个模块的版本号同步变化，发布时保持四个模块的修订号一致。
- 仅上游 npm 版本变化、wrapper 代码没变时，`wrapperRevision` 保持不变；Dependabot 改 `package.json` 后，受影响模块的基础版本自动跟随（如 `3.0.4-kt.2` → `3.0.5-kt.2`），只发布该模块即可。
- 不要再把 npm 版本或 `kt.N` 硬编码回子模块的 `build.gradle.kts`。
- `-kt.N` 在 SemVer 中是预发布标识，优先级低于同基础版本的无后缀版本；因为永不发布无后缀版本，发布序列内部不会冲突。

## 仓库与本地镜像

仓库里提交的构建配置只使用官方源：`settings.gradle.kts` 用 Gradle Plugin Portal 与 Maven Central，`gradle/wrapper/gradle-wrapper.properties` 用 `services.gradle.org`。这样 GitHub Actions 和任何克隆仓库的人行为一致。

国内加速只在本机生效，通过 `~/.gradle/init.d/aliyun-mirror.gradle` 注入阿里云镜像，不提交到仓库；不要在 `settings.gradle.kts` 或 wrapper 配置里写镜像地址。用 Groovy 而不是 `.gradle.kts`：IDEA 同步时会对 `init.d` 下的 Kotlin DSL 脚本做独立编译，`beforeSettings` 等隐式接收者解析不了，会报一堆 `Unresolved reference`（命令行构建却正常）。

`kotlin-wrappers` 同步滞后，且有时只有 POM 没有 klib，而 Gradle 一旦在某个仓库找到元数据就不会回退到下一个仓库。`settings.gradle.kts` 因此用 `exclusiveContent` 把 `org.jetbrains.kotlin-wrappers` 和 `kotlin-js-plain-objects` 固定到 Maven Central；新增 wrapper 依赖时如遇 `Could not find ... .klib`，把对应 group/module 加进去。

## CI 与依赖更新

- `.github/workflows/ci.yml` 在 push 到 `master` 和所有 PR 上运行 `./gradlew build`（JDK 21，Ubuntu）。
- `.github/workflows/dependabot-auto-merge.yml` 在 CI 成功后自动 squash 合并 Dependabot 的 PR。它用 `workflow_run` 而不是直接由 Dependabot PR 触发，因为后者拿到的 `GITHUB_TOKEN` 只读。
- `.github/dependabot.yml` 覆盖 `gradle`、`npm`（根目录 `package.json`）和 `github-actions` 三个生态。npm 版本由 Dependabot 改 `package.json`，Gradle 自动读取，不再手工改 `npm(...)`。
- Gradle wrapper 固定在 `9.4.1`，并在 Dependabot 里忽略 `gradle-wrapper`：IntelliJ IDEA 2026.2.3 同步 Gradle 9.8.x 项目时会报 `ExternalSystemPartialResolutionException`（消息为空，界面只显示“同步失败”），回退到 9.4.1 即恢复正常。等 IDEA 修复后再删除这条 ignore 并升级。
- Dependabot 的 npm PR 不会带 Gradle 生成的锁文件更新，所以 CI 对 Dependabot PR 先跑 `kotlinUpgradePackageLock` 再构建，并把校验过的锁文件作为 `npm-lock` artifact 上传；自动合并 job 下载该 artifact、提交到 PR 分支，然后 squash 合并。普通 PR 仍然严格校验锁文件。
- `kotlin-js-store` 里只有 Gradle 生成的锁文件，没有也不应该有 `package.json`；根目录的 `package.json` 才是版本清单，不要给 `kotlin-js-store` 配置 npm 生态的 Dependabot。
- 本地增量构建后，`jsPackageJson` / `rootPackageJson` 可能残留旧输出（模块版本变成 `0.0.0-unspecified`、漏掉 `packages_imported/*`），此时 CI 会报 `Lock file was changed`。执行 `./gradlew kotlinUpgradePackageLock --rerun-tasks` 重新生成锁文件并提交。

## 发布到 Maven Central

发布配置统一在根 `build.gradle.kts` 的 `subprojects {}` 里，使用
`com.vanniktech.maven.publish`，所以 group、POM、签名和 Central Portal 目标不会被各模块改乱。
group 固定为 `io.github.shijianjs`（GitHub 命名空间，Central 可自动验证）；`subprojects` 的
group 必须单独设置，根项目的 `group` 不会自动继承。

一次性准备：

1. 在 [Central Portal](https://central.sonatype.com/) 注册账号，添加并验证 `io.github.shijianjs`
   namespace（选择 GitHub 验证，用户名 `shijianjs`）。
2. 生成 GPG 密钥并把公钥发布到 `keys.openpgp.org` / `keyserver.ubuntu.com`。当前发布密钥指纹
   为 `BE2F 2272 947D 5C0D 119A F7FE 5F86 E41B 23AE 8164`（ed25519，2029-10-19 到期），
   公钥见 `https://keys.openpgp.org/vks/v1/by-fingerprint/BE2F2272947D5C0D119AF7FE5F86E41B23AE8164`。
3. 在 Central Portal 生成 **User Token**（不是登录密码）。
4. 在 GitHub 仓库 Secrets 中配置：
   `MAVEN_CENTRAL_USERNAME`、`MAVEN_CENTRAL_PASSWORD`（即 User Token 的两部分）、
   `MAVEN_CENTRAL_SIGNING_KEY`（`gpg --export-secret-keys --armor <key id>` 的完整输出）、
   `MAVEN_CENTRAL_SIGNING_KEY_PASSWORD`（密钥没有密码时留空）。

发布时手动运行 **Publish to Maven Central** workflow（`.github/workflows/publish.yml`），可以选
单个模块或 `all`。DSL 里用的是 `publishToMavenCentral(automaticRelease = true)`，上传后会等待
Central 校验并自动发布。Maven Central 的版本不可覆盖，发布前务必确认版本号没有被用过；同一
版本重复发布会失败。选 `all` 只有在四个模块的当前版本都从未发布过时才安全；日常发布应只选
版本有变化的模块。

本地检查发布内容时，`publishToMavenLocal` 对非 `-SNAPSHOT` 版本也强制签名，需要提供
`signingInMemoryKey` 等属性。只想看 POM 和构件清单时，可以临时把模块版本改成 `-SNAPSHOT`，
或者只运行 `generatePomFileForKotlinMultiplatformPublication`、`buildKotlinToolingMetadata`。

### klibs.io 自动发现

klibs.io 没有需要提交的元数据文件，它从 Maven Central 和 GitHub 自动抓取，满足以下条件即可被收录：

- 项目开源且托管在 GitHub。
- 至少一个构件发布到 Maven Central，且确切版本已同步到 Google 的 Maven Central 镜像（同步有延迟）。
- 至少一个构件是 Kotlin Multiplatform 项目，并发布 `kotlin-tooling-metadata.json`。Kotlin
  Multiplatform 插件会自动生成该文件，`maven-publish` 会把它附到根 publication 上；当前四个
  模块虽然只有 JS target，也属于 KMP 并满足此条件。
- POM 的 `url` 或 `scm.url` 是有效的 GitHub 仓库链接。根 `build.gradle.kts` 已统一设置，
  改仓库地址时要同步修改，否则 klibs.io 无法关联源码。

发布后可以在 `https://klibs.io/package/{GROUP_ID}/{ARTIFACT_ID}/{VERSION}/status` 查看索引状态，
例如 `https://klibs.io/package/io.github.shijianjs/ktjs-iconify-utils/3.1.7-kt.1/status`。镜像同步
完成后仍未被发现，可以在
[klibs-io-issue-management](https://github.com/JetBrains/klibs-io-issue-management/issues/new?template=index_request.yml)
提交 index request。

## 演示页面与 GitHub Pages

- `demo` 模块是演示站点，只消费公共 API，不发布、也不进入 Dokka。根 `build.gradle.kts` 的发布与
  Dokka 配置都按 `ktjs-iconify-` 前缀过滤子模块，新增非发布模块时沿用该约定。
- demo 的端到端测试是 `demo/e2e/demo-page.test.mjs`：Playwright 驱动打包后的
  `demo/build/dist/js/productionExecutable`，脚本自带静态服务器，不涉及 `build/pages` 与 API
  文档。运行 `./gradlew :demo:e2eTest`；它不在 `check` 里，CI 在 `pagesSite` 之后单独执行。
  Playwright 与 Chromium 复用 Kotlin/JS npm workspace（`build/js`）和
  `:ktjs-iconify-icon:kotlinInstallPlaywrightBrowsers`，脚本本身用 PATH 上的 `node` 启动。
- `./gradlew pagesSite` 把 `demo/build/dist/js/productionExecutable` 与 `build/dokka/html`
  组装到 `build/pages`：演示页在根目录，API 文档在 `api/`。演示页内部链接使用相对路径，因此
  `/api/` 在本地和 Pages 上都可用。
- Dokka 2.2.0 同时应用到根项目和四个 wrapper 子模块；聚合入口是根项目的
  `dependencies { dokka(project(...)) }`，任务为 `dokkaGeneratePublicationHtml`（2.x 已移除旧的
  `dokkaHtmlMultiModule`）。
- `.github/workflows/pages.yml` 在 push 到 `master` 后构建并部署 `build/pages`。仓库
  Settings → Pages 的 Source 必须选 **GitHub Actions**（一次性配置，工作流不会自动改）。
- 站点地址为 `https://shijianjs.github.io/ktjs-iconify/`，API 文档在 `/api/`；README、模块
  README 和演示页导航都链接到这两个地址，改仓库名时要一起更新。
- 本地预览：执行 `./gradlew pagesSite` 后给 `build/pages` 起任意静态服务器。直接开
  `file://` 会因相对路径与 CORS 导致图标和文档跳转异常。

## 文档约定

- 需要跨会话、跨上下文感知的信息（环境约定、构建方式、模块结构、容易踩的坑）记录在本文件。
- 只与某个目录强相关、篇幅较长的说明，放在该目录下的 `AGENTS.md`（如 `ktjs-iconify-tools/AGENTS.md`），根文件只保留指向它的简短索引。
- 跨项目需要感知的信息也记录在本文件：本仓库会作为模板创建更多 wrapper 项目，约定应写成不依赖具体模块名的通用规则，而不是只活在当前模块或当前对话里。
- 公开声明都要有 KDoc：上游源码或 README 有说明的，翻译并改写过来；没有的按实际行为补充。描述以源码为准，不要照搬过时或错误的理解。
