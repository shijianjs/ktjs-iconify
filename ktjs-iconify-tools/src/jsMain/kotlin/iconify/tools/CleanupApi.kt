@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

/**
 * Clean up an SVG before parsing or optimising it.
 */
external fun cleanupSVG(svg: SVG, options: CleanupSVGOptions? = definedExternally)

/**
 * Check for tags that should not be in an icon. Throws on failure.
 */
external fun checkBadTags(svg: SVG, options: CleanupSVGOptions? = definedExternally)

/**
 * Find all elements in an SVG and invoke the callback for each element.
 */
external fun parseSVG(svg: SVG, callback: (ParseSVGCallbackItem) -> Unit)

/**
 * Parse CSS in an SVG. Return `undefined` from the callback to remove a rule.
 */
external fun parseSVGStyle(svg: SVG, callback: (dynamic) -> dynamic)

/**
 * Find IDs, links and palette usage in an SVG.
 */
external fun analyseSVGStructure(
    svg: SVG,
    options: AnalyseSVGStructureOptions? = definedExternally,
): AnalyseSVGStructureResult

external fun removeBadAttributes(svg: SVG)

/**
 * Clean up the root `<style>`. Returns removed animations and at-rules.
 */
external fun cleanupRootStyle(svg: SVG): dynamic

external fun cleanupInlineStyle(svg: SVG)

external fun cleanupSVGRoot(svg: SVG)

external fun convertStyleToAttrs(svg: SVG)
