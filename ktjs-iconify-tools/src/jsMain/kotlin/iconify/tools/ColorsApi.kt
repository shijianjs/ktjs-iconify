@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.utils.Color

/**
 * Find and optionally replace colors in an SVG.
 *
 * Checks color attributes, inline and global styles and animated color values. For each
 * color, [ParseColorsOptions.callback] can return a new color to change it, the original
 * value to keep it, `"unset"` to delete the value, or `"remove"` to remove the shape or
 * rule. [ParseColorsOptions.defaultColor] is applied to shapes that rely on the default
 * black fill; it can be a color or a callback.
 *
 * Returns the list of found colors and whether the icon relies on a default color or
 * global style. Clean up the SVG first (for example with [cleanupSVG]) so styles are
 * converted to attributes before parsing, otherwise results might be inaccurate.
 */
external fun parseColors(svg: SVG, options: ParseColorsOptions? = definedExternally): FindColorsResult

/**
 * Check if a color is empty, such as `none` or `transparent`.
 */
external fun isEmptyColor(color: Color): Boolean

/**
 * Validate colors in an SVG. Throws on error.
 *
 * `none`, `transparent` and `url()` are always allowed. When [expectMonotone] is `true`
 * only `currentColor` is allowed for actual colors; when `false` only palette colors
 * (`rgb`, `hsl`, etc.) are allowed, and `currentColor` is rejected. Other color types
 * always throw.
 *
 * Returns the same result as [parseColors].
 */
external fun validateColors(
    svg: SVG,
    expectMonotone: Boolean,
    options: ParseColorsOptions? = definedExternally,
): FindColorsResult

/**
 * Detect whether an icon set is monotone (`false`), palette (`true`) or mixed (`null`).
 *
 * Checks only `icon` entries, ignoring aliases and variations. An icon set with no
 * parsable colors is treated as mixed.
 */
external fun detectIconSetPalette(iconSet: IconSet): Boolean?
