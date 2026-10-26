@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyInfo
import iconify.IconifyJSON
import kotlin.js.JsAny
import kotlin.js.Promise

/**
 * Get icon data from an icon set, resolving aliases and filling in defaults.
 *
 * @return the resolved icon, or `null` if the icon does not exist or its alias chain is broken.
 */
external fun getIconData(data: IconifyJSON, name: String): ExtendedIconifyIcon?

/**
 * Extract a subset of icons from an icon set.
 *
 * Aliases are copied as aliases, but only when their parent icon is also included, so the result is
 * self-contained.
 *
 * @param notFound list missing names in `not_found` and return a result even when no icon was
 * found.
 * @return the subset, or `null` if no icon was found and [notFound] is not `true`.
 */
external fun getIcons(
    data: IconifyJSON,
    names: Array<String>,
    notFound: Boolean = definedExternally,
): IconifyJSON?

/**
 * Invoke [callback] for each icon in the icon set.
 *
 * Alias chains are resolved before the callback is invoked. `data` is `null` if the icon is
 * missing.
 *
 * @return names of the icons found in the icon set.
 */
external fun parseIconSet(
    data: IconifyJSON,
    callback: (name: String, data: ExtendedIconifyIcon?) -> JsAny?,
): Array<String>

/**
 * Asynchronous version of [parseIconSet]: awaits each callback before processing the next icon.
 *
 * @return names of the icons found in the icon set.
 */
external fun parseIconSetAsync(
    data: IconifyJSON,
    callback: (name: String, data: ExtendedIconifyIcon?) -> Promise<JsAny?>,
): Promise<Array<String>>

/**
 * Validate an icon set, optionally fixing it.
 *
 * Checks metadata, icon data, aliases and parent references.
 *
 * @return the icon set on success.
 * @throws if the icon set is invalid and [IconSetValidationOptions.fix] is not `true`.
 */
external fun validateIconSet(
    obj: JsAny?,
    options: IconSetValidationOptions? = definedExternally,
): IconifyJSON

/**
 * Quick validation of an icon set.
 *
 * Unlike [validateIconSet], this does not check metadata, does not throw and does not fix
 * anything. It only checks that the object has an `icons` object.
 *
 * @return the icon set on success, `null` on failure.
 */
external fun quicklyValidateIconSet(obj: JsAny?): IconifyJSON?

/**
 * Expand a minified icon set in place.
 *
 * Copies root defaults into each icon and alias, the opposite of [minifyIconSet].
 */
external fun expandIconSet(data: IconifyJSON)

/**
 * Minify an icon set in place.
 *
 * Moves values repeated by all icons to the root, and removes default values.
 */
external fun minifyIconSet(data: IconifyJSON)

/**
 * Convert icon set information from the legacy format used by the Iconify API and
 * `collections.json` to [IconifyInfo].
 *
 * @param expectedPrefix when set, reject data whose `prefix` does not match.
 * @return the converted information, or `null` if [data] is invalid.
 */
external fun convertIconSetInfo(data: JsAny, expectedPrefix: String = definedExternally): IconifyInfo?

/**
 * Parent icons for each icon.
 *
 * The result is keyed by icon name; each value lists the direct parent first, followed by its
 * parents. Icons without a parent map to an empty list. Missing icons and aliases with a broken
 * parent chain map to `null`.
 *
 * @param names only resolve these icons; defaults to all icons and aliases.
 */
external fun getIconsTree(data: IconifyJSON, names: Array<String>? = definedExternally): ParentIconsTree
