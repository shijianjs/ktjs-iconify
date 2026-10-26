package iconify.tools

import iconify.IconifyIcon
import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.JsAny

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
 * SVG viewBox: `left`, `top`, `width` and `height`, plus the optional horizontal center
 * `cx`.
 */
external interface IconViewBox {
    /**
     * Left position of the viewBox.
     *
     * Omitted when the viewBox starts at `0`.
     */
    var left: Double?

    /**
     * Top position of the viewBox.
     *
     * Omitted when the viewBox starts at `0`.
     */
    var top: Double?

    /**
     * Horizontal center, used for icons that are horizontally symmetric.
     */
    var cx: Double?

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
    var node: ParsedXMLTagElement

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
    var elements: ElementsMap

    /**
     * IDs found in the SVG, keyed by ID and mapped to the element index.
     */
    var ids: Record<String, Double>

    /**
     * Links from elements to the IDs they reference.
     */
    var links: Array<LinkToElementWithID>

    /**
     * Element tree for elements that inherit attributes from their parent.
     */
    var tree: ElementsTreeItem
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
 * Value of a custom file in [ExportOptionsWithCustomFiles.customFiles].
 *
 * 上游 `string | Record<string, unknown> | null`。用同名工厂函数包装；`null` 表示删除
 * 该文件，直接写进 [Record] 的值即可。
 */
external interface CustomFile

fun CustomFile(value: String): CustomFile = unsafeCast(value)

fun CustomFile(value: Record<String, JsAny?>): CustomFile = unsafeCast(value)

/**
 * Options to write custom files during export.
 */
external interface ExportOptionsWithCustomFiles {
    /**
     * Custom files, keyed by filename.
     *
     * A string is written as-is, an object is written as JSON, and `null` deletes the file.
     */
    var customFiles: Record<String, CustomFile?>?
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
     *
     * 上游类型是 `Record<string, unknown>`。
     */
    var `package`: Record<String, JsAny?>?

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
     *
     * 上游类型是 `Record<string, unknown>`。
     */
    var `package`: Record<String, JsAny?>?

    /**
     * Callback to update the generated `package.json` contents.
     */
    var customisePackage: ((Record<String, JsAny?>) -> Unit)?

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
