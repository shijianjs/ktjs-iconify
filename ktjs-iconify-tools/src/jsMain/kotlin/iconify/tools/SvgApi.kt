@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.IconifyIcon
import iconify.utils.IconifyIconCustomisations

/**
 * SVG document used to manipulate icon content.
 *
 * The constructor parses and validates [content]. After modifying the parsed tree, call
 * [load] again when you need to re-parse the document.
 */
external class SVG {
    /**
     * Parse an SVG document. Throws when the content is not a single `<svg>` element or
     * has neither a valid `viewBox` nor valid width and height.
     */
    constructor(content: String)

    /** Parsed SVG root element. */
    var `$svg`: ParsedXMLTagElement

    /** ViewBox as `{ left, top, width, height }`. */
    var viewBox: IconViewBox

    /**
     * Get the SVG as a string.
     *
     * With [customisations], builds a new SVG via `iconToSVG`, including the needed
     * namespaces and attributes. Without customisations, keeps the original document and
     * adds a missing `viewBox`, `width` or `height`. [prettyPrint] controls whitespace in
     * the output.
     */
    @JsName("toString")
    fun toSvgString(
        customisations: IconifyIconCustomisations? = definedExternally,
        prettyPrint: Boolean = definedExternally,
    ): String

    /**
     * Get the SVG as a string without pretty printing.
     */
    fun toMinifiedString(customisations: IconifyIconCustomisations? = definedExternally): String

    /**
     * Get the SVG as a pretty-printed string.
     */
    fun toPrettyString(customisations: IconifyIconCustomisations? = definedExternally): String

    /**
     * Get the inner content of the SVG.
     *
     * Throws when the root element has `fill`, `stroke` or `opacity` attributes, because
     * those cannot be moved into the body safely. Run [cleanupSVGRoot] first.
     */
    fun getBody(): String

    /**
     * Get the icon as Iconify icon data: viewBox dimensions plus body.
     */
    fun getIcon(): IconifyIcon

    /**
     * Parse and replace the SVG contents.
     *
     * Removes comments, XML declarations, DOCTYPE, Adobe Illustrator junk and empty `<g>`
     * elements before parsing. Requires exactly one `<svg>` root element and either a
     * valid `viewBox` or both width and height; otherwise throws.
     */
    fun load(content: String)
}
