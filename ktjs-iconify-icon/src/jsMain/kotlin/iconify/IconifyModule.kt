package iconify

@JsModule("iconify-icon")
private external object IconifyIconModule {
    @JsName("IconifyIconComponent")
    val component: dynamic
}

/**
 * The `IconifyIconComponent` export: the registered custom element class, or the exported
 * function set when custom elements are unavailable.
 *
 * Use [registerIconifyIcon] instead of referencing this directly.
 */
val iconifyIconComponent: dynamic
    get() = IconifyIconModule.component
