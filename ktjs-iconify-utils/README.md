# ktjs-iconify-utils

Kotlin/JS wrapper for [`@iconify/utils`](https://www.npmjs.com/package/@iconify/utils) 3.1.7.
Works in browsers and Node.js.

[English](README.md) | [简体中文](README.zh-CN.md)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-utils?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-utils)

**Wrapper version:** `3.1.7-kt.2`, tracking upstream `@iconify/utils` 3.1.7. See the
[versioning rule](../README.md#versioning).

Depends on [`ktjs-iconify-types`](../ktjs-iconify-types/README.md) and re-exports its types with
`api`. The npm dependency is bundled through Kotlin/JS `npm(...)`.

## API groups

| Area | Representative functions |
| --- | --- |
| Icon set parsing | `getIconData`, `getIcons`, `parseIconSet`, `parseIconSetAsync`, `validateIconSet`, `quicklyValidateIconSet`, `expandIconSet`, `minifyIconSet`, `getIconsTree` |
| Icon helpers | `mergeIconData`, `mergeIconTransformations`, `makeIconSquare`, `makeViewBoxSquare`, `stringToIcon`, `validateIconName` |
| SVG | `iconToSVG`, `iconToHTML`, `calculateSize`, `getSVGViewBox`, `svgToData`, `svgToURL`, `splitSVGDefs`, `replaceIDs`, `trimSVG`, `prettifySVG`, `encodeSvgForCss`, `parseSVGContent` |
| CSS | `getIconCSS`, `getIconContentCSS`, `getIconsCSS`, `getIconsContentCSS` |
| Customisations | `mergeCustomisations`, `flipFromString`, `rotateFromString`, `toBoolean` |
| Colors | `stringToColor`, `compareColors`, `colorToHexString`, `colorToString` |
| Emoji | `parseEmojiSequence`, `replaceEmojisInText`, `findAndReplaceEmojisInText` |
| Loader | `loadIcon`, `getCustomIcon`, `searchForIcon`, `mergeIconProps`, `getPossibleIconNames` |
| Misc | `sanitiseTitleAttribute` and string/object helpers |

## Usage

```kotlin
import iconify.IconifyJSON
import iconify.utils.getIconCSS
import iconify.utils.getIconData
import iconify.utils.iconToHTML
import iconify.utils.iconToSVG
import kotlin.js.js

fun renderHome(iconSet: IconifyJSON): String {
    val icon = getIconData(iconSet, "home") ?: return ""

    val built = iconToSVG(icon, js("({ width: 48, height: 48 })"))
    val svg = iconToHTML(built.body, built.attributes)

    // Rendered as a CSS mask; defaults to `.icon--{prefix}` / `.icon--{prefix}--{name}` selectors.
    val css = getIconCSS(icon, js("({ mode: 'mask' })"))

    return svg + css
}
```

Options and customisations are external interfaces. Build them with `js("({ ... })")`; their
properties mirror the upstream TypeScript option objects.

## Mapping notes

- Union types such as `number | string | null` are declared as `dynamic`.
- `SVGViewBox` is a four-number array `[left, top, width, height]`.
- Callbacks whose `undefined` return value has meaning (`findAndReplaceEmojisInText`,
  `parseSVGStyle` in tools) are typed to return `dynamic` rather than `Unit`.
- Index-signature maps in icon data remain `dynamic`, as in `ktjs-iconify-types`.

## Tests

```bash
./gradlew :ktjs-iconify-utils:jsBrowserTest
```

Tests run in Chromium through Playwright.
