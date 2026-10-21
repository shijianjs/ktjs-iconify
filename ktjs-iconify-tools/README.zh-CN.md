# ktjs-iconify-tools

[`@iconify/tools`](https://www.npmjs.com/package/@iconify/tools) 5.0.14 的 Kotlin/JS wrapper。

[English](README.md) | [简体中文](README.zh-CN.md)

[API 文档](https://shijianjs.github.io/ktjs-iconify/api/ktjs-iconify-tools/)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-tools?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-tools)

**Wrapper 版本：** `5.0.14-kt.2`，对应上游 `@iconify/tools` 5.0.14。规则见
[版本约定](../README.zh-CN.md#版本约定)。

仅面向 Node.js。浏览器侧的图标解析与 SVG/CSS 生成请使用
[`ktjs-iconify-utils`](../ktjs-iconify-utils/README.zh-CN.md)，本模块依赖它。

## 环境要求

`@iconify/tools` 是纯 ESM 包，而本项目输出 CommonJS。wrapper 通过该包公开的
`@iconify/tools/lib/index.js` 子路径导入，因此使用方需要支持 `require(ESM)` 的 Node.js 版本：
20.19+、22.12+ 或 24。完整说明见 [AGENTS.md](AGENTS.md)。

## API

| 领域 | 代表 API |
| --- | --- |
| SVG | `SVG` 类：`viewBox`、`getBody`、`getIcon`、`toSvgString`、`toMinifiedString`、`toPrettyString` |
| 图标集 | `IconSet` 类、`blankIconSet`、`sortThemeKeys` |
| 清理 | `cleanupSVG`、`parseSVG`、`parseSVGStyle`、`analyseSVGStructure`、`checkBadTags`、`removeBadAttributes` |
| 优化 | `runSVGO`、`deOptimisePaths`、`resetSVGOrigin`、`convertSVGToMask`、`scaleSVG`、`cleanupGlobalStyle` |
| 导入导出 | `importDirectory`、`importDirectorySync`、`exportToDirectory`、`exportIconPackage`、`exportJSONPackage`、`mergeIconSets`、`addTagsToIconSet` |
| 颜色 | `parseColors`、`isEmptyColor`、`validateColors`、`detectIconSetPalette` |
| 下载 | `downloadPackage`、`downloadNPMPackage`、`downloadGitHubRepo`、`downloadGitLabRepo`、`downloadGitRepo`、`unzip`、`untar`、`importFromFigma` |
| 其他 | `writeJSONFile`、`scanDirectory`、`compareDirectories`、`execAsync`、`cleanupIconKeyword`、`bumpVersion` |

`IconSet` 覆盖 `resolve`、`list`、`forEach`、`getTree`、`toSVG`、`toHtml`、`export`、`exists`、
`remove`、`rename`、`setIcon`、`setAlias`、`setVariation`、`fromSVG`、`chars` 以及分类和主题
相关方法。

## 用法

```kotlin
import iconify.tools.SVG
import iconify.tools.blankIconSet
import iconify.tools.cleanupSVG

val svg = SVG(
    """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
           <path d="M0 0h24v24H0z"/>
       </svg>""",
)
cleanupSVG(svg)

val iconSet = blankIconSet("my-icons")
iconSet.fromSVG("home", svg)
iconSet.setAlias("house", "home")

val json = iconSet.export()
```

## 语义注意

- `IconSet.list(types?)` 默认只返回 `icon` 和 `variation`，不包含别名。需要列出别名时传
  `arrayOf("icon", "alias")`（或 `arrayOf("icon", "variation", "alias")`）。
- `SVG.viewBox` 是带 `left`、`top`、`width`、`height` 的对象，与 `ktjs-iconify-utils` 中
  四元素数组形式的 `SVGViewBox` 不同。
- `IconSet.forEach` 是异步的，返回 `Promise<Unit>`；同步版本为 `forEachSync`。

## 测试

```bash
./gradlew :ktjs-iconify-tools:jsNodeTest
```
