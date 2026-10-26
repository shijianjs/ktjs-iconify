@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

/**
 * Clean up an SVG before parsing or optimising it.
 *
 * Runs, in order: [cleanupInlineStyle], [convertStyleToAttrs], [cleanupSVGRoot],
 * [checkBadTags], [removeBadAttributes] and [cleanupRootStyle].
 *
 * Bad or unsupported elements make [checkBadTags] throw an exception. [options] only
 * contains [CleanupSVGOptions.keepTitles].
 */
external fun cleanupSVG(svg: SVG, options: CleanupSVGOptions? = definedExternally)

/**
 * Check for tags that should not be in an icon. Throws on failure.
 *
 * `title` elements are removed unless [CleanupSVGOptions.keepTitles] is `true`.
 * Unsupported tags and namespaced custom tags (such as Inkscape junk) are silently
 * removed, while bad or unknown elements, nested `<svg>` and invalid parent/child
 * combinations throw an error.
 */
external fun checkBadTags(svg: SVG, options: CleanupSVGOptions? = definedExternally)

/**
 * Find all elements in an SVG and invoke the callback for each element.
 *
 * [ParseSVGCallbackItem.parents] contains parents with the direct parent first and
 * the root `<svg>` last. Set [ParseSVGCallbackItem.testChildren] to `false` to skip
 * child elements, and [ParseSVGCallbackItem.removeNode] to `true` to remove the node.
 * Asynchronous callbacks are not supported and throw an error.
 */
external fun parseSVG(svg: SVG, callback: (ParseSVGCallbackItem) -> Unit)

/**
 * Parse CSS in an SVG. Return `undefined` from the callback to remove a rule.
 *
 * The callback is invoked for inline `style` attributes (`type` `"inline"`), rules
 * in `<style>` elements (`type` `"global"`), other at-rules (`type` `"at-rule"`) and
 * keyframes (`type` `"keyframes"`). Return a new string to change the value, or
 * `undefined` to delete the rule. Asynchronous callbacks are not supported.
 */
external fun parseSVGStyle(svg: SVG, callback: (ParseSVGStyleCallbackItem) -> String?)

/**
 * Find IDs, links and palette usage in an SVG.
 *
 * Run cleanup functions first so inline styles are converted to attributes. The result
 * contains maps of elements and IDs, a list of links from elements to IDs, and a tree
 * describing which elements are rendered as paint or as masks/clip paths.
 *
 * Duplicate IDs, definitions without an ID and missing links throw an error. With
 * [AnalyseSVGStructureOptions.fixErrors] they are removed or repaired with a warning
 * instead.
 */
external fun analyseSVGStructure(
    svg: SVG,
    options: AnalyseSVGStructureOptions? = definedExternally,
): AnalyseSVGStructureResult

/**
 * Remove useless attributes from all elements.
 *
 * Removes event handlers, junk and editor-specific attributes, namespaced attributes
 * (`xlink:href` becomes `href`), and all attributes on `<defs>` that are not
 * tag-specific.
 */
external fun removeBadAttributes(svg: SVG)

/**
 * Clean up the root `<style>`. Returns removed animations and at-rules.
 *
 * Keeps inline styles, global rules and keyframes; removes other at-rules such as
 * `@font-face` and `@media`. The returned object contains `animations` and
 * `removedAtRules` sets, each omitted when empty.
 */
external fun cleanupRootStyle(svg: SVG): CleanupRootStyleResult

/**
 * Expand inline `style` attributes.
 *
 * Rules that have a presentational attribute equivalent (such as `fill` or `stroke`)
 * are moved to attributes. Animation, transform and transition rules are kept in the
 * `style` attribute. Junk rules are dropped, and unexpected rules produce a warning.
 */
external fun cleanupInlineStyle(svg: SVG)

/**
 * Clean up the root `<svg>` element.
 *
 * Removes junk, namespace and editor-specific attributes, strips `px` from dimensions
 * and `class`. Presentational attributes that belong on child shapes (such as `fill`,
 * `stroke` and `opacity`) are moved to a wrapper `<g>`; `<style>`, `<title>`,
 * definitions and masks are not wrapped. Unknown attributes are removed with a warning.
 */
external fun cleanupSVGRoot(svg: SVG)

/**
 * Convert CSS rules to attributes using SVGO.
 *
 * Runs the SVGO `convertStyleToAttrs` and `inlineStyles` plugins. Does nothing when
 * the SVG contains no usable styles.
 */
external fun convertStyleToAttrs(svg: SVG)
