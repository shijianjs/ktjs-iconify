@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyIcon

/**
 * Generate SVG attributes and body from an icon.
 */
external fun iconToSVG(
    icon: IconifyIcon,
    customisations: IconifyIconCustomisations? = definedExternally,
): IconifyIconBuildResult

/**
 * Build an `<svg>` string from a body and attributes.
 */
external fun iconToHTML(body: String, attributes: dynamic): String

/**
 * Calculate the second dimension when only one is set.
 */
external fun calculateSize(
    size: dynamic,
    ratio: Double,
    precision: Int = definedExternally,
): dynamic

external fun getSVGViewBox(value: String): SVGViewBox?

external fun svgToData(svg: String): String

external fun svgToURL(svg: String): String

/**
 * Extract non-nestable definitions such as masks and clip paths.
 */
external fun splitSVGDefs(
    content: String,
    tag: String = definedExternally,
): SplitSVGDefsResult

external fun mergeDefsAndContent(defs: String, content: String): String

/**
 * Wrap content, leaving definitions unwrapped.
 */
external fun wrapSVGContent(body: String, start: String, end: String): String

/**
 * Replace IDs with unique IDs to avoid collisions when embedding multiple icons.
 */
external fun replaceIDs(body: String): String

external fun clearIDCache()

/** Remove unnecessary whitespace. */
external fun trimSVG(str: String): String

external fun prettifySVG(
    content: String,
    tab: String = definedExternally,
    depth: Int = definedExternally,
): String?

/** Encode SVG for use in CSS `url()`. */
external fun encodeSvgForCss(svg: String): String

/**
 * Prepare an HTML string for `innerHTML` assignment.
 */
external fun cleanUpInnerHTML(html: String): String

external fun parseSVGContent(content: String): ParsedSVGContent?

external fun buildParsedSVG(data: ParsedSVGContent): IconifyIconBuildResult?

external fun convertParsedSVG(data: ParsedSVGContent): IconifyIcon?
