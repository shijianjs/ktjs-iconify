@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

/**
 * Optimise an SVG with SVGO.
 *
 * Without [SVGOptions.plugins], uses the Iconify plugin set and detects animations and
 * filters to avoid plugins that would break them. `multipass` defaults to `true`.
 * IDs are rewritten with the `svgID` prefix by default; set `cleanupIDs` to a custom
 * prefix or `false` to disable it. [SVGOptions.keepShapes] keeps plugins that modify
 * shapes disabled.
 */
external fun runSVGO(svg: SVG, options: SVGOptions? = definedExternally)

/**
 * Remove the clip path added by Figma and Penpot to icons with overflowing elements.
 *
 * Also cleans up Penpot-specific junk. If nothing can be removed, the original SVG is
 * restored. Returns `true` if a clip path was removed.
 */
external fun removeFigmaClipPathFromSVG(svg: SVG): Boolean

/**
 * Expand compressed paths for software that does not support them.
 *
 * Paths are rewritten with explicit commands and argument separators. Invalid paths are
 * left unchanged.
 */
external fun deOptimisePaths(svg: SVG)

/**
 * Reset the SVG origin to `0 0`.
 *
 * Shifts content by the viewBox origin and rewrites the viewBox, then optimises the
 * result. Does nothing when the origin is already `0 0`.
 */
external fun resetSVGOrigin(svg: SVG)

/**
 * Convert an SVG to a mask, fixing icons that use white where they should be transparent.
 *
 * Solid colors become white, transparent colors become black, and the result is wrapped
 * in a `<mask>` with a filled `<rect>`. Defaults: `color` is `currentColor`, `solid` is
 * black/`currentcolor`, `transparent` is white, `id` is `"mask"` and `force` is `false`.
 * A `custom` callback can map colors to other values or opacity levels.
 *
 * Returns `true` when the SVG was converted; otherwise restores the original and returns
 * `false`.
 */
external fun convertSVGToMask(svg: SVG, options: dynamic = definedExternally): Boolean

/**
 * Scale an SVG by [scale], resetting the origin first and optimising the result.
 */
external fun scaleSVG(svg: SVG, scale: Double)

/**
 * Expand global styles, removing `@font-face`, `@media` and similar rules.
 *
 * Global CSS rules matching IDs, classes or tags are copied to matching elements as
 * presentation attributes, and classes used only by those rules are removed. Rules used
 * by animations and media queries are left intact. On failure the original SVG is
 * restored.
 */
external fun cleanupGlobalStyle(svg: SVG)
