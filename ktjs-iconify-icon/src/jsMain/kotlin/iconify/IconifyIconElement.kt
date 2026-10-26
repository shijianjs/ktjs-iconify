package iconify

import js.reflect.unsafeCast
import web.html.HTMLElement

/**
 * Upstream `string | IconifyIcon`: an icon name such as `mdi:home`, serialised icon data, or icon
 * data itself.
 */
external interface StringOrIconifyIcon

fun StringOrIconifyIcon(value: String): StringOrIconifyIcon = unsafeCast(value)

fun StringOrIconifyIcon(value: IconifyIcon): StringOrIconifyIcon = unsafeCast(value)

/**
 * The `<iconify-icon>` custom element.
 *
 * Renders icons from the Iconify API or from locally registered icon data. The rendered icon
 * lives in the element's shadow DOM.
 *
 * [Online Documentation](https://iconify.design/docs/iconify-icon/)
 */
abstract external class IconifyIconElement : HTMLElement {
    /**
     * Icon to render: a name such as `mdi:home`, icon data, or serialised icon data.
     *
     * Changing the value loads the icon if needed and re-renders the element.
     */
    var icon: StringOrIconifyIcon

    /**
     * Rendering mode: `style`, `bg`, `mask` or `svg`.
     *
     * - `svg`: render an `<svg>` element.
     * - `bg`: render a `<span>` with the icon as background.
     * - `mask`: render a `<span>` masked with the icon, following `currentColor`.
     * - `style`: use `bg` or `mask`, depending on the icon content.
     *
     * Defaults to automatic mode: SVG for most icons, `bg` or `mask` for animated icons.
     */
    var mode: String?

    /**
     * Align the icon with text by adding `vertical-align: -0.125em`.
     */
    var inline: Boolean

    /**
     * Render the icon even when it is outside the viewport.
     *
     * By default rendering is delayed until the element becomes visible.
     */
    var noobserver: Boolean

    /**
     * Icon width, as a CSS length. By default the width follows the icon's aspect ratio at 1em
     * height.
     */
    var width: String?

    /**
     * Icon height, as a CSS length. Defaults to `1em`.
     */
    var height: String?

    /**
     * Rotation: `90deg`, `180deg` or `270deg` (equivalent to 1, 2 or 3 quarter turns).
     */
    var rotate: String?

    /**
     * Flip: `horizontal`, `vertical`, or both separated by a comma.
     */
    var flip: String?

    /**
     * Value of the rendered SVG's `preserveAspectRatio` attribute.
     */
    var preserveAspectRatio: String?

    /**
     * Current icon status: `rendered`, `loading` or `failed`.
     *
     * `failed` means the icon is known to be missing.
     */
    val status: String

    /**
     * Restarts animations in the rendered icon.
     */
    fun restartAnimation()
}
