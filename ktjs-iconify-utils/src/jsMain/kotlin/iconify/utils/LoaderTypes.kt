package iconify.utils

/**
 * Options for the icon loader.
 */
external interface IconifyLoaderOptions {
    /**
     * Emit a warning when a missing icon is matched by name search.
     *
     * The string is used as the warning message; `undefined` disables warnings.
     */
    var warn: String?

    /**
     * Add `xmlns` and `xlink` namespaces when necessary.
     *
     * Defaults to `false`.
     */
    var addXmlNs: Boolean?

    /**
     * Scale of icons against `1em`.
     */
    var scale: Double?

    /**
     * Style applied to icons by default.
     *
     * Defaults to an empty string.
     */
    var defaultStyle: String?

    /**
     * Class names applied to icons by default.
     *
     * Defaults to an empty string.
     */
    var defaultClass: String?

    /**
     * Custom collections, keyed by collection name.
     *
     * A value can be a loader function, an inline collection object, or a function returning an
     * icon set.
     */
    var customCollections: dynamic

    /**
     * Customisations applied to all resolved icons.
     *
     * Supports `transform`, `customize`, `iconCustomizer`, `additionalProps` and `trimCustomSvg`.
     */
    var customizations: dynamic

    /**
     * Auto-install icon packages when their usage is detected.
     *
     * Can be a boolean or a function that installs the package. Node.js only; ignored in browsers.
     *
     * Defaults to `false`.
     */
    var autoInstall: dynamic

    /**
     * Additional properties applied to the SVG.
     *
     * `width` and `height` already present on the SVG are kept. Set this to an empty object to keep
     * the properties from the customisations instead.
     */
    var usedProps: dynamic

    /**
     * Working directory used to resolve `@iconify-json` packages.
     *
     * A string or an array of strings, where the first existing path is used. Node.js only.
     *
     * Defaults to the current working directory.
     */
    var cwd: dynamic
}
