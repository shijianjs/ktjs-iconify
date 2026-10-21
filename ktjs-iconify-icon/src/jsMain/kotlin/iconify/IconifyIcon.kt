package iconify

import web.dom.document

/**
 * Imports `iconify-icon`, which registers the `<iconify-icon>` custom element.
 */
fun registerIconifyIcon(): dynamic = iconifyIconComponent

/**
 * Creates an `<iconify-icon>` element displaying [icon].
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
