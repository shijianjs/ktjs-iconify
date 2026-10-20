package iconify.utils

external interface IconifyLoaderOptions {
    /** Emit a warning when missing icons are matched. */
    var warn: String?

    /** Add `xmlns` and `xlink` namespaces when necessary. Defaults to `false`. */
    var addXmlNs: Boolean?

    /** Scale of icons against 1em. */
    var scale: Double?

    var defaultStyle: String?
    var defaultClass: String?

    /** Custom collections, keyed by collection name. */
    var customCollections: dynamic

    /** Icon customisations applied to all resolved icons. */
    var customizations: dynamic

    /** Auto-install icon packages when usage is detected. Node.js only. */
    var autoInstall: dynamic

    /** Additional icon properties applied to the SVG. */
    var usedProps: dynamic

    /** Working directory used to resolve `@iconify-json` packages. Node.js only. */
    var cwd: dynamic
}
