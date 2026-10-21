# ktjs-iconify-icon

Kotlin/JS wrapper for the [`iconify-icon`](https://www.npmjs.com/package/iconify-icon) 3.0.4 Web
Component.

[English](README.md) | [简体中文](README.zh-CN.md)

[API reference](https://shijianjs.github.io/ktjs-iconify/api/ktjs-iconify-icon/)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-iconify-icon?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-iconify-icon)

**Wrapper version:** `3.0.4-kt.3`, tracking upstream `iconify-icon` 3.0.4. See the
[versioning rule](../README.md#versioning).

Browser only. Importing the module loads `iconify-icon`, which registers the `<iconify-icon>`
custom element; icons can come from the Iconify API or from locally registered icon data.

## Usage

```kotlin
import iconify.IconifyIcon
import iconify.addIcon
import iconify.createIconifyIcon
import kotlin.js.js
import kotlin.js.unsafeCast
import web.dom.document

val element = createIconifyIcon("mdi:home") {
    width = "1em"
    height = "1em"
    mode = "svg"
}
document.body?.appendChild(element)

// Or register icon data locally, without an API request.
val data = js(
    """({ body: '<path d="M0 0h24v24H0z"/>', width: 24, height: 24 })""",
).unsafeCast<IconifyIcon>()
addIcon("test:home", data)
```

`createIconifyIcon` registers the custom element on first use. `registerIconifyIcon()` is exposed
for cases where the component must be loaded before creating elements another way.

## Element API

`IconifyIconElement` is an `abstract external class` extending `web.html.HTMLElement`.

| Member | Description |
| --- | --- |
| `icon: dynamic` | Icon name such as `mdi:home`, or icon data |
| `mode: String?` | Rendering mode: `style`, `bg`, `mask` or `svg` |
| `inline: Boolean` | Render inline instead of using shadow DOM |
| `noobserver: Boolean` | Disable the mutation observer |
| `width`, `height: String?` | Icon dimensions |
| `rotate: String?`, `flip: String?` | Transformations |
| `preserveAspectRatio: String?` | SVG `preserveAspectRatio` |
| `status: String` | Current loading status |
| `restartAnimation()` | Restart CSS animations |

## API functions

| Function | Description |
| --- | --- |
| `iconLoaded(name)` | Whether icon data is available locally |
| `getIcon(name)` | Icon data, `null` on failure, `undefined` if never loaded |
| `listIcons(provider?, prefix?)` | List available icons |
| `addIcon(name, data)` | Add a single icon |
| `addCollection(data, provider?)` | Add an icon set |
| `loadIcon(icon)`, `loadIcons(icons, callback?)` | Load icons from the API |
| `addAPIProvider(provider, config)` | Register a custom API provider |

## DOM types

The public API uses the `kotlin-browser` type system: the element extends `web.html.HTMLElement`,
and helpers use `web.dom.document`. Do not mix in `org.w3c.dom.*` or `kotlinx.browser.*`; callers
using those declarations need an `unsafeCast` at the boundary. The build fails on
`org.w3c.dom` or `kotlinx.browser` imports via Detekt's `ForbiddenImport` rule.

The tag name is available as the typed `IconifyIconTagName.iconifyIcon` constant, ready for
`createElement`:

```kotlin
val element = document.createElement(IconifyIconTagName.iconifyIcon)
```

## Tests

```bash
./gradlew :ktjs-iconify-icon:jsBrowserTest
```

Tests run in Chromium through Playwright.
