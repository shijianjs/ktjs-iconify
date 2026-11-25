---
title: The icon element
sidebar_position: 3
---

# The icon element

`IconifyIconElement` is an `abstract external class` extending `web.html.HTMLElement`. Its members
fall into three groups: the ones with hand-written setters upstream (`icon`, `inline`), the ones its
generic attribute reflection installs (`mode`, `width`, `height`, `rotate`, `flip`), and the two
fields it only reads as attributes (`noobserver`, `preserveAspectRatio`). Everything in the first two
groups is a property whose assignment writes the attribute and re-renders; the last two are typed
attribute accessors.

| Member | What it does |
| --- | --- |
| `icon` | icon name such as `mdi:home`, or icon data |
| `mode` | rendering mode: `style`, `bg`, `mask` or `svg` |
| `inline` | render inline (no shadow DOM) instead of as a block |
| `noObserverAttr` | `Attribute<Boolean>`: keep the glyph rendered outside the viewport |
| `ariaHidden` | `AriaHidden?` inherited from `ARIAMixin`: hide the icon from assistive readers |
| `width` / `height` | icon dimensions |
| `rotate` / `flip` | transformations, e.g. `"90deg"` and `"horizontal"` |
| `preserveAspectRatioAttr` | `Attribute<String>`: the rendered SVG's `preserveAspectRatio` |
| `status` | current loading status |
| `restartAnimation()` | restarts CSS animations after the icon changed |

`noobserver` is deliberately **not** a property: upstream installs generic attribute reflection for
it, which writes `noobserver="false"` — still a *present* attribute — so the flag could never be
turned off. `noObserverAttr` writes the bare attribute for `true` and removes it for `false`.

`preserveAspectRatio` has no property either, and it is the one attribute that is *read* by the
renderer yet missing from `observedAttributes`: write `preserveAspectRatioAttr` before the first
render, otherwise the current glyph keeps the old value until another observed attribute changes.

`aria-hidden` needs no custom accessor at all: it is the `ariaHidden` property every `HTMLElement`
inherits, typed as the `AriaHidden` union (`AriaHidden.ariaTrue` with the usual aliased import).

```mermaid
flowchart LR
    name["icon name<br/>mdi:home"] --> lookup["icon data"]
    lookup --> mode{"mode"}
    mode -->|svg| svg["SVG element"]
    mode -->|bg| bg["background-image on a box"]
    mode -->|mask| mask["CSS mask over currentColor"]
    mode -->|style| style["upstream default"]
```

## Decorative icons

Most icons on a page repeat what the text next to them already says — a marker before a heading, a
glyph inside a labelled button, a bullet in a list. `createDecorativeIcon` presets the two things
such an icon always needs and then applies its `configure` block:

```kotlin
import io.github.shijianjs.ktjs.iconify.createDecorativeIcon

val marker = createDecorativeIcon("lucide:layers") {
    inlineStyle.apply { fontSize = 18.px }
}
```

- `aria-hidden="true"` — the icon is skipped by assistive readers.
- `noobserver` — the glyph survives being scrolled or folded away. `iconify-icon` drops its own
  `<svg>` when an `IntersectionObserver` reports the element not visible, yet keeps
  `status === "rendered"`, so a hidden menu entry comes back blank; this attribute is the opt-out.

`configure` runs last, so either preset can still be overridden.

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
