package iconify

import web.dom.TagName
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
    document.createElement(TagName<IconifyIconElement>("iconify-icon"))
        .apply {
            registerIconifyIcon()
            this.icon = icon
            configure()
        }
