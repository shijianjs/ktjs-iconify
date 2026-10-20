package iconify

/**
 * Links to the provider's website.
 */
external interface APIProviderRawDataLinks {
    /** Collections list. */
    var home: String?

    /** Collection page. Supports `{prefix}`. */
    var collection: String?

    /** Icon page. Supports `{prefix}` and `{name}`. */
    var icon: String?
}

external interface APIProviderRawDataNPM {
    /** Package name for installation. Supports `{prefix}`. */
    var `package`: String?

    /** Icon import source. Supports `{prefix}` and `{name}`. */
    var icon: String?
}

/**
 * Raw API provider data, as provided by an Iconify API.
 */
external interface APIProviderRawData {
    /** Provider name as used in icon names. */
    var provider: String

    /** Human readable provider name. */
    var title: String?

    /** API endpoint or endpoints. */
    var api: dynamic

    var links: APIProviderRawDataLinks?
    var npm: APIProviderRawDataNPM?

    /** SVG generator URL, supports `{prefix}` and `{name}`. */
    var svg: String?
}
