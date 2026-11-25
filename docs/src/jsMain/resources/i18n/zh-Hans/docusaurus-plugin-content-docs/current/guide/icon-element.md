---
title: 图标元素
sidebar_position: 3
---

# 图标元素

`IconifyIconElement` 是继承 `web.html.HTMLElement` 的 `abstract external class`。它的成员分三类：上游手写
setter 的（`icon`、`inline`）、上游通用 attribute 反射装出来的（`mode`、`width`、`height`、`rotate`、
`flip`），以及它只按 attribute 读的两个（`noobserver`、`preserveAspectRatio`）。前两类都是属性，赋值时
会写 attribute 并触发重渲染；后两个则用类型化的 attribute 访问器。

| 成员 | 作用 |
| --- | --- |
| `icon` | 图标名（如 `mdi:home`），或图标数据 |
| `mode` | 渲染模式：`style`、`bg`、`mask`、`svg` |
| `inline` | 内联渲染（不走 shadow DOM），而不是块级 |
| `noObserverAttr` | `Attribute<Boolean>`：让图标在视口外也保持渲染 |
| `ariaHidden` | 继承自 `ARIAMixin` 的 `AriaHidden?`：把图标从辅助阅读器里隐藏 |
| `width` / `height` | 图标尺寸 |
| `rotate` / `flip` | 变换，例如 `"90deg"` 与 `"horizontal"` |
| `preserveAspectRatioAttr` | `Attribute<String>`：渲染出的 SVG 的 `preserveAspectRatio` |
| `status` | 当前加载状态 |
| `restartAnimation()` | 图标变化后重播 CSS 动画 |

`noobserver` 故意**不**做成属性：上游对它只装了通用的 attribute 反射，`false` 会写成
`noobserver="false"`——attribute 仍然存在，于是这个开关永远关不掉。`noObserverAttr` 在 `true` 时写裸
attribute，在 `false` 时删掉它。

`preserveAspectRatio` 同样没有属性，而且它是唯一一个渲染器会读、却不在 `observedAttributes` 里的
attribute：要在首次渲染前写 `preserveAspectRatioAttr`，否则当前图形会一直用旧值，直到别的被观察
attribute 变化。

`aria-hidden` 完全不需要自造访问器：它就是每个 `HTMLElement` 都继承的 `ariaHidden` 属性，类型是
`AriaHidden` 并集（`import web.aria.\`true\`` 之后写 `AriaHidden.\`true\``）。

```mermaid
flowchart LR
    name["图标名<br/>mdi:home"] --> lookup["图标数据"]
    lookup --> mode{"mode"}
    mode -->|svg| svg["SVG 元素"]
    mode -->|bg| bg["盒子上的 background-image"]
    mode -->|mask| mask["用 currentColor 的 CSS mask"]
    mode -->|style| style["上游默认"]
```

## 装饰性图标

页面上的大多数图标都在重复旁边的文字已经说的事——标题前的标记、带文字按钮里的图形、列表前的圆点。
`createDecorativeIcon` 把这类图标总需要的两件事预设好，再执行 `configure` 块：

```kotlin
import io.github.shijianjs.ktjs.iconify.createDecorativeIcon

val marker = createDecorativeIcon("lucide:layers") {
    inlineStyle.apply { fontSize = 18.px }
}
```

- `aria-hidden="true"` —— 辅助阅读器跳过这个图标。
- `noobserver` —— 图标被滚出视口或折叠隐藏后仍然保留。`iconify-icon` 一旦被 `IntersectionObserver`
  判定为不可见就丢掉自己的 `<svg>`，却把 `status` 留在 `"rendered"`，于是被隐藏过的菜单项下次打开是空
  的；这个 attribute 就是上游给的关闭开关。

`configure` 最后执行，所以两个预设都能覆盖。

## 三种模式

- `svg` 渲染真正的 `<svg>`，可以逐路径上色，但用不了 CSS 模式那套 `currentColor` 捷径。
- `bg` 把图标画成 `background-image`，颜色来自背景而不是图标。
- `mask` 把它当 CSS mask，于是图标吃 `color` —— 单色图标集通常用这个。
- `style` 是上游默认，按图标数据自己挑模式。

## 变换

`rotate` 收角度字符串，`flip` 收方向（`horizontal` / `vertical`）。两者都是 attribute 而不是 CSS transform，
所以在 `bg` 与 `mask` 模式下同样有效——那些模式里 CSS transform 是不管用的。

## 加载函数

| 函数 | 含义 |
| --- | --- |
| `iconLoaded(name)` | 数据是否已在本地 |
| `getIcon(name)` | 图标数据；失败给 `null`，从未加载给 `undefined` |
| `listIcons(provider?, prefix?)` | 已注册图标的名字 |
| `addIcon(name, data)` | 注册单个图标 |
| `addCollection(data, provider?)` | 注册整套 |
| `loadIcon(icon)` / `loadIcons(icons, callback?)` | 从 API 拉取 |
| `addAPIProvider(provider, config)` | 注册自定义 API 端点 |

## DOM 类型

公开 API 用 `kotlin-browser` 的类型体系：元素是 `web.html.*`，document 是 `web.dom.document`。不要混入
`org.w3c.dom.*` 或 `kotlinx.browser.*`——Detekt 的 `ForbiddenImport` 会让构建直接失败，因为混用之后封装就不再
能和 kotlin-browser 项目组合。
