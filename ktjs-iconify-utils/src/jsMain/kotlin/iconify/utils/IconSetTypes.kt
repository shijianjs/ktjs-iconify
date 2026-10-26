package iconify.utils

import iconify.DoubleOrDoubleArray
import js.objects.Record
import js.reflect.unsafeCast

/**
 * Palette status in the legacy icon set information: `"Colorless"` or `"Colorful"`.
 */
sealed external interface LegacyIconifyPalette

/**
 * 包装来自 JavaScript 的值。优先用 [LegacyIconifyPalettes] 里的常量。
 */
fun LegacyIconifyPalette(value: String): LegacyIconifyPalette = unsafeCast(value)

/**
 * [LegacyIconifyPalette] 的全部字面量。
 */
object LegacyIconifyPalettes {
    inline val colorless: LegacyIconifyPalette get() = LegacyIconifyPalette("Colorless")
    inline val colorful: LegacyIconifyPalette get() = LegacyIconifyPalette("Colorful")
}

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
    var height: DoubleOrDoubleArray?

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
    var palette: LegacyIconifyPalette?

    /**
     * `true` if the icon set is hidden from public icon set listings.
     */
    var hidden: Boolean?
}

/**
 * 上游 `ParentIconsList`：父级图标列表，第一个是直接父级。
 */
typealias ParentIconsList = Array<String>

/**
 * 上游 `ParentIconsTree`：图标名到父级列表的映射，没有父级或链断裂时为 `null`。
 */
typealias ParentIconsTree = Record<String, ParentIconsList?>
