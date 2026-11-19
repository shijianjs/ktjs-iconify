---
title: Tools
sidebar_position: 5
---

# Tools

`ktjs-iconify-tools` 封装 `@iconify/tools` 5.0.14，也就是生态里 Node.js 那一侧：读 SVG 目录、产出已发布的图标集
的脚本。浏览器只需要 `ktjs-iconify-utils`；这个模块依赖它，另加文件系统与优化部分。

| 领域 | 代表 API |
| --- | --- |
| SVG | `SVG` 类：`viewBox`、`getBody`、`getIcon`、`toSvgString`、`toMinifiedString`、`toPrettyString` |
| 图标集 | `IconSet` 类、`blankIconSet`、`sortThemeKeys` |
| 清理 | `cleanupSVG`、`parseSVG`、`parseSVGStyle`、`analyseSVGStructure`、`checkBadTags`、`removeBadAttributes` |
| 优化 | `runSVGO`、`deOptimisePaths`、`resetSVGOrigin`、`convertSVGToMask`、`scaleSVG`、`cleanupGlobalStyle` |
| 导入导出 | `importDirectory`、`importDirectorySync`、`exportToDirectory`、`exportIconPackage`、`exportJSONPackage`、`mergeIconSets`、`addTagsToIconSet` |
| 颜色 | `parseColors`、`isEmptyColor`、`validateColors`、`detectIconSetPalette` |
| 下载 | `downloadPackage`、`downloadNPMPackage`、`downloadGitHubRepo`、`downloadGitLabRepo`、`unzip`、`untar`、`importFromFigma` |
| 其他 | `writeJSONFile`、`scanDirectory`、`compareDirectories`、`execAsync`、`cleanupIconKeyword`、`bumpVersion` |

```mermaid
flowchart LR
    dir["SVG 文件"] --> imp["importDirectory"]
    imp --> set["IconSet"]
    set --> clean["cleanupSVG / runSVGO"]
    clean --> colours["parseColors"]
    colours --> exp["导出：图标集 JSON<br/>或 npm / JSON 包"]
```

```kotlin
import io.github.shijianjs.ktjs.iconify.tools.SVG
import io.github.shijianjs.ktjs.iconify.tools.blankIconSet
import io.github.shijianjs.ktjs.iconify.tools.cleanupSVG

val svg = SVG("""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M0 0h24v24H0z"/></svg>""")
cleanupSVG(svg)

val iconSet = blankIconSet("my-icons")
iconSet.fromSVG("home", svg)
iconSet.setAlias("house", "home")

val json = iconSet.export()
```

`IconSet` 覆盖 `resolve`、`list`、`forEach`、`getTree`、`toSVG`、`toHtml`、`export`、`exists`、`remove`、
`rename`、`setIcon`、`setAlias`、`setVariation`、`fromSVG`、`chars`，以及分类与主题相关辅助。

## 上游只有 ESM

`@iconify/tools` 是纯 ESM，而它的根导出没有 `require` 条件。封装从公开的 `@iconify/tools/lib/index.js` 子路径
导入，这样 ES module 与 CommonJS 消费方都能用；CommonJS 消费方另外需要 `require(ESM)` 支持
（Node.js 20.19+、22.12+ 或 24）。
