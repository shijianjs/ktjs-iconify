@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyJSON
import kotlin.js.Promise

/**
 * Load an icon from the Iconify API or a locally installed `@iconify-json` package.
 *
 * Browser builds can only use custom collections.
 */
external fun loadIcon(
    collection: String,
    icon: String,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Get an icon from an inline collection or a custom loader.
 */
external fun getCustomIcon(
    custom: dynamic,
    collection: String,
    icon: String,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Search for an icon in an icon set, trying alternative names.
 */
external fun searchForIcon(
    iconSet: IconifyJSON,
    collection: String,
    ids: Array<String>,
    options: IconifyLoaderOptions? = definedExternally,
): Promise<String?>

/**
 * Apply customisations and extra properties to an SVG string.
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
 * Get possible icon names, from the most specific to the most generic.
 */
external fun getPossibleIconNames(icon: String): Array<String>
