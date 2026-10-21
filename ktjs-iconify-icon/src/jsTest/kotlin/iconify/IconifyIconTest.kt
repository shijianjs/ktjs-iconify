package iconify

import kotlin.js.js
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import web.dom.document
import web.html.HTMLUnknownElement

class IconifyIconTest {
    @Test
    fun registersCustomElement() {
        registerIconifyIcon()
        assertFalse(document.createElement("iconify-icon") is HTMLUnknownElement)
    }

    @Test
    fun createsElementFromTypedTagName() {
        registerIconifyIcon()
        val element = document.createElement(IconifyIconTagName.iconifyIcon)

        assertEquals("iconify-icon", element.tagName.lowercase())
    }

    @Test
    fun createsElement() {
        val element = createIconifyIcon("mdi:home") {
            width = "1em"
        }

        assertEquals("iconify-icon", element.tagName.lowercase())
        assertEquals("mdi:home", element.icon)
        assertEquals("1em", element.width)
    }

    @Test
    fun storesInlineIconData() {
        val data = js("{ body: '<path d=\"M0 0h24v24H0z\"/>', width: 24, height: 24 }")

        assertTrue(addIcon("test:home", data))
        assertTrue(iconLoaded("test:home"))
    }
}
