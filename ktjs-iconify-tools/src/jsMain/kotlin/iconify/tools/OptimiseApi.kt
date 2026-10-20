@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

/**
 * Optimise an SVG with SVGO.
 */
external fun runSVGO(svg: SVG, options: SVGOptions? = definedExternally)

/**
 * Remove the clip path added by Figma and Penpot to icons with overflowing elements.
 *
 * Returns `true` if a clip path was removed.
 */
external fun removeFigmaClipPathFromSVG(svg: SVG): Boolean

/**
 * Expand compressed paths for software that does not support them.
 */
external fun deOptimisePaths(svg: SVG)

/**
 * Reset the SVG origin to `0 0`.
 */
external fun resetSVGOrigin(svg: SVG)

/**
 * Convert an SVG to a mask, fixing icons that use white where they should be transparent.
 */
external fun convertSVGToMask(svg: SVG, options: dynamic = definedExternally): Boolean

external fun scaleSVG(svg: SVG, scale: Double)

/**
 * Expand global styles, removing `@font-face`, `@media` and similar rules.
 */
external fun cleanupGlobalStyle(svg: SVG)
