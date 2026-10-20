# ktjs-iconify-tools

Kotlin/JS wrapper for [`@iconify/tools`](https://www.npmjs.com/package/@iconify/tools) 5.0.14.

[English](README.md) | [简体中文](README.zh-CN.md)

Node.js only. For browser-side icon parsing and SVG/CSS generation use
[`ktjs-iconify-utils`](../ktjs-iconify-utils/README.md), which this module depends on.

## Requirements

`@iconify/tools` is ESM-only, while this project emits CommonJS. The wrapper imports the package
through its public `@iconify/tools/lib/index.js` subpath, which means consumers need a Node.js
version with `require(ESM)` support: 20.19+, 22.12+ or 24. See
[AGENTS.md](AGENTS.md) for the full explanation.

## API

| Area | Representative API |
| --- | --- |
| SVG | `SVG` class: `viewBox`, `getBody`, `getIcon`, `toSvgString`, `toMinifiedString`, `toPrettyString` |
| Icon sets | `IconSet` class, `blankIconSet`, `sortThemeKeys` |
| Cleanup | `cleanupSVG`, `parseSVG`, `parseSVGStyle`, `analyseSVGStructure`, `checkBadTags`, `removeBadAttributes` |
| Optimisation | `runSVGO`, `deOptimisePaths`, `resetSVGOrigin`, `convertSVGToMask`, `scaleSVG`, `cleanupGlobalStyle` |
| Import/export | `importDirectory`, `importDirectorySync`, `exportToDirectory`, `exportIconPackage`, `exportJSONPackage`, `mergeIconSets`, `addTagsToIconSet` |
| Colors | `parseColors`, `isEmptyColor`, `validateColors`, `detectIconSetPalette` |
| Download | `downloadPackage`, `downloadNPMPackage`, `downloadGitHubRepo`, `downloadGitLabRepo`, `downloadGitRepo`, `unzip`, `untar`, `importFromFigma` |
| Misc | `writeJSONFile`, `scanDirectory`, `compareDirectories`, `execAsync`, `cleanupIconKeyword`, `bumpVersion` |

`IconSet` covers `resolve`, `list`, `forEach`, `getTree`, `toSVG`, `toHtml`, `export`, `exists`,
`remove`, `rename`, `setIcon`, `setAlias`, `setVariation`, `fromSVG`, `chars`, category and theme
helpers.

## Usage

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

## Semantic notes

- `IconSet.list(types?)` returns only `icon` and `variation` entries by default. Pass
  `arrayOf("icon", "alias")` (or `arrayOf("icon", "variation", "alias")`) to include aliases.
- `SVG.viewBox` is an object with `left`, `top`, `width` and `height`. This differs from
  `SVGViewBox` in `ktjs-iconify-utils`, which is a four-number array.
- `IconSet.forEach` is asynchronous and returns `Promise<Unit>`; `forEachSync` is the synchronous
  variant.

## Tests

```bash
./gradlew :ktjs-iconify-tools:jsNodeTest
```
