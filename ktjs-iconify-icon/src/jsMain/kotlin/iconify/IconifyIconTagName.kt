package iconify

import web.dom.TagName

/**
 * Typed tag name of the `<iconify-icon>` custom element.
 */
object IconifyIconTagName {
    /** Tag name for `document.createElement`. */
    inline val iconifyIcon: TagName<IconifyIconElement>
        get() = TagName("iconify-icon")
}
