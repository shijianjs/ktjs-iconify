@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyIcon
import iconify.IconifyJSON

/**
 * Generate CSS for one icon, rendered as background or mask.
 */
external fun getIconCSS(
    icon: IconifyIcon,
    options: IconCSSIconOptions? = definedExternally,
): String

/**
 * Generate CSS for one icon, rendered as pseudo-element content.
 */
external fun getIconContentCSS(
    icon: IconifyIcon,
    options: IconContentIconOptions,
): String

/**
 * Generate CSS for icons from an icon set, rendered as background or mask.
 */
external fun getIconsCSS(
    iconSet: IconifyJSON,
    names: Array<String>,
    options: IconCSSIconSetOptions? = definedExternally,
): String

/**
 * Generate CSS for icons from an icon set, rendered as pseudo-element content.
 */
external fun getIconsContentCSS(
    iconSet: IconifyJSON,
    names: Array<String>,
    options: IconContentIconSetOptions,
): String
