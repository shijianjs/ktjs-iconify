@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.utils.Color

/**
 * Find and optionally replace colors in an SVG.
 */
external fun parseColors(svg: SVG, options: ParseColorsOptions? = definedExternally): FindColorsResult

/**
 * Check if a color is empty, such as `none` or `transparent`.
 */
external fun isEmptyColor(color: Color): Boolean

/**
 * Validate colors in an SVG. Throws on error.
 */
external fun validateColors(
    svg: SVG,
    expectMonotone: Boolean,
    options: ParseColorsOptions? = definedExternally,
): FindColorsResult

/**
 * Detect whether an icon set is monotone (`false`), palette (`true`) or mixed (`null`).
 */
external fun detectIconSetPalette(iconSet: IconSet): Boolean?
