---
title: The icon element
sidebar_position: 3
---

# The icon element

`IconifyIconElement` is an `abstract external class` extending `web.html.HTMLElement`; every upstream
attribute is a Kotlin property, so nothing is written through strings of HTML.

| Member | What it does |
| --- | --- |
| `icon` | icon name such as `mdi:home`, or icon data |
| `mode` | rendering mode: `style`, `bg`, `mask` or `svg` |
| `inline` | render inline (no shadow DOM) instead of as a block |
| `noobserver` | disable the mutation observer |
| `width` / `height` | icon dimensions |
| `rotate` / `flip` | transformations, e.g. `"90deg"` and `"horizontal"` |
| `preserveAspectRatio` | forwarded to the SVG |
| `status` | current loading status |
| `restartAnimation()` | restarts CSS animations after the icon changed |

```mermaid
flowchart LR
    name["icon name<br/>mdi:home"] --> lookup["icon data"]
    lookup --> mode{"mode"}
    mode -->|svg| svg["SVG element"]
    mode -->|bg| bg["background-image on a box"]
    mode -->|mask| mask["CSS mask over currentColor"]
    mode -->|style| style["upstream default"]
```

## Modes

- `svg` renders an `<svg>`; it can be styled per path but cannot use `currentColor` shortcuts of the CSS modes.
- `bg` paints the icon as a `background-image`, so the colour comes from the background, not the icon.
- `mask` paints it as a CSS mask, which means the icon takes `color` — the usual choice for monochrome sets.
- `style` is upstream's default and picks a mode from the icon data.

## Transformations

`rotate` takes an angle string and `flip` a direction (`horizontal` / `vertical`). Both are attributes rather
than CSS transforms, so they also apply in `bg` and `mask` modes, where a CSS transform would not work.

## Loader functions

| Function | Meaning |
| --- | --- |
| `iconLoaded(name)` | whether the data is available locally |
| `getIcon(name)` | the data; `null` on failure, `undefined` when never loaded |
| `listIcons(provider?, prefix?)` | names of the registered icons |
| `addIcon(name, data)` | register one icon |
| `addCollection(data, provider?)` | register a whole set |
| `loadIcon(icon)` / `loadIcons(icons, callback?)` | fetch from the API |
| `addAPIProvider(provider, config)` | register a custom API endpoint |

## DOM types

The public API uses the `kotlin-browser` type system: elements are `web.html.*`, the document is
`web.dom.document`. Do not mix in `org.w3c.dom.*` or `kotlinx.browser.*` — the build fails on those imports
through Detekt's `ForbiddenImport`, because the wrappers would stop composing with kotlin-browser projects.
