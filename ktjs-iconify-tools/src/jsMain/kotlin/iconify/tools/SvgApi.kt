@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.IconifyIcon
import iconify.utils.IconifyIconCustomisations

/**
 * SVG document used to manipulate icon content.
 */
external class SVG {
    constructor(content: String)

    /** Parsed SVG root element. */
    var `$svg`: dynamic

    var viewBox: IconViewBox

    @JsName("toString")
    fun toSvgString(
        customisations: IconifyIconCustomisations? = definedExternally,
        prettyPrint: Boolean = definedExternally,
    ): String

    fun toMinifiedString(customisations: IconifyIconCustomisations? = definedExternally): String

    fun toPrettyString(customisations: IconifyIconCustomisations? = definedExternally): String

    fun getBody(): String

    fun getIcon(): IconifyIcon

    fun load(content: String)
}
