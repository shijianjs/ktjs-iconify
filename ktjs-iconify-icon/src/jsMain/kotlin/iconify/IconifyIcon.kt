package iconify

import web.dom.document

/**
 * Imports `iconify-icon`, which registers the `<iconify-icon>` custom element, and returns the
 * component.
 */
fun registerIconifyIcon(): dynamic = iconifyIconComponent

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
            this.icon = icon
            configure()
        }
