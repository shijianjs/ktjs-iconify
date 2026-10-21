package iconify.utils

/**
 * Options for [validateIconSet].
 */
external interface IconSetValidationOptions {
    /**
     * Attempt to fix the icon set instead of throwing an error.
     */
    var fix: Boolean?

    /**
     * Prefix to add when the icon set is missing one.
     */
    var prefix: String?

    /**
     * Provider to add when the icon set is missing one.
     */
    var provider: String?
}

/**
 * Icon set information in the legacy format used by the Iconify API and `collections.json`.
 *
 * Unlike [iconify.IconifyInfo], author and license are flat strings, and there is no `tags`.
 */
external interface LegacyIconifyInfo {
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
     * Author name.
     */
    var author: String?

    /**
     * URL to the author's website or the icon set homepage.
     */
    var url: String?

    /**
     * Human readable license name.
     */
    var license: String?

    /**
     * URL to the full license text.
     */
    var licenseURL: String?

    /**
     * SPDX license identifier.
     */
    var licenseSPDX: String?

    /**
     * Icon names to use as samples.
     */
    var samples: Array<String>?

    /**
     * Icon grid: a number or an array of numbers.
     */
    var height: dynamic

    /**
     * Display height of icon samples in pixels.
     */
    var displayHeight: Double?

    /**
     * Height of icon samples in pixels.
     */
    var samplesHeight: Double?

    /**
     * Category in the Iconify icon sets list.
     */
    var category: String?

    /**
     * `"Colorless"` or `"Colorful"`.
     */
    var palette: String?

    /**
     * `true` if the icon set is hidden from public icon set listings.
     */
    var hidden: Boolean?
}
