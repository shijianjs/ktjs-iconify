# ktjs-iconify-types

Kotlin/JS type declarations for [`@iconify/types`](https://www.npmjs.com/package/@iconify/types)
2.0.0.

[English](README.md) | [简体中文](README.zh-CN.md)

**Wrapper version:** `2.0.0-kt.1`, tracking upstream `@iconify/types` 2.0.0. See the
[versioning rule](../README.md#versioning).

This module contains types only. `@iconify/types` ships no runtime code, so there is no npm
dependency; the declarations describe the JSON structures used by Iconify icon sets and the
Iconify API.

## Types

| Type | Purpose |
| --- | --- |
| `IconifyDimenisons` | `left`, `top`, `width`, `height` defaults |
| `IconifyTransformations` | `rotate`, `hFlip`, `vFlip` |
| `IconifyOptional` | Dimensions plus transformations |
| `IconifyIcon` | Icon data: `body` and optional properties |
| `IconifyAlias` | Alias entry: `parent` and optional properties |
| `ExtendedIconifyIcon`, `ExtendedIconifyAlias` | Entries with the `hidden` flag |
| `IconifyInfo`, `IconifyInfoAuthor`, `IconifyInfoLicense` | Icon set metadata block |
| `IconifyMetaData` | `info`, `chars`, `categories`, `themes`, `prefixes`, `suffixes` |
| `IconifyJSONIconsData` | `prefix`, `provider`, `icons`, `aliases` |
| `IconifyJSON` | Full icon set JSON: icons data plus metadata |
| `IconifyJSONPackageExports` | Shape of an `@iconify-json/{prefix}` package |
| `IconifyIconName` | A name split into `provider`, `prefix`, `name` |
| `IconifyAPIProviderConfig` | Custom API provider configuration |

`ProviderTypes.kt` also declares `APIProviderRawData`, `APIProviderRawDataLinks` and
`APIProviderRawDataNPM`, which describe the data returned by `https://api.iconify.design`.

## Mapping notes

TypeScript index signatures (`Record<string, IconifyIcon>` and similar) are declared as `dynamic`,
because Kotlin has no index signatures. This affects `icons`, `aliases`, `chars`, `categories`,
`themes`, `prefixes` and `suffixes`; cast individual entries when you need typed access.

## Usage

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

The same interfaces are used by `ktjs-iconify-utils` and `ktjs-iconify-tools`, so icon data loaded
from JSON can be passed to those modules without conversion.

## Tests

```bash
./gradlew :ktjs-iconify-types:jsNodeTest
```
