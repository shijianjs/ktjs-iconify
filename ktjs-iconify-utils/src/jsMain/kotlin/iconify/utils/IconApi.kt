@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyTransformations
import kotlin.js.RegExp

external val defaultIconDimensions: dynamic

external val defaultIconTransformations: IconifyTransformations

external val defaultIconProps: dynamic

external val defaultExtendedIconProps: dynamic

/** Expression matching a valid icon name part. */
external val matchIconName: RegExp

/**
 * Merge an icon and its alias, or default values and an icon.
 */
external fun <T : ExtendedIconifyIcon> mergeIconData(parent: T, child: dynamic): T

external fun <T : IconifyTransformations> mergeIconTransformations(
    obj1: T,
    obj2: IconifyTransformations,
): T

external fun makeIconSquare(icon: FullIconifyIcon): FullIconifyIcon

external fun makeViewBoxSquare(viewBox: SVGViewBox): SVGViewBox

/**
 * Convert a string such as `mdi:home` to an icon name object.
 */
external fun stringToIcon(
    value: String,
    validate: Boolean = definedExternally,
    allowSimpleName: Boolean = definedExternally,
    provider: String = definedExternally,
): IconifyIconName?

external fun validateIconName(
    icon: IconifyIconName?,
    allowSimpleName: Boolean = definedExternally,
): Boolean
