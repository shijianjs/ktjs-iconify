package iconify.tools

import iconify.IconifyIcon

/**
 * Icon category.
 */
external interface IconCategory {
    /**
     * Category title.
     */
    var title: String

    /**
     * Approximate number of icons in the category.
     *
     * Updated to the correct number when the icon set is exported.
     */
    var count: Double
}

/**
 * SVG viewBox: `left`, `top`, `width` and `height`.
 */
external interface IconViewBox {
    /**
     * Left position of the viewBox.
     */
    var left: Double

    /**
     * Top position of the viewBox.
     */
    var top: Double

    /**
     * Width of the viewBox.
     */
    var width: Double

    /**
     * Height of the viewBox.
     */
    var height: Double
}

/**
 * Properties shared by icons and aliases.
 */
external interface CommonIconProps {
    /**
     * Left position of the viewBox.
     */
    var left: Double?

    /**
     * Top position of the viewBox.
     */
    var top: Double?

    /**
     * Width of the viewBox.
     */
    var width: Double?

    /**
     * Height of the viewBox.
     */
    var height: Double?

    /**
     * Number of 90 degree rotations.
     */
    var rotate: Double?

    /**
     * Horizontal flip.
     */
    var hFlip: Boolean?

    /**
     * Vertical flip.
     */
    var vFlip: Boolean?

    /**
     * `true` if the icon is hidden from search results.
     */
    var hidden: Boolean?
}

/**
 * Fully resolved icon.
 */
external interface ResolvedIconifyIcon : IconifyIcon {
    /**
     * `true` if the icon is hidden from search results.
     */
    var hidden: Boolean?
}

/**
 * Result of checking which icons belong to each theme.
 */
external interface CheckThemeResult {
    /**
     * Theme name to matching icon names.
     *
     * Keys are sorted longest first. An empty key matches all icons that do not belong to another
     * theme.
     */
    var valid: dynamic

    /**
     * Icons that do not match any theme.
     */
    var invalid: Array<String>
}

/**
 * Options for SVG cleanup.
 */
external interface CleanupSVGOptions {
    /**
     * Keep `<title>` elements.
     *
     * Defaults to `false`; titles are removed as unsupported.
     */
    var keepTitles: Boolean?
}

/**
 * Item passed to the [parseSVG] callback.
 */
external interface ParseSVGCallbackItem {
    /**
     * Parsed XML node.
     */
    var node: dynamic

    /**
     * SVG instance being parsed.
     */
    var svg: SVG

    /**
     * Parent elements, direct parent first and the `<svg>` root last.
     */
    var parents: Array<ParseSVGCallbackItem>

    /**
     * Set to `false` to skip the children of this node.
     */
    var testChildren: Boolean

    /**
     * Set to `true` to remove this node.
     */
    var removeNode: Boolean
}

/**
 * Options for [analyseSVGStructure].
 */
external interface AnalyseSVGStructureOptions {
    /**
     * Fix errors and print warnings instead of throwing exceptions.
     */
    var fixErrors: Boolean?
}

/**
 * Result of [analyseSVGStructure].
 */
external interface AnalyseSVGStructureResult {
    /**
     * All elements, keyed by their index.
     *
     * Elements have extra internal properties that describe IDs, links, reuse and palette usage.
     */
    var elements: dynamic

    /**
     * IDs found in the SVG, keyed by ID and mapped to the element index.
     */
    var ids: dynamic

    /**
     * Links from elements to the IDs they reference.
     */
    var links: dynamic

    /**
     * Element tree for elements that inherit attributes from their parent.
     */
    var tree: dynamic
}

/**
 * Options for [runSVGO].
 */
external interface SVGOptions {
    /**
     * Parse the SVG multiple times for better optimisation.
     *
     * Defaults to `true`.
     */
    var multipass: Boolean?

    /**
     * SVGO plugin list.
     *
     * When omitted, the default Iconify plugin set is used.
     */
    var plugins: Array<dynamic>?

    /**
     * Clean up IDs: a prefix string, a function that maps old IDs to new ones, or `false` to
     * disable.
     *
     * Defaults to `"svgID"`. Do not use dashes, they break some SVG animations.
     */
    var cleanupIDs: dynamic

    /**
     * Keep shapes: skip plugins that modify shapes.
     */
    var keepShapes: Boolean?
}

/**
 * Options for [importDirectory] and [importDirectorySync].
 */
