@file:JsModule("@iconify/utils")

package iconify.utils

/**
 * Parse a CSS color string: a hex color, a color keyword, or an `rgb`, `hsl`, `lab` or `lch`
 * function.
 *
 * Unsupported functions are returned as [FunctionColor] without parsing their content.
 *
 * @return the parsed color, or `null` if [value] is not a valid color.
 */
external fun stringToColor(value: String): Color?

/**
 * Compare two parsed colors.
 *
 * Equivalent representations are treated as equal, for example `#fff` and `rgb(255, 255, 255)`.
 * Hue and saturation are ignored for colors where they have no effect, and fully transparent
 * colors are equal regardless of their other components.
 */
external fun compareColors(color1: Color, color2: Color): Boolean

/**
 * Convert an RGB color to a hex string.
 *
 * @param canCompact shorten six digit colors to three digits, and eight digit colors to four, when
 * possible.
 * @param canRound round non-integer channel values; when `false`, such colors return `null`.
 * @return the hex string, or `null` if the color cannot be represented without rounding.
 */
external fun colorToHexString(
    color: RGBColor,
    canCompact: Boolean = definedExternally,
    canRound: Boolean = definedExternally,
): String?

/**
 * Convert a parsed color back to a CSS string.
 *
 * RGB colors are written as hex when possible, otherwise as `rgb()` or `rgba()`.
 *
 * @param canRound round non-integer channel values when converting RGB to hex.
 */
external fun colorToString(color: Color, canRound: Boolean = definedExternally): String

/**
 * CSS color keywords, keyed by name.
 *
 * Each value is a parsed [Color] object.
 */
external val colorKeywords: dynamic
