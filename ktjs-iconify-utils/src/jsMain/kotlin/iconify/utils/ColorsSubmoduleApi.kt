@file:JsModule("@iconify/utils/lib/colors")

package iconify.utils

/**
 * Convert an RGB color to a hex string.
 *
 * Bound to the `@iconify/utils/lib/colors` submodule, which is the only place this function is
 * exported from.
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
