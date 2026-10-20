package iconify.utils

import iconify.IconifyDimenisons
import iconify.IconifyIcon
import iconify.IconifyTransformations

/**
 * Icon name split into parts.
 */
external interface IconifyIconName {
    val provider: String
    val prefix: String
    val name: String
}

/**
 * `null`, a CSS size string such as `"1em"`, or a number of pixels.
 *
 * Kotlin has no union type, so sizes are typed as `dynamic`.
 */
external interface IconifyIconSizeCustomisations {
    var width: dynamic
    var height: dynamic
}

external interface IconifyIconCustomisations : IconifyTransformations, IconifyIconSizeCustomisations

external interface FullIconCustomisations {
    var width: dynamic
    var height: dynamic
    var rotate: Double
    var hFlip: Boolean
    var vFlip: Boolean
}

/** Icon with all optional properties resolved. */
external interface FullIconifyIcon : IconifyDimenisons, IconifyTransformations {
    var body: String
}

external interface IconifyIconBuildAttributes {
    var width: String?
    var height: String?
    var viewBox: String
}

external interface IconifyIconBuildResult {
    var attributes: IconifyIconBuildAttributes

    /** `[x, y, width, height]`. */
    var viewBox: Array<Double>

    var body: String
}
