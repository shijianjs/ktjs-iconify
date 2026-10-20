package iconify.utils

/** `"mask"` or `"background"`. */
typealias IconCSSMode = String

/** `"expanded"`, `"compact"` or `"compressed"`. */
typealias CSSFormatMode = String

external interface IconCSSSharedOptions {
    var varName: String?
    var forceSquare: Boolean?
    var color: String?

    /** Additional CSS rules, keyed by property name. */
    var rules: dynamic
}

external interface IconCSSIconSelectorOptions {
    var pseudoSelector: Boolean?
    var iconSelector: String?
}

external interface IconCSSSelectorOptions : IconCSSIconSelectorOptions {
    var commonSelector: String?
    var overrideSelector: String?
}

external interface IconCSSModeOptions {
    var mode: IconCSSMode?
}

external interface IconCSSFormatOptions {
    var format: CSSFormatMode?
}

external interface IconCSSIconOptions :
    IconCSSSharedOptions,
    IconCSSIconSelectorOptions,
    IconCSSModeOptions,
    IconCSSFormatOptions {
    var customise: ((String) -> String)?
}

external interface IconContentSharedOptions {
    var height: Double
    var width: Double?
    var color: String?
    var rules: dynamic
}

external interface IconContentIconSelectorOptions {
    var iconSelector: String?
}

external interface IconContentIconOptions :
    IconContentSharedOptions,
    IconContentIconSelectorOptions,
    IconCSSFormatOptions {
    var customise: ((String) -> String)?
}

external interface IconCSSIconSetOptions :
    IconCSSSharedOptions,
    IconCSSSelectorOptions,
    IconCSSModeOptions,
    IconCSSFormatOptions {
    var customise: ((String, String) -> String)?
}

external interface IconContentIconSetOptions :
    IconContentSharedOptions,
    IconContentIconSelectorOptions,
    IconCSSFormatOptions {
    var customise: ((String, String) -> String)?
}

external interface CSSUnformattedItem {
    var selector: dynamic
    var rules: dynamic
}
