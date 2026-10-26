package iconify.utils

import iconify.IconifyDimenisons
import iconify.IconifyIcon
import iconify.IconifyTransformations
import js.reflect.unsafeCast

/**
 * 上游 `string | number`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface StringOrDouble

fun StringOrDouble(value: String): StringOrDouble = unsafeCast(value)

fun StringOrDouble(value: Double): StringOrDouble = unsafeCast(value)

/**
 * 上游 `IconifyIconSize = null | string | number`。
 */
typealias IconifyIconSize = StringOrDouble?

/**
 * Icon name split into parts.
 */
external interface IconifyIconName {
    /**
     * API provider name; empty for the default provider.
     */
    val provider: String

    /**
     * Icon set prefix, such as `mdi`; empty for simple names.
     */
    val prefix: String

    /**
     * Icon name without prefix, such as `home`.
     */
    val name: String
}

/**
 * Icon size customisations.
 *
 * Each size is `null` for the default, a CSS size string such as `"1em"`, or a number of pixels;
 * see [IconifyIconSize].
 */
external interface IconifyIconSizeCustomisations {
    /**
     * Icon width.
     */
    var width: IconifyIconSize

    /**
     * Icon height.
     */
    var height: IconifyIconSize
}

/**
 * 上游 `Required<IconifyIconSizeCustomisations>`：两个键都存在，值可以是 `null`。
 */
external interface FullIconSizeCustomisations {
    /**
     * Icon width.
     */
    var width: IconifyIconSize

    /**
     * Icon height.
     */
    var height: IconifyIconSize
}

/**
 * Icon customisations: size and transformations.
 */
external interface IconifyIconCustomisations : IconifyTransformations, IconifyIconSizeCustomisations

/**
 * 上游 `Required<IconifyDimenisons>`：四个值都已补齐。
 */
external interface FullIconDimensions {
    /**
     * Left position of the viewBox.
     */
    var left: Double

    /**
     * Top position of the viewBox.
     */
    var top: Double

    /**
     * Width of the viewBox.
     */
    var width: Double

    /**
     * Height of the viewBox.
     */
    var height: Double
}

/**
 * 上游 `Required<IconifyTransformations>`：三个值都已补齐。
 */
external interface FullIconTransformations {
    /**
     * Number of 90 degree rotations, `0` to `3`.
     */
    var rotate: Double

    /**
     * Horizontal flip.
     */
    var hFlip: Boolean

    /**
     * Vertical flip.
     */
    var vFlip: Boolean
}

/**
 * 上游 `Required<IconifyOptional>`：尺寸与变换都已补齐。
 */
external interface FullIconProps : FullIconDimensions, FullIconTransformations

/**
 * Icon customisations with all values resolved.
 */
external interface FullIconCustomisations : FullIconSizeCustomisations, FullIconTransformations

/**
 * Icon with all optional properties resolved.
 */
external interface FullIconifyIcon : IconifyDimenisons, IconifyTransformations {
    /**
     * SVG body content: the inner SVG elements.
     */
    var body: String
}

/**
 * 上游 `Required<FullExtendedIconifyIcon>`：图标全部属性都已补齐。
 */
external interface FullExtendedIconifyIcon : FullIconifyIcon {
    /**
     * `true` if the icon is hidden from search results.
     */
    var hidden: Boolean
}

/**
 * Attributes of a generated `<svg>` element.
 */
external interface IconifyIconBuildAttributes {
    /**
     * Width attribute.
     */
    var width: String?

    /**
     * Height attribute.
     */
    var height: String?

    /**
     * ViewBox attribute.
     */
    var viewBox: String
}

/**
 * Result of building an SVG from an icon.
 */
external interface IconifyIconBuildResult {
    /**
     * Attributes for the `<svg>` element.
     */
    var attributes: IconifyIconBuildAttributes

    /**
     * ViewBox as `[x, y, width, height]`.
     */
    var viewBox: Array<Double>

    /**
     * SVG body content: the inner SVG elements.
     */
    var body: String
}
