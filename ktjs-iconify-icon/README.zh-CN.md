# ktjs-iconify-icon

[`iconify-icon`](https://www.npmjs.com/package/iconify-icon) 3.0.4 Web Component 的 Kotlin/JS
wrapper。

[English](README.md) | [简体中文](README.zh-CN.md)

**Wrapper 版本：** `3.0.4-kt.1`，对应上游 `iconify-icon` 3.0.4。规则见
[版本约定](../README.zh-CN.md#版本约定)。

仅面向浏览器。导入模块即加载 `iconify-icon` 并注册 `<iconify-icon>` 自定义元素；图标既可来自
Iconify API，也可来自本地注册的图标数据。

## 用法

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

// 也可以本地注册图标数据，避免 API 请求
val data = js(
    """({ body: '<path d="M0 0h24v24H0z"/>', width: 24, height: 24 })""",
).unsafeCast<IconifyIcon>()
addIcon("test:home", data)
```

`createIconifyIcon` 在首次调用时注册自定义元素。如果需要在其他方式创建元素前就加载组件，可
直接调用 `registerIconifyIcon()`。

## 元素 API

`IconifyIconElement` 是继承 `web.html.HTMLElement` 的 `abstract external class`。

| 成员 | 说明 |
| --- | --- |
| `icon: dynamic` | 图标名（如 `mdi:home`）或图标数据 |
| `mode: String?` | 渲染模式：`style`、`bg`、`mask`、`svg` |
| `inline: Boolean` | 使用内联渲染而不是 shadow DOM |
| `noobserver: Boolean` | 关闭 mutation observer |
| `width`、`height: String?` | 图标尺寸 |
| `rotate: String?`、`flip: String?` | 变换 |
| `preserveAspectRatio: String?` | SVG 的 `preserveAspectRatio` |
| `status: String` | 当前加载状态 |
| `restartAnimation()` | 重新播放 CSS 动画 |

## API 函数

| 函数 | 说明 |
| --- | --- |
| `iconLoaded(name)` | 图标数据是否已在本地 |
| `getIcon(name)` | 图标数据；失败为 `null`，从未加载为 `undefined` |
| `listIcons(provider?, prefix?)` | 列出可用图标 |
| `addIcon(name, data)` | 添加单个图标 |
| `addCollection(data, provider?)` | 添加图标集 |
| `loadIcon(icon)`、`loadIcons(icons, callback?)` | 从 API 加载图标 |
| `addAPIProvider(provider, config)` | 注册自定义 API provider |

## DOM 类型

公开 API 使用 `kotlin-browser` 类型体系：元素继承 `web.html.HTMLElement`，辅助函数使用
`web.dom.document`。不要混入 `org.w3c.dom.*` 或 `kotlinx.browser.*`；使用这些声明的调用方需要
在边界处做 `unsafeCast`。构建时 Detekt 的 `ForbiddenImport` 规则会拦截 `org.w3c.dom` 或
`kotlinx.browser` 的 import。

标签名通过 `IconifyIconTagName.iconifyIcon` 提供类型化常量，可直接传给 `createElement`：

```kotlin
val element = document.createElement(IconifyIconTagName.iconifyIcon)
```

## 测试

```bash
./gradlew :ktjs-iconify-icon:jsBrowserTest
```

测试通过 Playwright 在 Chromium 中运行。
