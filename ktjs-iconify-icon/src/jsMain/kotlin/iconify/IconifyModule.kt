package iconify

/**
 * The value exported as `IconifyIconComponent`: the registered custom element constructor, or the
 * exported function set when custom elements are unavailable.
 *
 * Upstream types it as `IconifyExportedFunctions`, but the runtime value is a constructor in
 * browsers, which an `external interface` cannot describe without also promising the exported
 * functions. Treat it as opaque and use [registerIconifyIcon] for the registration side effect;
 * the typed top-level functions in this package cover the same storage and API capabilities.
 */
external interface IconifyIconComponent

/**
 * The named exports of the `iconify-icon` package. Importing the module registers the
 * `<iconify-icon>` custom element.
 *
 * The module is an object with a named property rather than a top-level `external val` with
 * `@JsName`: under ES modules the latter compiles to a default import, and `iconify-icon` only
 * has named exports.
 */
@JsModule("iconify-icon")
external object IconifyModule {
    /**
     * The registered custom element class, or the exported function set when custom elements are
     * unavailable.
     *
     * Use [registerIconifyIcon] instead of referencing this directly.
     */
    val IconifyIconComponent: IconifyIconComponent
}
