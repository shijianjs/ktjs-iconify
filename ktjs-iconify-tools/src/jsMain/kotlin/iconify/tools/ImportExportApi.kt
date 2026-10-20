@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.Promise

/**
 * Import all SVG files from a directory.
 */
external fun importDirectory(
    path: String,
    options: ImportDirectoryOptions? = definedExternally,
): Promise<IconSet>

external fun importDirectorySync(
    path: String,
    options: ImportDirectoryOptions? = definedExternally,
): IconSet

/**
 * Export an icon set as one SVG file per icon.
 */
external fun exportToDirectory(
    iconSet: IconSet,
    options: ExportToDirectoryOptions,
): Promise<Array<String>>

/**
 * Export an icon set as an `@iconify-icons/{prefix}` style package.
 */
external fun exportIconPackage(
    iconSet: IconSet,
    options: ExportIconPackageOptions,
): Promise<Array<String>>

/**
 * Export an icon set as an `@iconify-json/{prefix}` style package.
 */
external fun exportJSONPackage(
    iconSet: IconSet,
    options: ExportJSONPackageOptions,
): Promise<Array<String>>

/**
 * Merge two icon sets. Icons from [newIcons] take precedence.
 */
external fun mergeIconSets(oldIcons: IconSet, newIcons: IconSet): IconSet

/**
 * Add palette and size tags to an icon set.
 */
external fun addTagsToIconSet(iconSet: IconSet, customTags: Array<String>? = definedExternally): Array<String>

/**
 * Normalise and prepare an export directory.
 */
external fun prepareDirectoryForExport(options: ExportTargetOptions): Promise<String>
