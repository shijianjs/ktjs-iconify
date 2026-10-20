package iconify

import web.html.HTMLElement

/**
 * The `<iconify-icon>` custom element.
 *
 * [Online Documentation](https://iconify.design/docs/iconify-icon/)
 */
abstract external class IconifyIconElement : HTMLElement {
    /**
     * Icon name such as `mdi:home`, or icon data.
     */
    var icon: dynamic

    /**
     * Rendering mode: `style`, `bg`, `mask` or `svg`.
     */
    var mode: String?

    var inline: Boolean

    var noobserver: Boolean

    var width: String?

    var height: String?

    var rotate: String?

    var flip: String?

    var preserveAspectRatio: String?

    val status: String

    fun restartAnimation()
}
