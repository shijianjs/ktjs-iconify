package iconify.utils

/**
 * Parsed CSS color.
 *
 * The [type] property identifies the variant and selects which fields are available.
 */
external interface Color {
    /**
     * Color type: `"rgb"`, `"hsl"`, `"lab"`, `"lch"`, `"function"`, `"transparent"`, `"none"` or
     * `"current"`.
     */
    var type: String
}

/**
 * RGB color.
 */
external interface RGBColor : Color {
    /** Red, `0` to `255`. */
    var r: Double

    /** Green, `0` to `255`. */
    var g: Double

    /** Blue, `0` to `255`. */
    var b: Double

    /** Alpha, `0` to `1`. */
    var alpha: Double
}

/**
 * HSL color.
 */
external interface HSLColor : Color {
    /** Hue, `0` to `360`. */
    var h: Double

    /** Saturation, `0` to `100`. */
    var s: Double

    /** Lightness, `0` to `100`. */
    var l: Double

    /** Alpha, `0` to `1`. */
    var alpha: Double
}

/**
 * LAB color.
 */
external interface LABColor : Color {
    /** Lightness, `0` to `100`. */
    var l: Double

    /** Green to red axis. */
    var a: Double

    /** Blue to yellow axis. */
    var b: Double

    /** Alpha, `0` to `1`. */
    var alpha: Double
}

/**
 * LCH color.
 */
external interface LCHColor : Color {
    /** Lightness, `0` to `100`. */
    var l: Double

    /** Chroma. */
    var c: Double

    /** Hue. */
    var h: Double

    /** Alpha, `0` to `1`. */
    var alpha: Double
}

/**
 * CSS color function that is not parsed, such as `oklch()`.
 *
 * The function name and its raw content are kept as-is.
 */
external interface FunctionColor : Color {
    /** Function name. */
    var `func`: String

    /** Raw function content, without the name and parentheses. */
    var value: String
}

/**
 * Fully transparent color.
 */
external interface TransparentColor : Color

/**
 * Value `none`.
 */
external interface NoColor : Color

/**
 * CSS `currentColor`.
 */
external interface CurrentColor : Color
