package io.github.shijianjs.ktjs.iconify

import web.aria.AriaHidden
import web.aria.`true`
import web.dom.document

/**
 * Imports `iconify-icon`, which registers the `<iconify-icon>` custom element, and returns the
 * component.
 */
fun registerIconifyIcon(): IconifyIconComponent = IconifyModule.IconifyIconComponent

/**
 * Creates an `<iconify-icon>` element displaying [icon].
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param icon icon name such as `mdi:home`, or serialised icon data.
 * @param configure applied to the new element after [icon] is set.
 */
fun createIconifyIcon(
    icon: String,
    configure: IconifyIconElement.() -> Unit = {},
): IconifyIconElement =
    document.createElement(IconifyIconTagName.iconifyIcon)
        .apply {
            registerIconifyIcon()
            this.icon = StringOrIconifyIcon(icon)
            configure()
        }

/**
 * Creates an `<iconify-icon>` for an icon that is **decoration**: a mark next to a label, a bullet
 * in a list, a glyph inside a button that already has its own text.
 *
 * Both presets are what almost every call site used to write by hand, and both are the reason the
 * element needed raw `setAttribute` strings:
 *
 * - `aria-hidden="true"` — the icon repeats what the nearby text already says, so an assistive
 *   reader should skip it. `ariaHidden` is the `ARIAMixin` property every element carries, typed
 *   `AriaHidden?`, so this is a property assignment rather than a string.
 * - `noobserver` — `iconify-icon` drops its own `<svg>` as soon as its `IntersectionObserver`
 *   reports the element as not visible, yet keeps `status === "rendered"`; a hidden menu entry
 *   then comes back blank on the next open. [noObserverAttr] writes the attribute the way upstream
 *   reads it (presence, not `"false"`).
 *
 * [configure] runs last, so a caller can still override either preset (for example, a decorated
 * icon that does need a label).
 *
 * @param icon icon name such as `mdi:home`, or serialised icon data.
 */
fun createDecorativeIcon(
    icon: String,
    configure: IconifyIconElement.() -> Unit = {},
): IconifyIconElement =
    createIconifyIcon(icon) {
        ariaHidden = AriaHidden.`true`
        noObserverAttr.value = true
        configure()
    }
