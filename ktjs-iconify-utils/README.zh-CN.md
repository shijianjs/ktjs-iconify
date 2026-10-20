# ktjs-iconify-utils

[`@iconify/utils`](https://www.npmjs.com/package/@iconify/utils) 3.1.7 的 Kotlin/JS wrapper，
可在浏览器和 Node.js 中运行。

[English](README.md) | [简体中文](README.zh-CN.md)

依赖 [`ktjs-iconify-types`](../ktjs-iconify-types/README.zh-CN.md)，并通过 `api` 转出其中的
类型。npm 依赖通过 Kotlin/JS 的 `npm(...)` 引入。

## API 分组

| 领域 | 代表函数 |
| --- | --- |
| 图标集解析 | `getIconData`、`getIcons`、`parseIconSet`、`parseIconSetAsync`、`validateIconSet`、`quicklyValidateIconSet`、`expandIconSet`、`minifyIconSet`、`getIconsTree` |
| 图标辅助 | `mergeIconData`、`mergeIconTransformations`、`makeIconSquare`、`makeViewBoxSquare`、`stringToIcon`、`validateIconName` |
| SVG | `iconToSVG`、`iconToHTML`、`calculateSize`、`getSVGViewBox`、`svgToData`、`svgToURL`、`splitSVGDefs`、`replaceIDs`、`trimSVG`、`prettifySVG`、`encodeSvgForCss`、`parseSVGContent` |
| CSS | `getIconCSS`、`getIconContentCSS`、`getIconsCSS`、`getIconsContentCSS` |
| 自定义属性 | `mergeCustomisations`、`flipFromString`、`rotateFromString`、`toBoolean` |
| 颜色 | `stringToColor`、`compareColors`、`colorToHexString`、`colorToString` |
| Emoji | `parseEmojiSequence`、`replaceEmojisInText`、`findAndReplaceEmojisInText` |
| Loader | `loadIcon`、`getCustomIcon`、`searchForIcon`、`mergeIconProps`、`getPossibleIconNames` |
| 其他 | `sanitiseTitleAttribute` 以及字符串/对象辅助函数 |

## 用法

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

    // 以 CSS mask 渲染；默认选择器为 `.icon--{prefix}` / `.icon--{prefix}--{name}`
    val css = getIconCSS(icon, js("({ mode: 'mask' })"))

    return svg + css
}
```

选项和自定义参数都是 external interface，用 `js("({ ... })")` 构造，字段名与上游 TypeScript
的选项对象一致。

## 映射说明

- `number | string | null` 这类联合类型声明为 `dynamic`。
- `SVGViewBox` 是四元素数组 `[left, top, width, height]`。
- 返回值 `undefined` 具有语义的回调（如 `findAndReplaceEmojisInText`，以及 tools 中的
  `parseSVGStyle`）声明为返回 `dynamic` 而不是 `Unit`。
- 图标数据中的索引签名映射与 `ktjs-iconify-types` 一致，仍为 `dynamic`。

## 测试

```bash
./gradlew :ktjs-iconify-utils:jsBrowserTest
```

测试通过 Playwright 在 Chromium 中运行。
