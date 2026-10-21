package iconify

/**
 * Icon dimensions.
 *
 * Defines the viewBox dimensions for SVG icons. These properties can be used in:
 * - icon definitions, applied directly;
 * - alias definitions, where they override the parent icon's properties;
 * - the root of an icon set JSON, where they are defaults for all icons.
 *
 * [Online Documentation](https://iconify.design/docs/types/iconify-json.html)
 */
external interface IconifyDimenisons {
    /**
     * Left position of the viewBox.
     *
     * Defaults to `0`.
     */
    var left: Double?

    /**
     * Top position of the viewBox.
     *
     * Defaults to `0`.
     */
    var top: Double?

    /**
     * Width of the viewBox.
     *
     * Defaults to `16`.
     */
    var width: Double?

    /**
     * Height of the viewBox.
     *
     * Defaults to `16`.
     */
    var height: Double?
}

/**
 * Icon transformations.
 *
 * Used in icon definitions, where they are applied directly, and in alias definitions, where they
 * are merged with the parent icon's properties.
 */
external interface IconifyTransformations {
    /**
     * Number of 90 degree rotations: `0` = 0°, `1` = 90°, `2` = 180°, `3` = 270°.
     *
     * When an alias is merged with its icon, rotations are added.
     *
     * Defaults to `0`.
     */
    var rotate: Double?

    /**
     * Horizontal flip.
     *
     * When an alias is merged with its icon, flips are combined, so two flips cancel out.
     *
     * Defaults to `false`.
     */
    var hFlip: Boolean?

    /**
     * Vertical flip.
     *
     * When an alias is merged with its icon, flips are combined, so two flips cancel out.
     *
     * Defaults to `false`.
     */
    var vFlip: Boolean?
}

/**
 * Dimensions and transformations.
 *
 * All properties are optional, so this is the common base of icons and aliases.
 */
external interface IconifyOptional : IconifyDimenisons, IconifyTransformations

/**
 * Alias to another icon in the same icon set.
 *
 * Properties from [IconifyOptional] override the parent icon's properties. Transformations are
 * merged instead of overridden.
 */
external interface IconifyAlias : IconifyOptional {
    /**
     * Parent icon name, without prefix.
     */
    var parent: String
}

/**
 * Icon definition.
 *
 * Contains the SVG body and optional properties. Missing properties are taken from the root of the
 * icon set JSON.
 */
external interface IconifyIcon : IconifyOptional {
    /**
     * SVG body content: the inner SVG elements.
     *
     * For example, `<path d="M12 2l3.09 6.26..."/>`.
     */
    var body: String
}

/**
 * Icon definition with API-specific attributes.
 */
external interface ExtendedIconifyIcon : IconifyIcon {
    /**
     * `true` if the icon is hidden from search results.
     *
     * Hidden icons stay in icon sets for backward compatibility when they are deprecated or
     * removed, so existing references do not break.
     */
    var hidden: Boolean?
}

/**
 * Alias definition with API-specific attributes.
 */
external interface ExtendedIconifyAlias : IconifyAlias {
    /**
     * `true` if the alias is hidden from search results.
     *
     * Hidden aliases stay in icon sets for backward compatibility when they are deprecated or
     * removed, so existing references do not break.
     */
    var hidden: Boolean?
}

/**
 * Author information.
 */
external interface IconifyInfoAuthor {
    /**
     * Author name.
     */
    var name: String

    /**
     * URL to the author's website or the icon set homepage.
     */
    var url: String?
}

/**
 * Licensing information.
 */
external interface IconifyInfoLicense {
    /**
     * Human readable license name, such as `Apache License 2.0`.
     */
    var title: String

    /**
     * SPDX license identifier, such as `Apache-2.0`.
     *
     * [SPDX License List](https://spdx.org/licenses/)
     */
    var spdx: String?

    /**
     * URL to the full license text.
     */
    var url: String?
}

/**
 * Icon set information block.
 */
