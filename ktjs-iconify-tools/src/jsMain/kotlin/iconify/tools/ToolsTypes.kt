package iconify.tools

import iconify.IconifyIcon

external interface IconCategory {
    var title: String
    var count: Double
}

external interface IconViewBox {
    var left: Double
    var top: Double
    var width: Double
    var height: Double
}

/**
 * Properties shared by icons and aliases.
 */
external interface CommonIconProps {
    var left: Double?
    var top: Double?
    var width: Double?
    var height: Double?
    var rotate: Double?
    var hFlip: Boolean?
    var vFlip: Boolean?
    var hidden: Boolean?
}

/**
 * Fully resolved icon.
 */
external interface ResolvedIconifyIcon : IconifyIcon {
    var hidden: Boolean?
}

external interface CheckThemeResult {
    /** Theme name to matching icon names. */
    var valid: dynamic

    var invalid: Array<String>
}

external interface CleanupSVGOptions {
    var keepTitles: Boolean?
}

external interface ParseSVGCallbackItem {
    var node: dynamic
    var svg: SVG
    var parents: Array<ParseSVGCallbackItem>
    var testChildren: Boolean
    var removeNode: Boolean
}

external interface AnalyseSVGStructureOptions {
    var fixErrors: Boolean?
}

external interface AnalyseSVGStructureResult {
    var elements: dynamic
    var ids: dynamic
    var links: dynamic
    var tree: dynamic
}

external interface SVGOptions {
    var multipass: Boolean?

    /** SVGO plugin list. When omitted, the default Iconify plugin set is used. */
    var plugins: Array<dynamic>?
    var cleanupIDs: dynamic
    var keepShapes: Boolean?
}

external interface ImportDirectoryOptions : CleanupSVGOptions {
    var prefix: String?
    var includeSubDirs: Boolean?

    /** Callback that returns an icon keyword, or `undefined` to skip the file. */
    var keyword: dynamic

    /** `true`, `false` or `"warn"`. */
    var ignoreImportErrors: dynamic
}

external interface ExportTargetOptions {
    var target: String
    var cleanup: Boolean?
}

external interface ExportOptionsWithCustomFiles {
    var customFiles: dynamic
}

external interface ExportToDirectoryOptions : ExportTargetOptions {
    var autoHeight: Boolean?
    var includeAliases: Boolean?
    var includeChars: Boolean?
    var log: Boolean?
}

external interface ExportIconPackageOptions : ExportTargetOptions, ExportOptionsWithCustomFiles {
    var `package`: dynamic
    var module: Boolean?
    var typesContent: String?
    var wildcardTypesVersion: Boolean?
}

external interface ExportJSONPackageOptions : ExportTargetOptions, ExportOptionsWithCustomFiles {
    var `package`: dynamic
    var customisePackage: ((dynamic) -> Unit)?
    var wildcardTypesVersion: Boolean?
}

external interface CompareDirectoriesOptions {
    var ignoreNewLine: Boolean?
    var ignoreVersions: Boolean?
    var textExtensions: Array<String>?
}

external interface ExecResult {
    var stdout: String
    var stderr: String
}

external interface FindColorsResult {
    var colors: Array<dynamic>
    var hasUnsetColor: Boolean
    var hasGlobalStyle: Boolean
}

external interface ParseColorsOptions : AnalyseSVGStructureOptions {
    /** Callback invoked for each color. Return a new color, `"unset"` or `"remove"`. */
    var callback: dynamic
    var defaultColor: dynamic
}
