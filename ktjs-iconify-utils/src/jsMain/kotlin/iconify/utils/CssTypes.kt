package iconify.utils

import iconify.StringOrStringArray
import js.objects.Record

/**
 * How icons are rendered in CSS: `"mask"` or `"background"`.
 */
typealias IconCSSMode = String

/**
 * CSS formatting: `"expanded"`, `"compact"` or `"compressed"`.
 */
typealias CSSFormatMode = String

/**
 * Options shared by CSS generation for one icon and for an icon set.
 */
external interface IconCSSSharedOptions {
    /**
     * CSS variable name used to store the icon data.
     *
     * Set to `null` to store the data in each rule instead. Defaults to `"svg"` in mask mode and
     * to no variable in background mode.
     */
    var varName: String?

    /**
     * Make each icon square by centering it in a square viewBox.
     */
    var forceSquare: Boolean?

    /**
     * Color for monotone icons. Icons with a set color use `"background"` mode.
     */
    var color: String?

    /**
     * Additional CSS rules, keyed by property name.
     */
    var rules: Record<String, String>?
}

/**
 * Selector options for CSS generation for one icon.
 */
external interface IconCSSIconSelectorOptions {
    /**
     * Treat [iconSelector] as a pseudo-selector, such as `::after`.
     */
    var pseudoSelector: Boolean?

    /**
     * Selector used for the icon.
     */
    var iconSelector: String?
}

/**
 * Selector options for CSS generation for an icon set.
 */
external interface IconCSSSelectorOptions : IconCSSIconSelectorOptions {
    /**
     * Selector for rules shared by all icons.
     *
     * Used only when set. Defaults to `.icon--{prefix}`.
     */
    var commonSelector: String?

    /**
     * Selector for rules that override the common rules. Supports `{name}` for the icon name.
     *
     * Used only when both [commonSelector] and [overrideSelector] are set.
     */
    var overrideSelector: String?
}

/**
 * Rendering mode.
 */
external interface IconCSSModeOptions {
    /**
     * Rendering mode. When omitted, it is detected from the icon content: `"mask"` for icons that
     * use `currentColor`, `"background"` otherwise.
     */
    var mode: IconCSSMode?
}

/**
 * CSS formatting options.
 */
external interface IconCSSFormatOptions {
    /**
     * Formatter.
     */
    var format: CSSFormatMode?
}

/**
 * Options for generating CSS for one icon as background or mask.
 */
external interface IconCSSIconOptions :
    IconCSSSharedOptions,
    IconCSSIconSelectorOptions,
    IconCSSModeOptions,
    IconCSSFormatOptions {
    /**
     * Customise the icon body before generating CSS.
     */
    var customise: ((String) -> String)?
}

/**
 * Options shared by content CSS generation for one icon and for an icon set.
 */
external interface IconContentSharedOptions {
    /**
     * Icon height.
     */
    var height: Double

    /**
     * Icon width. Defaults to the height adjusted by the icon's width/height ratio.
     */
    var width: Double?

    /**
     * Color for monotone icons.
     */
    var color: String?

    /**
     * Additional CSS rules, keyed by property name.
     */
    var rules: Record<String, String>?
}

/**
 * Selector options for content CSS generation for one icon.
 */
external interface IconContentIconSelectorOptions {
    /**
     * Selector used for the icon.
     */
    var iconSelector: String?
}

/**
 * Options for generating CSS for one icon as pseudo-element content.
 */
external interface IconContentIconOptions :
    IconContentSharedOptions,
    IconContentIconSelectorOptions,
    IconCSSFormatOptions {
    /**
     * Customise the icon body before generating CSS.
     */
    var customise: ((String) -> String)?
}

/**
 * Options for generating CSS for an icon set as background or mask.
 */
external interface IconCSSIconSetOptions :
    IconCSSSharedOptions,
    IconCSSSelectorOptions,
    IconCSSModeOptions,
    IconCSSFormatOptions {
    /**
     * Customise an icon body before generating CSS. Receives the body and the icon name.
     */
    var customise: ((String, String) -> String)?
}

/**
 * Options for generating CSS for an icon set as pseudo-element content.
 */
external interface IconContentIconSetOptions :
    IconContentSharedOptions,
    IconContentIconSelectorOptions,
    IconCSSFormatOptions {
    /**
     * Customise an icon body before generating CSS. Receives the body and the icon name.
     */
    var customise: ((String, String) -> String)?
}

/**
 * CSS rule set before formatting.
 */
external interface CSSUnformattedItem {
    /**
     * Selector, or a list of selectors.
     */
    var selector: StringOrStringArray

    /**
     * CSS rules, keyed by property name.
     */
    var rules: Record<String, String>
}