external interface ImportDirectoryOptions : CleanupSVGOptions {
    /**
     * Icon set prefix. Can also be set later.
     */
    var prefix: String?

    /**
     * Include files from subdirectories.
     *
     * Defaults to `true`.
     */
    var includeSubDirs: Boolean?

    /**
     * Callback that returns an icon keyword, or `undefined` to skip the file.
     *
     * Receives the file entry, the default keyword derived from the filename, and the icon set.
     * Can return a promise.
     */
    var keyword: dynamic

    /**
     * How to handle icons that fail to import: `false` to throw, `"warn"` to print a warning, or
     * `true` to ignore.
     *
     * Defaults to `false`.
     */
    var ignoreImportErrors: dynamic
}

/**
 * Common options for exporting to a directory.
 */
external interface ExportTargetOptions {
    /**
     * Target directory.
     */
    var target: String

    /**
     * Remove old files before exporting.
     *
     * Defaults to `false`.
     */
    var cleanup: Boolean?
}

/**
 * Options to write custom files during export.
 */
external interface ExportOptionsWithCustomFiles {
    /**
     * Custom files, keyed by filename.
     *
     * A string is written as-is, an object is written as JSON, and `null` deletes the file.
     */
    var customFiles: dynamic
}

/**
 * Options for [exportToDirectory].
 */
external interface ExportToDirectoryOptions : ExportTargetOptions {
    /**
     * Set the icon height to `auto`, so width and height match the viewBox.
     *
     * When `false`, the height is set to `1em`. Defaults to `true`.
     */
    var autoHeight: Boolean?

    /**
     * Include aliases.
     *
     * Defaults to `true`.
     */
    var includeAliases: Boolean?

    /**
     * Include characters.
     *
     * Defaults to `false`.
     */
    var includeChars: Boolean?

    /**
     * Log stored files.
     *
     * Defaults to `false`.
     */
    var log: Boolean?
}

/**
 * Options for [exportIconPackage].
 */
external interface ExportIconPackageOptions : ExportTargetOptions, ExportOptionsWithCustomFiles {
    /**
     * Content of `package.json`.
     */
    var `package`: dynamic

    /**
     * Export ES modules instead of CommonJS.
     *
     * Defaults to `true`.
     */
    var module: Boolean?

    /**
     * Custom content for the generated `.d.ts` files.
     */
    var typesContent: String?

    /**
     * Use a wildcard version for `@iconify/types` in `package.json`.
     *
     * Defaults to `true`.
     */
    var wildcardTypesVersion: Boolean?
}

/**
 * Options for [exportJSONPackage].
 */
external interface ExportJSONPackageOptions : ExportTargetOptions, ExportOptionsWithCustomFiles {
    /**
     * Content of `package.json`.
     */
    var `package`: dynamic

    /**
     * Callback to update the generated `package.json` contents.
     */
    var customisePackage: ((dynamic) -> Unit)?

    /**
     * Use a wildcard version for `@iconify/types` in `package.json`.
     *
     * Defaults to `true`.
     */
    var wildcardTypesVersion: Boolean?
}

/**
 * Options for [compareDirectories].
 */
external interface CompareDirectoriesOptions {
    /**
     * Ignore new line differences in text files.
     *
     * Defaults to `true`.
     */
    var ignoreNewLine: Boolean?

    /**
     * Ignore version numbers in `package.json`.
     *
     * Defaults to `true`.
     */
    var ignoreVersions: Boolean?

    /**
     * Additional file extensions to treat as text.
     */
    var textExtensions: Array<String>?
}

/**
 * Result of running a shell command.
 */
external interface ExecResult {
    /**
     * Standard output.
     */
    var stdout: String

    /**
     * Standard error.
     */
    var stderr: String
}

/**
 * Result of [parseColors] and [validateColors].
 */
external interface FindColorsResult {
    /**
     * Custom colors found in the SVG.
     *
     * Parsed as [iconify.utils.Color] when possible, otherwise kept as a string.
     */
    var colors: Array<dynamic>

    /**
     * `true` if the SVG uses the default color.
     */
    var hasUnsetColor: Boolean

    /**
     * `true` if the SVG has a global style, which makes detecting the default fill unreliable.
     */
    var hasGlobalStyle: Boolean
}

/**
 * Options for [parseColors] and [validateColors].
 */
external interface ParseColorsOptions : AnalyseSVGStructureOptions {
    /**
     * Callback invoked for each color.
     *
     * Receives the attribute, the color string, the parsed color or `null`, and, for colors in
     * elements, the tag name and element. Return a new color, `"unset"` to remove the attribute,
     * or `"remove"` to remove the shape or rule.
     */
    var callback: dynamic

    /**
     * Color for elements that use the default color.
     *
     * Can be a parsed color, a color string, or a callback that receives the property, element,
     * tree item and analysis result.
     */
    var defaultColor: dynamic
}
