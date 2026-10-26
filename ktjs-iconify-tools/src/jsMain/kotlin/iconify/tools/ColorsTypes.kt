package iconify.tools

import iconify.utils.Color
import js.objects.Record
import js.reflect.unsafeCast

/**
 * Color attribute: `"color"`, `"fill"`, `"stroke"`, `"stop-color"` or `"flood-color"`.
 *
 * 上游 `ColorAttributes`。
 */
sealed external interface ColorAttribute

/**
 * 包装来自 JavaScript 的值。优先用 [ColorAttributes] 里的常量。
 */
fun ColorAttribute(value: String): ColorAttribute = unsafeCast(value)

/**
 * [ColorAttribute] 的全部字面量。
 */
object ColorAttributes {
    inline val color: ColorAttribute get() = ColorAttribute("color")
    inline val fill: ColorAttribute get() = ColorAttribute("fill")
    inline val stroke: ColorAttribute get() = ColorAttribute("stroke")
    inline val stopColor: ColorAttribute get() = ColorAttribute("stop-color")
    inline val floodColor: ColorAttribute get() = ColorAttribute("flood-color")
}

/**
 * 上游 `Color | string`：解析后的颜色，或无法解析的原始字符串。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface ColorOrString

fun ColorOrString(value: Color): ColorOrString = unsafeCast(value)

fun ColorOrString(value: String): ColorOrString = unsafeCast(value)

/**
 * Element extended with the color properties added by [parseColors].
 */
external interface ExtendedTagElementWithColors : ExtendedTagElement {
    /** Colors found on this element, keyed by attribute. */
    var _colors: Record<ColorAttribute, ColorOrString?>?

    /** `true` if the element was removed while parsing colors. */
    var _removed: Boolean?
}

/**
 * Result of a [ParseColorsCallback]: a new color, the original value, `"unset"` to delete
 * the attribute, or `"remove"` to remove the shape or rule.
 *
 * 上游 `Color | string | 'remove' | 'unset'`，其中字符串分支用 [ColorOrString] 的字符串
 * 工厂表示。
 */
typealias ParseColorsCallbackResult = ColorOrString

/**
 * Callback invoked for each found color.
 *
 * [tagName] and [item] are only set for colors in elements.
 */
typealias ParseColorsCallback =
    (
        attr: ColorAttribute,
        colorString: String,
        parsedColor: Color?,
        tagName: String?,
        item: ExtendedTagElementWithColors?,
    ) -> ParseColorsCallbackResult

/**
 * Callback that returns the color to use for elements with the default color.
 */
typealias ParseColorDefaultColorCallback =
    (
        prop: String,
        item: ExtendedTagElementWithColors,
        treeItem: ElementsTreeItem,
        iconData: AnalyseSVGStructureResult,
    ) -> Color

/**
 * 上游 `Color | string | ParseColorOptionsDefaultColorCallback`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface ParseColorDefaultColor

fun ParseColorDefaultColor(value: Color): ParseColorDefaultColor = unsafeCast(value)

fun ParseColorDefaultColor(value: String): ParseColorDefaultColor = unsafeCast(value)

fun ParseColorDefaultColor(
    value: ParseColorDefaultColorCallback,
): ParseColorDefaultColor = unsafeCast(value)

/**
 * Result of [parseColors] and [validateColors].
 */
external interface FindColorsResult {
    /**
     * Custom colors found in the SVG.
     *
     * Parsed as [Color] when possible, otherwise kept as a string.
     */
    var colors: Array<ColorOrString>

    /**
     * `true` if the SVG uses the default color.
     */
    var hasUnsetColor: Boolean

    /**
     * `true` if the SVG has a global style, which makes detecting the default fill unreliable.
     */
    var hasGlobalStyle: Boolean
}

/**
 * Options for [parseColors] and [validateColors].
 */
external interface ParseColorsOptions : AnalyseSVGStructureOptions {
    /**
     * Callback invoked for each color.
     *
     * Receives the attribute, the color string, the parsed color or `null`, and, for colors in
     * elements, the tag name and element. Return a new color, `"unset"` to remove the attribute,
     * or `"remove"` to remove the shape or rule.
     */
    var callback: ParseColorsCallback?

    /**
     * Color for elements that use the default color.
     *
     * Can be a parsed color, a color string, or a callback that receives the property, element,
     * tree item and analysis result.
     */
    var defaultColor: ParseColorDefaultColor?
}
