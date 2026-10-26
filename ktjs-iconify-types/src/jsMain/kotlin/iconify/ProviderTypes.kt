package iconify

/**
 * Links to the provider's website.
 */
external interface APIProviderRawDataLinks {
    /**
     * Link to the collections list.
     */
    var home: String?

    /**
     * Link to a collection page. Supports `{prefix}`.
     */
    var collection: String?

    /**
     * Link to an icon page. Supports `{prefix}` and `{name}`.
     */
    var icon: String?
}

/**
 * NPM packages for the provider's icons, used in code samples.
 */
external interface APIProviderRawDataNPM {
    /**
     * Package name for installation. Supports `{prefix}`.
     */
    var `package`: String?

    /**
     * Icon import source. Supports `{prefix}` and `{name}`.
     */
    var icon: String?
}

/**
 * Raw API provider data, as provided by an Iconify API.
 */
external interface APIProviderRawData {
    /**
     * Provider name as used in icon names.
     */
    var provider: String

    /**
     * Human readable provider name.
     */
    var title: String?

    /**
     * API endpoint or endpoints.
     *
     * Usually redundant, because the endpoint that returned the data is already known.
     */
    var api: StringOrStringArray?

    /**
     * Links to the provider's website.
     */
    var links: APIProviderRawDataLinks?

    /**
     * NPM packages for the provider's icons.
     */
    var npm: APIProviderRawDataNPM?

    /**
     * SVG URL template, including the full host name and the `{prefix}` and `{name}` variables.
     *
     * For example, `https://api.iconify.design/{prefix}/{name}.svg`.
     */
    var svg: String?
}
