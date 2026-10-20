package iconify

/**
 * Icon name split into parts.
 */
external interface IconifyIconName {
    val provider: String
    val prefix: String
    val name: String
}

/**
 * API provider configuration for [addAPIProvider].
 */
external interface IconifyAPIProviderConfig {
    /** API endpoints, tried in order. Supports `{prefix}` and `{provider}`. */
    var resources: Array<String>

    /** Path to the icons endpoint. Defaults to the Iconify API path. */
    var path: String?

    /** Maximum number of icons per API request. */
    var maxURL: Int?
}
