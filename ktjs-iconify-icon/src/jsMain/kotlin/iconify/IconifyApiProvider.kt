package iconify

/**
 * Icon name split into parts.
 */
external interface IconifyIconName {
    /** API provider name; empty for the default provider. */
    val provider: String

    /** Icon set prefix, such as `mdi`. */
    val prefix: String

    /** Icon name without prefix, such as `home`. */
    val name: String
}

/**
 * API provider configuration for [addAPIProvider].
 */
external interface IconifyAPIProviderConfig {
    /** API hosts, tried in order. */
    var resources: Array<String>

    /** Root path after the domain and before the icon prefix. Defaults to `/`. */
    var path: String?

    /** Maximum URL length; longer icon lists are split across requests. Defaults to `500`. */
    var maxURL: Int?
}
