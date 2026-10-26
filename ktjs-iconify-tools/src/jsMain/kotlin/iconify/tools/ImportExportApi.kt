@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.Promise

/**
 * Import all SVG files from a directory.
 *
 * Subdirectories are included unless [ImportDirectoryOptions.includeSubDirs] is `false`.
 * Each icon is cleaned up with [cleanupSVG] and added to a [IconSet]. The default icon
 * name comes from the filename via [cleanupIconKeyword];
 * [ImportDirectoryOptions.keyword] can return a different name, or `undefined` to skip
 * the icon. The callback may be asynchronous.
 *
 * Import errors throw by default, despite the upstream comment claiming otherwise. Set
 * [ImportDirectoryOptions.ignoreImportErrors] to `true` to ignore them, or to `"warn"` to
 * log a warning.
 */
external fun importDirectory(
    path: String,
    options: ImportDirectoryOptions? = definedExternally,
): Promise<IconSet>

/**
 * Synchronous version of [importDirectory].
 */
external fun importDirectorySync(
    path: String,
    options: ImportDirectorySyncOptions? = definedExternally,
): IconSet

/**
 * Export an icon set as one SVG file per icon.
 *
 * Width and height are `auto` by default; with
 * [ExportToDirectoryOptions.autoHeight] set to `false`, height is `1em`. Aliases are
 * included unless [ExportToDirectoryOptions.includeAliases] is `false`, characters are
 * only exported when [ExportToDirectoryOptions.includeChars] is `true`. Returns the list
 * of written files.
 */
external fun exportToDirectory(
    iconSet: IconSet,
    options: ExportToDirectoryOptions,
): Promise<Array<String>>

/**
 * Export an icon set as an `@iconify-icons/{prefix}` style package.
 *
 * Writes a `.js` module and a `.d.ts` file for each icon, plus `package.json`. Modules
 * are ESM by default; set [ExportIconPackageOptions.module] to `false` for CommonJS.
 * [ExportIconPackageOptions.typesContent] overrides the generated type declaration.
 * Returns the list of written files.
 */
external fun exportIconPackage(
    iconSet: IconSet,
    options: ExportIconPackageOptions,
): Promise<Array<String>>

/**
 * Export an icon set as an `@iconify-json/{prefix}` style package.
 *
 * Writes `icons.json`, `info.json`, `metadata.json`, `chars.json`, `index.js`,
 * `index.mjs`, `index.d.ts` and `package.json`. Use
 * [ExportJSONPackageOptions.customisePackage] to modify `package.json` before writing.
 * Returns the list of written files.
 */
external fun exportJSONPackage(
    iconSet: IconSet,
    options: ExportJSONPackageOptions,
): Promise<Array<String>>

/**
 * Merge two icon sets. Icons from [newIcons] take precedence.
 *
 * Old icons that are identical to a new icon become aliases; conflicting icons are added
 * as hidden icons. The modification time is preserved when the merged data is identical
 * to either input.
 */
external fun mergeIconSets(oldIcons: IconSet, newIcons: IconSet): IconSet

/**
 * Add palette and size tags to an icon set.
 *
 * Detects whether the icon set is monotone or uses a palette, adds square/grid/height
 * tags based on icon dimensions, then merges [customTags] and assigns the result to
 * `info.tags`. Returns the tags.
 *
 * Deprecated upstream.
 */
external fun addTagsToIconSet(iconSet: IconSet, customTags: Array<String>? = definedExternally): Array<String>

/**
 * Normalise and prepare an export directory.
 *
 * Removes the directory when [ExportTargetOptions.cleanup] is `true`, creates it if
 * missing, and returns the normalized path without a trailing slash.
 */
external fun prepareDirectoryForExport(options: ExportTargetOptions): Promise<String>
