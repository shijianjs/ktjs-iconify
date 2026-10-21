package iconify

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
    val IconifyIconComponent: dynamic
}
