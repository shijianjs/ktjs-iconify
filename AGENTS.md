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

## 文档约定

- 需要跨会话、跨上下文感知的信息（环境约定、构建方式、模块结构、容易踩的坑）记录在本文件。
- 只与某个目录强相关、篇幅较长的说明，放在该目录下的 `AGENTS.md`（如 `ktjs-iconify-tools/AGENTS.md`），根文件只保留指向它的简短索引。
