package iconify.tools

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IconifyToolsTest {
    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    @Test
    fun readsSvg() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M0 0h24v24H0z"/></svg>""",
        )

        assertEquals(24.0, svg.viewBox.width)
        assertTrue(svg.getBody().contains("<path"))
        assertTrue(svg.toSvgString().contains("viewBox=\"0 0 24 24\""))

        val icon = svg.getIcon()
        assertEquals(24.0, icon.width)
        assertEquals(24.0, icon.height)
    }

    @Test
    fun cleansUpAndParsesColors() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16"><path style="fill: #ff0000" d="M0 0h16v16H0z"/></svg>""",
        )

        cleanupSVG(svg)

        val result = parseColors(svg)
        assertTrue(result.colors.isNotEmpty())
    }

    @Test
    fun editsIconSet() {
        val iconSet = blankIconSet("test")
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M0 0h24v24H0z"/></svg>""",
        )

        assertTrue(iconSet.fromSVG("home", svg))
        assertEquals(1, iconSet.count())

        assertTrue(iconSet.setAlias("house", "home"))
        assertEquals(listOf("home"), iconSet.list().sorted())
        assertEquals(
            listOf("home", "house"),
            iconSet.list(arrayOf(IconSetIconTypes.icon, IconSetIconTypes.alias)).sorted(),
        )
        assertNotNull(iconSet.resolve("house"))

        val json = iconSet.export()
        assertEquals("test", json.prefix)
        assertEquals(1, keys(json.icons).size)
        assertEquals(1, keys(json.aliases).size)
    }
}