external interface IconifyInfo {
    /**
     * Name of the icon set.
     */
    var name: String

    /**
     * Total number of icons in the set.
     */
    var total: Double?

    /**
     * Version string of the icon set.
     */
    var version: String?

    /**
     * Author information.
     */
    var author: IconifyInfoAuthor

    /**
     * Licensing information.
     */
    var license: IconifyInfoLicense

    /**
     * Icon names to use as samples in icon set listings.
     */
    var samples: Array<String>?

    /**
     * Icon grid, for display purposes: a number or an array of numbers.
     */
    var height: dynamic

    /**
     * Display height of icon samples in pixels.
     *
     * Between 16 and 24 pixels is optimal for icon browsers.
     */
    var displayHeight: Double?

    /**
     * Category in the Iconify icon sets list.
     */
    var category: String?

    /**
     * Tags used to group and filter similar icon sets.
     */
    var tags: Array<String>?

    /**
     * `true` if icons have a predefined color scheme, `false` if they use `currentColor`.
     *
     * Icon sets should avoid mixing both types for a consistent search experience.
     */
    var palette: Boolean?

    /**
     * `true` if the icon set is hidden from public icon set listings.
     *
     * Used for private or deprecated icon sets.
     *
     * Defaults to `false`.
     */
    var hidden: Boolean?
}

/**
 * Metadata for icon set browsing and organization.
 *
 * [chars] maps Unicode character codes such as `f000` to icon names, [categories] maps category
 * titles to icon name lists, and [themes] is the legacy themes format keyed by an arbitrary unique
 * string.
 */
external interface IconifyMetaData {
    /**
     * Icon set information block.
     *
     * Used for public icon sets; can be omitted for private icon sets.
     */
    var info: IconifyInfo?

    /**
     * Character to icon mappings, for font-based icon sets.
     */
    var chars: dynamic

    /**
     * Categories, keyed by category title, each holding a list of icon names.
     */
    var categories: dynamic

    /**
     * Legacy themes format keyed by a unique string.
     */
    var themes: dynamic

    /**
     * Theme prefixes, keyed by icon name prefix including the dash.
     */
    var prefixes: dynamic

    /**
     * Theme suffixes, keyed by icon name suffix including the dash.
     */
    var suffixes: dynamic
}

/**
 * Icon set JSON without browsing metadata.
 *
 * Contains the data needed to render icons. Root dimensions are defaults for icons that do not
 * specify their own.
 */
external interface IconifyJSONIconsData : IconifyDimenisons {
    /**
     * Unique prefix for all icons in this set. Required.
     */
    var prefix: String

    /**
     * API provider, optional.
     */
    var provider: String?

    /**
     * Icons, keyed by name without prefix.
     */
    var icons: dynamic

    /**
     * Aliases, keyed by name without prefix.
     */
    var aliases: dynamic?
}

/**
 * Full icon set JSON structure.
 *
 * Optional values at the root are defaults for the icons in the set.
 */
external interface IconifyJSON : IconifyJSONIconsData, IconifyMetaData {
    /**
     * Last modification time of icon data, as a Unix timestamp in seconds.
     *
     * Only reflects changes to icon data, not metadata. Used to invalidate cached icons.
     */
    var lastModified: Double?

    /**
     * Icon names that were requested but do not exist.
     *
     * Returned by the Iconify API to help identify missing or misnamed icons.
     */
    var not_found: Array<String>?
}

/**
 * Structure of `@iconify-json/{prefix}` package exports.
 *
 * Icon set packages split the JSON structure into separate files, so importing one part does not
 * pull in the others.
 */
external interface IconifyJSONPackageExports {
    /**
     * Icon set information and metadata.
     */
    var info: IconifyInfo

    /**
     * Complete icon set data, including icons and aliases.
     */
    var icons: IconifyJSON

    /**
     * Metadata for browsing and organization.
     */
    var metadata: IconifyMetaData

    /**
     * Character to icon mappings, for font-based icon sets.
     */
    var chars: dynamic
}
