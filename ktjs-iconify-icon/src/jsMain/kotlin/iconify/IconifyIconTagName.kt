package iconify

import web.dom.TagName

/**
 * Typed tag name of the `<iconify-icon>` custom element.
 */
object IconifyIconTagName {
    inline val iconifyIcon: TagName<IconifyIconElement>
        get() = TagName("iconify-icon")
}
