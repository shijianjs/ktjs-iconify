@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyInfo
import iconify.IconifyJSON
import kotlin.js.Promise

/**
 * Get icon data from an icon set, resolving aliases.
 */
external fun getIconData(data: IconifyJSON, name: String): ExtendedIconifyIcon?

/**
 * Extract a subset of icons from an icon set.
 */
external fun getIcons(
    data: IconifyJSON,
    names: Array<String>,
    notFound: Boolean = definedExternally,
): IconifyJSON?

/**
 * Callback invoked for each icon. `data` is `null` if the icon is missing.
 */
external fun parseIconSet(
    data: IconifyJSON,
    callback: (name: String, data: ExtendedIconifyIcon?) -> Any?,
): Array<String>

external fun parseIconSetAsync(
    data: IconifyJSON,
    callback: (name: String, data: ExtendedIconifyIcon?) -> dynamic,
): Promise<Array<String>>

/**
 * Validate an icon set. Returns the icon set on success, throws on failure.
 */
external fun validateIconSet(
    obj: dynamic,
    options: IconSetValidationOptions? = definedExternally,
): IconifyJSON

/**
 * Quick validation. Returns `null` on failure.
 */
external fun quicklyValidateIconSet(obj: dynamic): IconifyJSON?

/** Expand a minified icon set in place. */
external fun expandIconSet(data: IconifyJSON)

/** Minify an icon set in place by moving repeated values to the root. */
external fun minifyIconSet(data: IconifyJSON)

external fun convertIconSetInfo(data: dynamic, expectedPrefix: String = definedExternally): IconifyInfo?

/**
 * Parent icon list for each icon, `null` if the alias tree cannot be resolved.
 */
external fun getIconsTree(data: IconifyJSON, names: Array<String>? = definedExternally): dynamic

/** Optional properties copied when extracting icons. */
external val propsToCopy: Array<String>
