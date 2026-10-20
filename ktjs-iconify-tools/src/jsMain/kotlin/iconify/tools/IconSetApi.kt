@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import iconify.IconifyInfo
import iconify.IconifyJSON
import iconify.utils.IconifyIconCustomisations
import kotlin.js.Promise

/**
 * Editable icon set.
 */
external class IconSet {
    constructor(data: IconifyJSON)

    var prefix: String
    var lastModified: Double

    /** Entries keyed by icon name. Use class methods to modify them. */
    var entries: dynamic

    var info: IconifyInfo?

    /** Native `Set<IconCategory>`. Use class methods to modify it. */
    var categories: dynamic

    var prefixes: dynamic
    var suffixes: dynamic

    fun load(data: IconifyJSON)

    fun updateLastModified(value: Double = definedExternally)

    fun list(types: Array<String>? = definedExternally): Array<String>

    /**
     * Iterate over entries. The callback can return `false` to stop the loop.
     */
    fun forEach(
        callback: (name: String, type: String) -> dynamic,
        types: Array<String>? = definedExternally,
    ): Promise<Unit>

    fun forEachSync(
        callback: (name: String, type: String) -> dynamic,
        types: Array<String>? = definedExternally,
    )

    /**
     * Parent icon list for each icon, `null` when an alias cannot be resolved.
     */
    fun getTree(names: Array<String>? = definedExternally): dynamic

    fun resolve(
        name: String,
        full: Boolean = definedExternally,
    ): ResolvedIconifyIcon?

    @JsName("toString")
    fun toHtml(
        name: String,
        customisations: IconifyIconCustomisations? = definedExternally,
    ): String?

    fun toSVG(name: String): SVG?

    fun export(validate: Boolean = definedExternally): IconifyJSON

    fun chars(names: Array<String>? = definedExternally): dynamic

    fun count(): Int

    fun findCategory(title: String, add: Boolean): IconCategory?

    fun listCategory(category: dynamic): Array<String>?

    fun exists(name: String): Boolean

    /**
     * Remove an icon. When [removeDependencies] is a string, aliases are re-parented to it.
     */
    fun remove(name: String, removeDependencies: dynamic = definedExternally): Int

    fun rename(oldName: String, newName: String): Boolean

    /**
     * Add or update an entry. The item has a `type` of `"icon"`, `"alias"` or `"variation"`.
     */
    fun setItem(name: String, item: dynamic): Boolean

    fun setIcon(name: String, icon: ResolvedIconifyIcon): Boolean

    fun setAlias(name: String, parent: String): Boolean

    fun setVariation(name: String, parent: String, props: CommonIconProps): Boolean

    fun fromSVG(name: String, svg: SVG): Boolean

    fun toggleCharacter(iconName: String, char: String, add: Boolean): Boolean

    fun toggleCategory(iconName: String, category: String, add: Boolean): Boolean

    fun checkTheme(prefix: Boolean): CheckThemeResult
}

external fun blankIconSet(prefix: String): IconSet

/**
 * Sort theme keys, longest first. Modifies and returns the list.
 */
external fun sortThemeKeys(keys: Array<String>): Array<String>
