package iconify.tools

import iconify.utils.Color
import iconify.utils.StringOrDouble
import js.reflect.unsafeCast
import kotlin.js.JsAny

/**
 * SVGO plugin configuration: a plugin name, or an object with a name and optional
 * parameters.
 *
 * 上游 `PluginConfig`。自定义插件对象里的 `fn` 是本包装未建模的 XAST visitor，按不透明
 * [JsAny] 保留。
 */
external interface PluginConfig

fun PluginConfig(value: String): PluginConfig = unsafeCast(value)

fun PluginConfig(value: PluginConfigObject): PluginConfig = unsafeCast(value)

/**
 * Object form of [PluginConfig].
 */
external interface PluginConfigObject {
    /** Plugin name. */
    var name: String

    /**
     * Plugin parameters.
     *
     * 上游按插件名映射到各自的参数类型；这里无法表达，按不透明值保留。
     */
    var params: JsAny?

    /**
     * Plugin implementation for custom plugins.
     *
     * 上游类型是 `Plugin<T>`，返回 XAST visitor；这里按不透明值保留。
     */
    var fn: JsAny?
}

/**
 * 上游 `string | ((id: string) => string) | false`：ID 清洗方式。
 */
external interface CleanupIDsOption

fun CleanupIDsOption(value: String): CleanupIDsOption = unsafeCast(value)

fun CleanupIDsOption(value: (id: String) -> String): CleanupIDsOption = unsafeCast(value)

fun CleanupIDsOption(value: Boolean): CleanupIDsOption = unsafeCast(value)

/**
 * Options shared by both [SVGOptions] variants.
 */
external interface SVGOptionsCommon {
    /**
     * Parse the SVG multiple times for better optimisation.
     *
     * Defaults to `true`.
     */
    var multipass: Boolean?
}

/**
 * Options that provide an explicit SVGO plugin list.
 */
external interface SVGOptionsWithPlugins : SVGOptionsCommon {
    /**
     * SVGO plugin list.
     */
    var plugins: Array<PluginConfig>
}

/**
 * Options that use the Iconify plugin set.
 */
external interface SVGOptionsWithoutPlugins : SVGOptionsCommon {
    /**
     * Clean up IDs: a prefix string, a function that maps old IDs to new ones, or `false` to
     * disable.
     *
     * Defaults to `"svgID"`. Do not use dashes, they break some SVG animations.
     */
    var cleanupIDs: CleanupIDsOption?

    /**
     * Keep shapes: skip plugins that modify shapes.
     */
    var keepShapes: Boolean?
}

/**
 * 上游 `SVGOptionsWithPlugin | SVGOptionsWithoutPlugin`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface SVGOptions

fun SVGOptions(value: SVGOptionsWithPlugins): SVGOptions = unsafeCast(value)

fun SVGOptions(value: SVGOptionsWithoutPlugins): SVGOptions = unsafeCast(value)

/**
 * Callback that checks whether a color matches.
 */
typealias ColorCheckCallback = (value: String, color: Color?) -> Boolean

/**
 * 上游 `string | string[] | ColorCheckCallback`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface ColorCheck

fun ColorCheck(value: String): ColorCheck = unsafeCast(value)

fun ColorCheck(value: Array<String>): ColorCheck = unsafeCast(value)

fun ColorCheck(value: ColorCheckCallback): ColorCheck = unsafeCast(value)

/**
 * Callback that maps a color to a replacement value or opacity.
 */
typealias SVGToMaskCustomCallback = (value: String, color: Color?) -> StringOrDouble?

/**
 * Options for [convertSVGToMask].
 */
external interface SVGToMaskOptions {
    /**
     * Color of the opaque area.
     *
     * Defaults to `currentColor`.
     */
    var color: String?

    /**
     * Colors that are treated as solid.
     *
     * Defaults to black and `currentcolor`.
     */
    var solid: ColorCheck?

    /**
     * Colors that are treated as transparent.
     *
     * Defaults to white.
     */
    var transparent: ColorCheck?

    /**
     * Callback that maps colors to a replacement color or opacity before the default solid
     * and transparent checks.
     */
    var custom: SVGToMaskCustomCallback?

    /**
     * Convert even when the colors look like a well-designed icon.
     *
     * Defaults to `false`.
     */
    var force: Boolean?

    /**
     * ID of the generated mask.
     *
     * Defaults to `"mask"`.
     */
    var id: String?
}
