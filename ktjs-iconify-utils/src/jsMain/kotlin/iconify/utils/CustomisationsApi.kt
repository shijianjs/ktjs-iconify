@file:JsModule("@iconify/utils")

package iconify.utils

/**
 * Default size customisations: `width` and `height` set to `null`.
 */
external val defaultIconSizeCustomisations: dynamic

/**
 * Default icon customisations: no size, no rotation, no flips.
 */
external val defaultIconCustomisations: FullIconCustomisations

/**
 * Convert customisations to a full set of values.
 *
 * Missing properties are taken from [defaults]. Values of the wrong type are ignored, and rotation
 * is normalised modulo 4.
 */
external fun mergeCustomisations(
    defaults: FullIconCustomisations,
    item: IconifyIconCustomisations,
): FullIconCustomisations

/**
 * Apply a `flip` string to customisations.
 *
 * The string is split by spaces and commas. `"horizontal"` sets [IconifyIconCustomisations.hFlip],
 * `"vertical"` sets [IconifyIconCustomisations.vFlip]; other values are ignored.
 */
external fun flipFromString(custom: IconifyIconCustomisations, flip: String)

/**
 * Parse a rotation string such as `"90deg"`, `"25%"` or `"1"` into a number of 90 degree rotations,
 * normalised to `0`..`3`.
 *
 * @param defaultValue returned when the string does not contain a whole number of quarter turns.
 * Defaults to `0`.
 */
external fun rotateFromString(value: String, defaultValue: Int = definedExternally): Int

/**
 * Read a boolean customisation value from an attribute.
 *
 * `true`, `"1"`, `"true"` and [name] itself are `true`; `false`, `"0"`, `"false"` and an empty
 * string are `false`; numbers use their truthiness. Anything else returns [defaultValue].
 */
external fun toBoolean(name: String, value: dynamic, defaultValue: Boolean): Boolean
