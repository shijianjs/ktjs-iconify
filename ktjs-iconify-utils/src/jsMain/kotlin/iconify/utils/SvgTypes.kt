package iconify.utils

/**
 * SVG viewBox: `[x, y, width, height]`.
 */
typealias SVGViewBox = Array<Double>

external interface ParsedSVGContent {
    var attribs: dynamic
    var body: String
}

external interface SplitSVGDefsResult {
    var defs: String
    var content: String
}
