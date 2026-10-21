@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyIcon

/**
 * Generate SVG attributes and body from an icon and customisations.
 *
 * No style is generated, to stay compatible with frameworks that use style objects; the result
 * contains an `inline` attribute instead, and the renderer should add
 * `vertical-align: -0.125em` when it is `true`. Customisations are used as-is, so they should be
 * normalised by the platform parser. Call [replaceIDs] to make IDs in the body unique.
 */
external fun iconToSVG(
    icon: IconifyIcon,
    customisations: IconifyIconCustomisations? = definedExternally,
): IconifyIconBuildResult

/**
 * Build an `<svg>` string from a body and attributes.
 *
 * Adds the SVG namespace, and the `xlink` namespace when the body uses `xlink:`.
 */
external fun iconToHTML(body: String, attributes: dynamic): String

/**
 * Calculate the second dimension when only one dimension is set.
 *
 * @param size a number of pixels or a CSS size string such as `"1em"`.
 * @param ratio width/height ratio of the icon.
 * @param precision number of decimal digits used when rounding up; defaults to `100`, meaning two
 * decimal digits.
 * @return the calculated size, in the same form as [size].
 */
external fun calculateSize(
    size: dynamic,
    ratio: Double,
    precision: Int = definedExternally,
): dynamic

/**
 * Parse a viewBox string such as `"0 0 24 24"`.
 *
 * @return `[x, y, width, height]`, or `null` if the string is invalid.
 */
external fun getSVGViewBox(value: String): SVGViewBox?

/**
 * Generate a `data:image/svg+xml` URL from an SVG string.
 *
 * Uses a compact encoding that only escapes characters significant in SVG URLs.
 */
external fun svgToData(svg: String): String

/**
 * Generate a CSS `url("data:image/svg+xml,...")` value from an SVG string.
 */
external fun svgToURL(svg: String): String

/**
 * Extract non-nestable definitions, such as masks and clip paths, from SVG content.
 *
 * Should only be used with tags that cannot be nested.
 *
 * @param tag tag name to extract; defaults to `"defs"`.
 * @return the extracted definitions and the remaining content.
 */
external fun splitSVGDefs(
    content: String,
    tag: String = definedExternally,
): SplitSVGDefsResult

/**
 * Wrap [defs] in `<defs>` and prepend them to [content].
 */
external fun mergeDefsAndContent(defs: String, content: String): String

/**
 * Wrap SVG content with [start] and [end], leaving definitions unwrapped.
 */
external fun wrapSVGContent(body: String, start: String, end: String): String

/**
 * Replace IDs in an SVG body with unique IDs, updating their references.
 *
 * Prevents collisions when multiple icons are embedded in the same document. The counter cache is
 * global; call [clearIDCache] to reset it.
 *
 * @return the body with replaced IDs, or [body] unchanged when it contains no IDs.
 */
external fun replaceIDs(body: String): String

/**
 * Clear the ID counter cache used by [replaceIDs].
 */
external fun clearIDCache()

/**
 * Remove unnecessary whitespace from an SVG string.
 */
external fun trimSVG(str: String): String

/**
 * Pretty-print SVG content.
 *
 * @param tab indentation string; defaults to a tab.
 * @param depth initial indentation depth.
 * @return the formatted content, or `null` if the content cannot be parsed.
 */
external fun prettifySVG(
    content: String,
    tab: String = definedExternally,
    depth: Int = definedExternally,
): String?

/**
 * Encode an SVG for use in a CSS `url()`.
 *
 * Adds the `xlink` namespace when the SVG uses `xlink:`.
 */
external fun encodeSvgForCss(svg: String): String

/**
 * Prepare an HTML string for `innerHTML` assignment.
 *
 * Uses a Trusted Types policy when the browser requires one. This does not sanitise the HTML: it is
 * meant for icon data that has already been validated.
 */
external fun cleanUpInnerHTML(html: String): String

/**
 * Extract attributes and content from an SVG string.
 *
 * @return the attributes of the `<svg>` element and its inner content, or `null` if the string
 * does not contain an SVG.
 */
external fun parseSVGContent(content: String): ParsedSVGContent?

/**
 * Convert parsed SVG content to SVG attributes and body.
 *
 * Presentation attributes such as `fill` and `stroke` are moved into a wrapping `<g>`.
 *
 * @return the build result, or `null` if the parsed content has no valid viewBox.
 */
external fun buildParsedSVG(data: ParsedSVGContent): IconifyIconBuildResult?

/**
 * Convert parsed SVG content to an icon.
 *
 * Presentation attributes are moved into a wrapping `<g>`.
 *
 * @return the icon, or `null` if the parsed content has no valid viewBox.
 */
external fun convertParsedSVG(data: ParsedSVGContent): IconifyIcon?
