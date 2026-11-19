---
title: 图标元素
sidebar_position: 3
---

# 图标元素

`IconifyIconElement` 是继承 `web.html.HTMLElement` 的 `abstract external class`；上游的每个 attribute 都是
Kotlin 属性，不通过拼 HTML 字符串来写。

| 成员 | 作用 |
| --- | --- |
| `icon` | 图标名（如 `mdi:home`），或图标数据 |
| `mode` | 渲染模式：`style`、`bg`、`mask`、`svg` |
| `inline` | 内联渲染（不走 shadow DOM），而不是块级 |
| `noobserver` | 关掉 mutation observer |
| `width` / `height` | 图标尺寸 |
| `rotate` / `flip` | 变换，例如 `"90deg"` 与 `"horizontal"` |
| `preserveAspectRatio` | 转发给 SVG |
| `status` | 当前加载状态 |
| `restartAnimation()` | 图标变化后重播 CSS 动画 |

```mermaid
flowchart LR
    name["图标名<br/>mdi:home"] --> lookup["图标数据"]
    lookup --> mode{"mode"}
    mode -->|svg| svg["SVG 元素"]
    mode -->|bg| bg["盒子上的 background-image"]
    mode -->|mask| mask["用 currentColor 的 CSS mask"]
    mode -->|style| style["上游默认"]
```

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
