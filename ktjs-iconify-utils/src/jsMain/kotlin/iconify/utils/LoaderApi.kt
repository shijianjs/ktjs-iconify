@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyJSON
import kotlin.js.Promise

/**
 * Load an icon from a custom collection registered in [IconifyLoaderOptions.customCollections].
 *
 * The loader does not fetch anything: it only uses inline collections and custom loader functions.
 * Returns `undefined` when the collection is not registered or the icon is missing.
 */
external fun loadIcon(
    collection: String,
    icon: String,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Get an icon from an inline collection or a custom loader function.
 *
 * The result is an SVG string with customisations and extra properties applied.
 *
 * Returns `undefined` when the icon is missing.
 */
external fun getCustomIcon(
    custom: dynamic,
    collection: String,
    icon: String,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Search for an icon in an icon set, trying names from [ids] in order.
 *
 * The first matching name is rendered as an SVG string with customisations applied.
 *
 * Returns `undefined` when none of the names exist.
 */
external fun searchForIcon(
    iconSet: IconifyJSON,
    collection: String,
    ids: Array<String>,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Apply customisations and extra properties to an SVG string.
 *
 * Applies the transformations configured in [IconifyLoaderOptions.customizations], then adds
 * namespaces, default style and class, scale and [IconifyLoaderOptions.usedProps].
 */
external fun mergeIconProps(
    svg: String,
    collection: String,
    icon: String,
    options: IconifyLoaderOptions? = definedExternally,
    propsProvider: (() -> dynamic)? = definedExternally,
    afterCustomizations: ((dynamic) -> Unit)? = definedExternally,
): Promise<String>

/**
 * Possible icon names for a name that may use a different naming convention, from the most specific
 * to the most generic.
 *
 * For example, `fooBar` also produces `foo-bar`, and `foo2` also produces `foo-2`.
 */
external fun getPossibleIconNames(icon: String): Array<String>
