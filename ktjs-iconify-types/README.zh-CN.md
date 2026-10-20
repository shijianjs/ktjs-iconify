# ktjs-iconify-types

[`@iconify/types`](https://www.npmjs.com/package/@iconify/types) 2.0.0 的 Kotlin/JS 类型声明。

[English](README.md) | [简体中文](README.zh-CN.md)

**Wrapper 版本：** `2.0.0-kt.1`，对应上游 `@iconify/types` 2.0.0。规则见
[版本约定](../README.zh-CN.md#版本约定)。

本模块只有类型。`@iconify/types` 不包含运行时代码，因此这里没有 npm 依赖；这些声明描述了
Iconify 图标集与 Iconify API 使用的 JSON 结构。

## 类型

| 类型 | 用途 |
| --- | --- |
| `IconifyDimenisons` | `left`、`top`、`width`、`height` 默认值 |
| `IconifyTransformations` | `rotate`、`hFlip`、`vFlip` |
| `IconifyOptional` | 尺寸与变换的组合 |
| `IconifyIcon` | 图标数据：`body` 及可选属性 |
| `IconifyAlias` | 别名条目：`parent` 及可选属性 |
| `ExtendedIconifyIcon`、`ExtendedIconifyAlias` | 带 `hidden` 标记的条目 |
| `IconifyInfo`、`IconifyInfoAuthor`、`IconifyInfoLicense` | 图标集元信息 |
| `IconifyMetaData` | `info`、`chars`、`categories`、`themes`、`prefixes`、`suffixes` |
| `IconifyJSONIconsData` | `prefix`、`provider`、`icons`、`aliases` |
| `IconifyJSON` | 完整图标集 JSON：图标数据加元信息 |
| `IconifyJSONPackageExports` | `@iconify-json/{prefix}` 包的导出结构 |
| `IconifyIconName` | 拆分为 `provider`、`prefix`、`name` 的图标名 |
| `IconifyAPIProviderConfig` | 自定义 API provider 配置 |

`ProviderTypes.kt` 还声明了 `APIProviderRawData`、`APIProviderRawDataLinks`、
`APIProviderRawDataNPM`，用于描述 `https://api.iconify.design` 返回的数据。

## 映射说明

TypeScript 的索引签名（`Record<string, IconifyIcon>` 等）声明为 `dynamic`，因为 Kotlin 没有
索引签名。受影响的字段有 `icons`、`aliases`、`chars`、`categories`、`themes`、`prefixes`、
`suffixes`；需要类型化访问时对单个条目做转换。

## 用法

```kotlin
import iconify.IconifyJSON
import kotlin.js.js
import kotlin.js.unsafeCast

val iconSet: IconifyJSON = js(
    """({
        prefix: 'mdi',
        width: 24,
        height: 24,
        icons: { home: { body: '<path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>' } }
    })""",
)

fun iconCount(data: IconifyJSON): Int =
    js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(data.icons).size
```

`ktjs-iconify-utils` 与 `ktjs-iconify-tools` 复用同一组接口，从 JSON 加载的图标数据可以直接传入，
不需要转换。

## 测试

```bash
./gradlew :ktjs-iconify-types:jsNodeTest
```
