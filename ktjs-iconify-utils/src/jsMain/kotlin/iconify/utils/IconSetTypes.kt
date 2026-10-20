package iconify.utils

external interface IconSetValidationOptions {
    /** Attempt to fix the icon set instead of throwing an error. */
    var fix: Boolean?

    /** Prefix and provider to add when missing. */
    var prefix: String?
    var provider: String?
}

/**
 * Icon set information in the legacy format used by the Iconify API and `collections.json`.
 */
external interface LegacyIconifyInfo {
    var name: String
    var total: Double?
    var version: String?
    var author: String?
    var url: String?
    var license: String?
    var licenseURL: String?
    var licenseSPDX: String?
    var samples: Array<String>?
    var height: dynamic
    var displayHeight: Double?
    var samplesHeight: Double?
    var category: String?

    /** `"Colorless"` or `"Colorful"`. */
    var palette: String?

    var hidden: Boolean?
}
