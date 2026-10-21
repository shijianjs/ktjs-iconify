package iconify.utils

import iconify.IconifyDimenisons
import iconify.IconifyIcon
import iconify.IconifyTransformations

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
 * Each size is `null` for the default, a CSS size string such as `"1em"`, or a number of pixels.
 * Kotlin has no union type, so sizes are typed as `dynamic`.
 */
external interface IconifyIconSizeCustomisations {
    /**
     * Icon width.
     */
    var width: dynamic

    /**
     * Icon height.
     */
    var height: dynamic
}

/**
 * Icon customisations: size and transformations.
 */
external interface IconifyIconCustomisations : IconifyTransformations, IconifyIconSizeCustomisations

/**
 * Icon customisations with all values resolved.
 */
external interface FullIconCustomisations {
    /**
     * Icon width: `null`, a CSS size string or a number of pixels.
     */
    var width: dynamic

    /**
     * Icon height: `null`, a CSS size string or a number of pixels.
     */
    var height: dynamic

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
 * Icon with all optional properties resolved.
 */
external interface FullIconifyIcon : IconifyDimenisons, IconifyTransformations {
    /**
     * SVG body content: the inner SVG elements.
     */
    var body: String
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
