@file:JsModule("iconify-icon")

package iconify

import kotlin.js.Promise

// IconifyIcon and IconifyJSON are declared once in ktjs-iconify-types and shared
// through the `iconify` package. IconifyIconName comes from IconifyApiProvider.kt,
// because it belongs to this package's own typings rather than @iconify/types.

/**
 * Checks whether the icon data is already available locally.
 */
external fun iconLoaded(name: String): Boolean

/**
 * Returns icon data, `null` if loading failed, or `undefined` if it was never loaded.
 */
external fun getIcon(name: String): IconifyIcon?

external fun listIcons(
    provider: String? = definedExternally,
    prefix: String? = definedExternally,
): Array<String>

external fun addIcon(name: String, data: IconifyIcon?): Boolean

external fun addCollection(data: IconifyJSON, provider: String? = definedExternally): Boolean

external fun loadIcon(icon: String): Promise<IconifyIcon>

/**
 * Loads multiple icons and returns a function that aborts pending requests.
 */
external fun loadIcons(
    icons: Array<String>,
    callback: ((loaded: Array<IconifyIconName>, missing: Array<IconifyIconName>, pending: Array<IconifyIconName>, unsubscribe: () -> Unit) -> Unit)? = definedExternally,
): () -> Unit

external fun addAPIProvider(provider: String, config: IconifyAPIProviderConfig): Boolean
