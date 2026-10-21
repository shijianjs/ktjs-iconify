@file:JsModule("iconify-icon")

package iconify

import kotlin.js.Promise

// IconifyIcon and IconifyJSON are declared once in ktjs-iconify-types and shared
// through the `iconify` package. IconifyIconName comes from IconifyApiProvider.kt,
// because it belongs to this package's own typings rather than @iconify/types.

/**
 * Checks whether icon data is available locally.
 *
 * @param name icon name such as `mdi:home`, optionally prefixed with a provider.
 */
external fun iconLoaded(name: String): Boolean

/**
 * Returns icon data with all default properties filled in.
 *
 * Returns `null` if the icon is known to be missing, or `undefined` if it was never loaded.
 */
external fun getIcon(name: String): IconifyIcon?

/**
 * Lists available icons in `prefix:name` form, or `@provider:prefix:name` for icons from a
 * non-default provider.
 *
 * @param provider only list icons from this provider; lists all providers when `null`.
 * @param prefix only list icons with this prefix; used only together with [provider].
 */
external fun listIcons(
    provider: String? = definedExternally,
    prefix: String? = definedExternally,
): Array<String>

/**
 * Adds an icon to storage.
 *
 * @param name icon name such as `mdi:home`.
 * @param data icon data, or `null` to mark the icon as missing.
 * @return `false` if [name] is invalid.
 */
external fun addIcon(name: String, data: IconifyIcon?): Boolean

/**
 * Adds an icon set to storage.
 *
 * @param data icon set data.
 * @param provider provider to store the set under; defaults to `data.provider`.
 * @return `false` if the icon set is invalid.
 */
external fun addCollection(data: IconifyJSON, provider: String? = definedExternally): Boolean

/**
 * Loads one icon from the Iconify API.
 *
 * The returned promise resolves with full icon data and rejects if the icon cannot be loaded.
 */
external fun loadIcon(icon: String): Promise<IconifyIcon>

/**
 * Loads multiple icons from the Iconify API.
 *
 * @param callback receives the lists of loaded, missing and pending icons, plus a function that
 * unsubscribes from further callbacks.
 * @return a function that aborts loading.
 */
external fun loadIcons(
    icons: Array<String>,
    callback: ((loaded: Array<IconifyIconName>, missing: Array<IconifyIconName>, pending: Array<IconifyIconName>, unsubscribe: () -> Unit) -> Unit)? = definedExternally,
): () -> Unit

/**
 * Registers or replaces the API configuration for [provider].
 *
 * @return `false` if the configuration is invalid.
 */
external fun addAPIProvider(provider: String, config: IconifyAPIProviderConfig): Boolean
