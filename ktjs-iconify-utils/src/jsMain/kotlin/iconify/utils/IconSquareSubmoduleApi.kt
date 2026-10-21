@file:JsModule("@iconify/utils/lib/icon/square")

package iconify.utils

/**
 * Center a viewBox in a square one, keeping the original size.
 *
 * Bound to the `@iconify/utils/lib/icon/square` submodule, which is the only place this function is
 * exported from.
 *
 * Returns [viewBox] unchanged if it is already square.
 */
external fun makeViewBoxSquare(viewBox: SVGViewBox): SVGViewBox
