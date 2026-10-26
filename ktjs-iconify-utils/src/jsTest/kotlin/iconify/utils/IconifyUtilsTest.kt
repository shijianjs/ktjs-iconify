package iconify.utils

import iconify.IconifyJSON
import kotlin.js.js
import js.objects.Record
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IconifyUtilsTest {
    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    private fun iconSet(): IconifyJSON = js(
        """({
            prefix: 'test',
            width: 24,
            height: 24,
            icons: { home: { body: '<path d="M0 0h24v24H0z"/>' } },
            aliases: { house: { parent: 'home', hFlip: true } }
        })""",
    ).unsafeCast<IconifyJSON>()

    @Test
    fun parsesIconNames() {
        val icon = stringToIcon("mdi:home")

        assertNotNull(icon)
        assertEquals("mdi", icon.prefix)
        assertEquals("home", icon.name)
        assertTrue(validateIconName(icon))
    }

    @Test
    fun resolvesAliases() {
        val icon = getIconData(iconSet(), "house")

        assertNotNull(icon)
        assertEquals("<path d=\"M0 0h24v24H0z\"/>", icon.body)
        assertEquals(true, icon.hFlip)
        assertEquals(24.0, icon.width)
    }

    @Test
    fun extractsIcons() {
        val subset = getIcons(iconSet(), arrayOf("home", "house"))

        assertNotNull(subset)
        assertEquals("test", subset.prefix)
        assertEquals(1, keys(subset.icons).size)
    }

    @Test
    fun buildsSvg() {
        val icon = getIconData(iconSet(), "home")
        assertNotNull(icon)

        val build = iconToSVG(icon, js("({ width: 48, height: 48 })"))
        assertEquals("0 0 24 24", build.attributes.viewBox)
        assertEquals("48", build.attributes.width)

        val svg = iconToHTML(build.body, build.attributes.unsafeCast<Record<String, String>>())
        assertTrue(svg.startsWith("<svg"))
        assertTrue(svg.contains("viewBox=\"0 0 24 24\""))
    }

    @Test
    fun generatesCss() {
        val icon = getIconData(iconSet(), "home")
        assertNotNull(icon)

        val maskCss = getIconCSS(icon, js("({ mode: 'mask' })"))
        assertTrue(maskCss.contains("mask-image"))

        val setCss = getIconsCSS(iconSet(), arrayOf("home", "house"), js("({ mode: 'background' })"))
        assertTrue(setCss.contains(".icon--test--home"))
        assertTrue(setCss.contains("data:image/svg+xml"))
    }

    @Test
    fun convertsColors() {
        val color = stringToColor("#ff0000")

        assertNotNull(color)
        assertEquals("rgb", color.type)
        assertEquals("#f00", colorToString(color))
    }
}
