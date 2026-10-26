package iconify.tools

import iconify.utils.AwaitableOptionalString
import iconify.utils.ParentIconsTree
import js.collections.JsSet
import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.Promise

/**
 * Entry type in an icon set: `"icon"`, `"alias"` or `"variation"`.
 */
sealed external interface IconSetIconType

/**
 * 包装来自 JavaScript 的值。优先用 [IconSetIconTypes] 里的常量。
 */
fun IconSetIconType(value: String): IconSetIconType = unsafeCast(value)

/**
 * [IconSetIconType] 的全部字面量。
 */
object IconSetIconTypes {
    inline val icon: IconSetIconType get() = IconSetIconType("icon")
    inline val alias: IconSetIconType get() = IconSetIconType("alias")
    inline val variation: IconSetIconType get() = IconSetIconType("variation")
}

/**
 * Entry in an icon set: an icon, an alias or a variation.
 *
 * The [type] property identifies the variant: cast to [IconSetIcon], [IconSetIconAlias] or
 * [IconSetIconVariation] after checking it.
 */
external interface IconSetIconEntry {
    var type: IconSetIconType
}

/**
 * Entry with characters.
 */
external interface IconWithChars {
    /** Characters mapped to this entry. */
    var chars: JsSet<String>
}

/**
 * Entry with shared icon properties.
 */
external interface IconWithPropsData : IconWithChars {
    /** Properties shared by icons and aliases. */
    var props: CommonIconProps
}

/**
 * Entry with categories.
 */
external interface IconWithCategories {
    /** Categories this entry belongs to. */
    var categories: JsSet<IconCategory>
}

/**
 * Entry that points to a parent entry.
 */
external interface IconParentData {
    /** Parent icon name. */
    var parent: String
}

/**
 * Icon entry.
 */
external interface IconSetIcon : IconSetIconEntry, IconWithPropsData, IconWithCategories {
    /** SVG body content. */
    var body: String
}

/**
 * Alias entry.
 */
external interface IconSetIconAlias : IconSetIconEntry, IconWithChars, IconParentData

/**
 * Variation: an alias with properties that override the parent icon.
 */
external interface IconSetIconVariation : IconSetIconEntry, IconWithPropsData, IconParentData

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
    var valid: Record<String, Array<String>>

    /**
     * Icons that do not match any theme.
     */
    var invalid: Array<String>
}

/**
 * Value returned by an [IconSet.forEach] callback: `false` stops the loop, any other value
 * continues it.
 */
external interface IconSetForEachResult

fun IconSetForEachResult(value: Boolean): IconSetForEachResult = unsafeCast(value)

fun IconSetForEachResult(value: Promise<Boolean?>): IconSetForEachResult = unsafeCast(value)

/**
 * Callback for [IconSet.forEach]. Return `false` (or a promise of `false` / `null`) to stop
 * the loop; `null` continues.
 */
typealias IconSetAsyncForEachCallback =
    (name: String, type: IconSetIconType) -> IconSetForEachResult?

/**
 * Callback for [IconSet.forEachSync]. Return `false` to stop the loop, `null` or `true` to
 * continue.
 */
typealias IconSetSyncForEachCallback = (name: String, type: IconSetIconType) -> Boolean?

/**
 * Category or category title accepted by [IconSet.listCategory].
 */
external interface IconCategoryOrString

fun IconCategoryOrString(value: IconCategory): IconCategoryOrString = unsafeCast(value)

fun IconCategoryOrString(value: String): IconCategoryOrString = unsafeCast(value)

/**
 * Boolean or string accepted by [IconSet.remove] for `removeDependencies`: `true` removes
 * dependants, `false` keeps them, and a string re-parents them to that icon.
 */
external interface BooleanOrString

fun BooleanOrString(value: Boolean): BooleanOrString = unsafeCast(value)

fun BooleanOrString(value: String): BooleanOrString = unsafeCast(value)

/**
 * How [importDirectory] and [importDirectorySync] handle icons that fail to import: `false`
 * throws, `"warn"` prints a warning, and `true` ignores the error.
 */
external interface ImportIgnoreErrors

fun ImportIgnoreErrors(value: Boolean): ImportIgnoreErrors = unsafeCast(value)

fun ImportIgnoreErrors(value: String): ImportIgnoreErrors = unsafeCast(value)

/**
 * File entry passed to the [ImportDirectoryOptions.keyword] callback.
 */
external interface ImportDirectoryFileEntry {
    /** Full path to the file. */
    var path: String

    /** Directory relative to the imported root, ending with `/`; empty for the root. */
    var subdir: String

    /** File name without extension. */
    var file: String

    /** File extension, including the leading dot; can be empty. */
    var ext: String
}

/**
 * Callback that returns an icon keyword, or `undefined` to skip the file.
 */
typealias ImportDirectoryKeywordCallback =
    (file: ImportDirectoryFileEntry, defaultKeyword: String, iconSet: IconSet) -> AwaitableOptionalString?

/**
 * Synchronous callback that returns an icon keyword, or `undefined` to skip the file.
 */
typealias ImportDirectoryKeywordSyncCallback =
    (file: ImportDirectoryFileEntry, defaultKeyword: String, iconSet: IconSet) -> String?

/**
 * Options shared by [importDirectory] and [importDirectorySync].
 */
external interface ImportDirectoryOptionsBase : CleanupSVGOptions {
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
     * How to handle icons that fail to import.
     *
     * Defaults to `false`.
     */
    var ignoreImportErrors: ImportIgnoreErrors?
}

/**
 * Options for [importDirectory].
 */
external interface ImportDirectoryOptions : ImportDirectoryOptionsBase {
    /**
     * Callback that returns an icon keyword, or `undefined` to skip the file.
     *
     * Receives the file entry, the default keyword derived from the filename, and the icon set.
     * Can return a promise.
     */
    var keyword: ImportDirectoryKeywordCallback?
}

/**
 * Options for [importDirectorySync].
 */
external interface ImportDirectorySyncOptions : ImportDirectoryOptionsBase {
    /**
     * Callback that returns an icon keyword, or `undefined` to skip the file.
     */
    var keyword: ImportDirectoryKeywordSyncCallback?
}

/**
 * Parent icon list for each entry, keyed by entry name.
 */
typealias IconSetTree = ParentIconsTree
