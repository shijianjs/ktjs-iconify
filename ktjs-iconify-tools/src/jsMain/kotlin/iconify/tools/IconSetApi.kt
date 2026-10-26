@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.IconifyInfo
import iconify.IconifyJSON
import iconify.utils.IconifyIconCustomisations
import js.collections.JsSet
import js.objects.Record
import kotlin.js.Promise

/**
 * Editable icon set.
 *
 * An icon set contains icons, aliases, variations, metadata, characters and categories.
 * Use class methods to modify entries and categories instead of writing to [entries] and
 * [categories] directly.
 */
external class IconSet {
    constructor(data: IconifyJSON)

    /** Icon set prefix. */
    var prefix: String

    /** Last modification time, as a Unix timestamp in seconds. */
    var lastModified: Double

    /** Entries keyed by icon name. Use class methods to modify them. */
    var entries: Record<String, IconSetIconEntry>

    /** Icon set metadata. */
    var info: IconifyInfo?

    /** Native `Set<IconCategory>`. Use class methods to modify it. */
    var categories: JsSet<IconCategory>

    /** Theme prefixes: prefix without dash mapped to theme title. */
    var prefixes: Record<String, String>

    /** Theme suffixes: suffix without dash mapped to theme title. */
    var suffixes: Record<String, String>

    /**
     * Load icon set data, replacing the current contents.
     *
     * Aliases with properties become variations. Characters and categories are attached
     * to entries, and legacy `themes` are converted to [prefixes] and [suffixes].
     */
    fun load(data: IconifyJSON)

    /**
     * Update the last modification time. Without [value], uses the current time.
     */
    fun updateLastModified(value: Double = definedExternally)

    /**
     * List entry names of the requested types.
     *
     * Defaults to `icon` and `variation`; aliases are not included unless requested.
     */
    fun list(types: Array<IconSetIconType>? = definedExternally): Array<String>

    /**
     * Iterate over entries. The callback can return `false` to stop the loop.
     *
     * Includes icons, variations and aliases by default and supports asynchronous
     * callbacks.
     */
    fun forEach(
        callback: IconSetAsyncForEachCallback,
        types: Array<IconSetIconType>? = definedExternally,
    ): Promise<Unit>

    /**
     * Synchronous version of [forEach]. The callback can return `false` to stop the loop.
     */
    fun forEachSync(
        callback: IconSetSyncForEachCallback,
        types: Array<IconSetIconType>? = definedExternally,
    )

    /**
     * Parent icon list for each icon, `null` when an alias cannot be resolved.
     *
     * The list starts with the direct parent and ends with the icon; it does not include
     * the icon itself. Icons have an empty list. Without [names], checks all entries.
     */
    fun getTree(names: Array<String>? = definedExternally): IconSetTree

    /**
     * Resolve an icon, alias or variation to icon data.
     *
     * Returns `null` if the entry does not exist or its alias chain is broken. With
     * [full], missing properties are filled with defaults such as `width: 16` and
     * `height: 16`.
     */
    fun resolve(
        name: String,
        full: Boolean = definedExternally,
    ): ResolvedIconifyIcon?

    /**
     * Generate an SVG string for an icon.
     *
     * Returns `null` if the icon cannot be resolved. Without [customisations], width and
     * height default to `auto`.
     */
    @JsName("toString")
    fun toHtml(
        name: String,
        customisations: IconifyIconCustomisations? = definedExternally,
    ): String?

    /**
     * Get an [SVG] instance for an icon, or `null` if it cannot be resolved.
     */
    fun toSVG(name: String): SVG?

    /**
     * Export the icon set as Iconify JSON.
     *
     * Icons are sorted by name and their bodies trimmed, characters, categories and
     * themes are included, `info.total` is updated, and the result is minified. With
     * [validate] (default), aliases and variations with broken parent chains are omitted.
     */
    fun export(validate: Boolean = definedExternally): IconifyJSON

    /**
     * Get the character map (`char` to icon name), optionally limited to [names].
     */
    fun chars(names: Array<String>? = definedExternally): Record<String, String>

    /**
     * Count icons, excluding aliases and hidden icons.
     */
    fun count(): Int

    /**
     * Find a category by title, optionally creating it with [add].
     */
    fun findCategory(title: String, add: Boolean): IconCategory?

    /**
     * List icons in a category.
     *
     * Updates the category count, excluding aliases and hidden icons, and removes the
     * category if it is empty. Returns `null` when the category does not exist or is empty.
     */
    fun listCategory(category: IconCategoryOrString): Array<String>?

    /** Check if an entry with this name exists. */
    fun exists(name: String): Boolean

    /**
     * Remove an icon. When [removeDependencies] is a string, aliases are re-parented to it.
     *
     * [removeDependencies] defaults to `true`, which also removes aliases and variations
     * that depend on the icon. A string must be the name of an existing icon; in that case
     * dependants are re-parented and the method returns `0`. Otherwise returns the number
     * of removed entries.
     */
    fun remove(name: String, removeDependencies: BooleanOrString? = definedExternally): Int

    /**
     * Rename an entry and update aliases that reference it.
     *
     * An existing entry with [newName] is replaced. Returns `false` when the names are
     * the same or [oldName] does not exist.
     */
    fun rename(oldName: String, newName: String): Boolean

    /**
     * Add or update an entry. The item has a `type` of `"icon"`, `"alias"` or `"variation"`.
     *
     * Returns `false` when the parent of an alias or variation does not exist.
     */
    fun setItem(name: String, item: IconSetIconEntry): Boolean

    /** Add or update an icon from resolved icon data. */
    fun setIcon(name: String, icon: ResolvedIconifyIcon): Boolean

    /**
     * Add or update an alias. Returns `false` when [parent] does not exist.
     */
    fun setAlias(name: String, parent: String): Boolean

    /**
     * Add or update a variation with [props]. Returns `false` when [parent] does not exist.
     */
    fun setVariation(name: String, parent: String, props: CommonIconProps): Boolean

    /**
     * Add or update an icon from an [SVG], using its viewBox as dimensions.
     *
     * When replacing an existing icon, its characters are kept; its categories are kept
     * only if the previous entry was an icon.
     */
    fun fromSVG(name: String, svg: SVG): Boolean

    /**
     * Add or remove a character for an icon. Returns `true` if the character map changed.
     */
    fun toggleCharacter(iconName: String, char: String, add: Boolean): Boolean

    /**
     * Add or remove a category for an icon. Returns `true` if the category changed.
     */
    fun toggleCategory(iconName: String, category: String, add: Boolean): Boolean

    /**
     * Find icons that belong to themes.
     *
     * Uses [prefixes] when [prefix] is `true`, otherwise [suffixes]. Longer keys take
     * precedence; an empty key matches all remaining icons. Returns `valid`, mapping each
     * theme key to icon names, and `invalid`, listing icons that match no theme.
     */
    fun checkTheme(prefix: Boolean): CheckThemeResult
}

/**
 * Create a blank icon set with the given prefix.
 */
external fun blankIconSet(prefix: String): IconSet

/**
 * Sort theme keys, longest first. Modifies and returns the list.
 *
 * Keys of equal length are sorted alphabetically.
 */
external fun sortThemeKeys(keys: Array<String>): Array<String>
