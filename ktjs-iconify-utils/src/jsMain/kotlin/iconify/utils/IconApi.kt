@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyTransformations
import kotlin.js.RegExp

/**
 * Default dimensions: `left` and `top` `0`, `width` and `height` `16`.
 */
external val defaultIconDimensions: dynamic

/**
 * Default transformations: no rotation, no flips.
 */
external val defaultIconTransformations: IconifyTransformations

/**
 * Default dimensions and transformations.
 */
external val defaultIconProps: dynamic

/**
 * Default values for all properties of [ExtendedIconifyIcon]: [defaultIconProps], an empty body and
 * `hidden` set to `false`.
 */
external val defaultExtendedIconProps: dynamic

/**
 * Expression matching a valid icon name part.
 *
 * Lowercase letters and digits, separated by single dashes. Used by the Iconify API, but ignored
 * for custom icon sets, which may use other naming conventions.
 */
external val matchIconName: RegExp

/**
 * Merge an icon and its alias, or default values and an icon.
 *
 * Transformations are merged: flips are combined and rotations are added. Other properties are
 * taken from [child] when present, otherwise from [parent], with defaults filled in for missing
 * values.
 */
external fun <T : ExtendedIconifyIcon> mergeIconData(parent: T, child: dynamic): T

/**
 * Merge transformations.
 *
 * Flips are combined, so two flips cancel out, and rotations are added modulo 4. Only the resulting
 * non-default values are present on the returned object.
 */
external fun <T : IconifyTransformations> mergeIconTransformations(
    obj1: T,
    obj2: IconifyTransformations,
): T

/**
 * Center an icon in a square viewBox, keeping it at its original size.
 *
 * Returns the icon unchanged if it is already square.
 */
external fun makeIconSquare(icon: FullIconifyIcon): FullIconifyIcon

/**
 * Center a viewBox in a square one, keeping the original size.
 *
 * Returns [viewBox] unchanged if it is already square.
 */
external fun makeViewBoxSquare(viewBox: SVGViewBox): SVGViewBox

/**
 * Convert a string such as `mdi:home`, `@provider:mdi:home` or `mdi-home` to an icon name object.
 *
 * @param validate return `null` when the result is not a valid icon name.
 * @param allowSimpleName allow names without a prefix, such as `home`.
 * @param provider provider used when the string does not include one.
 * @return the parsed name, or `null` if [value] cannot be parsed.
 */
external fun stringToIcon(
    value: String,
    validate: Boolean = definedExternally,
    allowSimpleName: Boolean = definedExternally,
    provider: String = definedExternally,
): IconifyIconName?

/**
 * Check whether an icon name is valid: the prefix must not be empty, unless [allowSimpleName] is
 * set, and the name must not be empty.
 */
external fun validateIconName(
    icon: IconifyIconName?,
    allowSimpleName: Boolean = definedExternally,
): Boolean
