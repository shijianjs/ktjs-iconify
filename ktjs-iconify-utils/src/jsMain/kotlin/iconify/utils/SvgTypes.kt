package iconify.utils

import js.objects.Record

/**
 * SVG viewBox: `[x, y, width, height]`.
 */
typealias SVGViewBox = Array<Double>

/**
 * SVG split into element attributes and inner content.
 */
external interface ParsedSVGContent {
    /**
     * Attributes of the `<svg>` element, keyed by attribute name.
     */
    var attribs: Record<String, String>

    /**
     * Inner content of the `<svg>` element.
     */
    var body: String
}

/**
 * Result of splitting definitions from SVG content.
 */
external interface SplitSVGDefsResult {
    /**
     * Extracted definitions, without the wrapping tag.
     */
    var defs: String

    /**
     * Content that remains after removing the definitions.
     */
    var content: String
}
