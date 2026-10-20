package iconify

/**
 * Icon dimensions.
 *
 * Used by icons, aliases and the root of an icon set as default values.
 *
 * [Online Documentation](https://iconify.design/docs/types/iconify-json.html)
 */
external interface IconifyDimenisons {
    /** Left position of viewBox. Defaults to `0`. */
    var left: Double?

    /** Top position of viewBox. Defaults to `0`. */
    var top: Double?

    /** Width of viewBox. Defaults to `16`. */
    var width: Double?

    /** Height of viewBox. Defaults to `16`. */
    var height: Double?
}

/**
 * Icon transformations.
 */
external interface IconifyTransformations {
    /** Number of 90 degree rotations. */
    var rotate: Double?

    /** Horizontal flip. */
    var hFlip: Boolean?

    /** Vertical flip. */
    var vFlip: Boolean?
}

external interface IconifyOptional : IconifyDimenisons, IconifyTransformations

/**
 * Alias to another icon in the same icon set.
 */
external interface IconifyAlias : IconifyOptional {
    /** Parent icon name, without prefix. */
    var parent: String
}

/**
 * Icon data as stored in an icon set.
 */
external interface IconifyIcon : IconifyOptional {
    /** Icon body: `<path d="..." />`. */
    var body: String
}

external interface ExtendedIconifyIcon : IconifyIcon {
    /** True if the icon is hidden from search but still accessible. */
    var hidden: Boolean?
}

external interface ExtendedIconifyAlias : IconifyAlias {
    var hidden: Boolean?
}

external interface IconifyInfoAuthor {
    var name: String
    var url: String?
}

external interface IconifyInfoLicense {
    /** Human readable license name. */
    var title: String

    /** SPDX license identifier. */
    var spdx: String?

    /** License URL. */
    var url: String?
}

/**
 * Icon set information block.
 */
external interface IconifyInfo {
    var name: String
    var total: Double?
    var version: String?
    var author: IconifyInfoAuthor
    var license: IconifyInfoLicense
    var samples: Array<String>?

    /** Icon grid: a number or an array of numbers. */
    var height: dynamic

    var displayHeight: Double?
    var category: String?
    var tags: Array<String>?

    /** `true` if icons have a predefined color scheme, `false` if they use `currentColor`. */
    var palette: Boolean?

    var hidden: Boolean?
}

/**
 * Metadata stored in an icon set JSON.
 *
 * [chars] maps characters such as `f000` to icon names, [categories] maps category
 * titles to icon name lists, and [themes] is the old themes format keyed by a unique string.
 */
external interface IconifyMetaData {
    var info: IconifyInfo?
    var chars: dynamic
    var categories: dynamic

    /** Old themes format. */
    var themes: dynamic

    /** New themes format. Key is a prefix or suffix, value is a title. */
    var prefixes: dynamic
    var suffixes: dynamic
}

/**
 * JSON structure containing only icon data.
 */
external interface IconifyJSONIconsData : IconifyDimenisons {
    /** Icon set prefix, required. */
    var prefix: String

    /** API provider, optional. */
    var provider: String?

    /** Icons, keyed by name without prefix. */
    var icons: dynamic

    /** Aliases, keyed by name without prefix. */
    var aliases: dynamic?
}

/**
 * Full icon set JSON structure.
 *
 * Optional values at the root are defaults for the icons in the set.
 */
external interface IconifyJSON : IconifyJSONIconsData, IconifyMetaData {
    /** Last modification time of icon data, as a Unix timestamp in seconds. */
    var lastModified: Double?

    /** Missing icons, returned by the Iconify API. */
    var not_found: Array<String>?
}

/**
 * Structure of `@iconify-json/{prefix}` package exports.
 */
external interface IconifyJSONPackageExports {
    var info: IconifyInfo
    var icons: IconifyJSON
    var metadata: IconifyMetaData
    var chars: dynamic
}
