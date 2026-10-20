@file:JsModule("@iconify/utils")

package iconify.utils

external val defaultIconSizeCustomisations: dynamic

external val defaultIconCustomisations: FullIconCustomisations

/**
 * Convert customisations to a full set of values, checking value types.
 */
external fun mergeCustomisations(
    defaults: FullIconCustomisations,
    item: IconifyIconCustomisations,
): FullIconCustomisations

/**
 * Apply a `flip` string such as `"horizontal"` or `"vertical"` to customisations.
 */
external fun flipFromString(custom: IconifyIconCustomisations, flip: String)

/**
 * Parse a rotation string such as `"90deg"` or `"1"`.
 */
external fun rotateFromString(value: String, defaultValue: Int = definedExternally): Int

/**
 * Read a boolean customisation value from an attribute.
 */
external fun toBoolean(name: String, value: dynamic, defaultValue: Boolean): Boolean
