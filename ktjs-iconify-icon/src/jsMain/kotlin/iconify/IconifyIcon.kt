package iconify

import kotlin.js.unsafeCast
import kotlinx.browser.document

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
    document.createElement("iconify-icon")
        .unsafeCast<IconifyIconElement>()
        .apply {
            registerIconifyIcon()
            this.icon = icon
            configure()
        }
