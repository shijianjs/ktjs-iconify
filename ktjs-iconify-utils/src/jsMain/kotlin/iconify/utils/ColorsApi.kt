@file:JsModule("@iconify/utils")

package iconify.utils

external fun stringToColor(value: String): Color?

external fun compareColors(color1: Color, color2: Color): Boolean

external fun colorToHexString(
    color: RGBColor,
    canCompact: Boolean = definedExternally,
    canRound: Boolean = definedExternally,
): String?

external fun colorToString(color: Color, canRound: Boolean = definedExternally): String

/** CSS color keywords, keyed by name. */
external val colorKeywords: dynamic
