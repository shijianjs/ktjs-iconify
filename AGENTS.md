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

每个子模块独立版本，格式为 `{上游 npm 版本}-kt.{修订号}`。上游 npm 版本只在根目录 `package.json` 的 `dependencies` 中声明，根 `build.gradle.kts` 解析后放入 `extra["npmVersions"]`，各子模块读取它来声明 `npm(...)` 并推导版本基础部分：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：只改 `package.json`，基础版本自动跟随，修订号重置为 `kt.1`；即使 wrapper 代码没变，也要按新上游版本发布。
- 上游不变、只修订 wrapper：递增 `build.gradle.kts` 里的修订号（`kt.2`、`kt.3`…）。
- 不要再把 npm 版本硬编码回 `build.gradle.kts`。
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

## 文档约定

- 需要跨会话、跨上下文感知的信息（环境约定、构建方式、模块结构、容易踩的坑）记录在本文件。
- 只与某个目录强相关、篇幅较长的说明，放在该目录下的 `AGENTS.md`（如 `ktjs-iconify-tools/AGENTS.md`），根文件只保留指向它的简短索引。
