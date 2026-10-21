package iconify.utils

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColorsTest {
    private fun rgb(r: Double, g: Double, b: Double, alpha: Double = 1.0): RGBColor =
        js("({ type: 'rgb', r: r, g: g, b: b, alpha: alpha })").unsafeCast<RGBColor>()

    private fun hsl(h: Double, s: Double, l: Double, alpha: Double = 1.0): HSLColor =
        js("({ type: 'hsl', h: h, s: s, l: l, alpha: alpha })").unsafeCast<HSLColor>()

    private fun lab(l: Double, a: Double, b: Double, alpha: Double = 1.0): LABColor =
        js("({ type: 'lab', l: l, a: a, b: b, alpha: alpha })").unsafeCast<LABColor>()

    private fun lch(l: Double, c: Double, h: Double, alpha: Double = 1.0): LCHColor =
        js("({ type: 'lch', l: l, c: c, h: h, alpha: alpha })").unsafeCast<LCHColor>()

    private fun function(name: String, value: String): FunctionColor =
        js("({ type: 'function', func: name, value: value })").unsafeCast<FunctionColor>()

    private fun assertRgb(
        expected: RGBColor,
        actual: Color?,
        message: String = "",
    ) {
        val color = assertNotNull(actual, message)
        assertEquals("rgb", color.type, message)
        val value = color.unsafeCast<RGBColor>()
        assertEquals(expected.r, value.r, message)
        assertEquals(expected.g, value.g, message)
        assertEquals(expected.b, value.b, message)
        assertEquals(expected.alpha, value.alpha, message)
    }

    private fun assertType(type: String, actual: Color?, message: String = "") {
        val color = assertNotNull(actual, message)
        assertEquals(type, color.type, message)
    }

    @Test
    fun parsesKeywords() {
        assertRgb(rgb(255.0, 0.0, 0.0), stringToColor("red"))
        assertRgb(rgb(169.0, 169.0, 169.0), stringToColor("darkGray"))
        assertRgb(rgb(128.0, 0.0, 128.0), stringToColor("purple"))

        assertType("none", stringToColor("None"))
        assertType("transparent", stringToColor("transparent"))
        assertType("current", stringToColor("currentColor"))
        assertNull(stringToColor("inherit"))

        assertEquals("#f00", colorToString(rgb(255.0, 0.0, 0.0)))
        assertEquals("#a9a9a9", colorToString(rgb(169.0, 169.0, 169.0)))
        assertEquals("#800080", colorToString(rgb(128.0, 0.0, 128.0)))
    }

    @Test
    fun formatsKeywordsAndAlpha() {
        assertEquals("rgba(255, 0, 0, 0.5)", colorToString(rgb(255.0, 0.0, 0.0, 0.5)))
        assertEquals("#ff000080", colorToString(rgb(255.0, 0.0, 0.0, 0.5), canRound = true))
        assertEquals("#f004", colorToString(rgb(254.7, 0.4, 0.0, 68.0 / 255.0), canRound = true))

        assertEquals("none", colorToString(js("({ type: 'none' })").unsafeCast<NoColor>()))
        assertEquals(
            "currentColor",
            colorToString(js("({ type: 'current' })").unsafeCast<CurrentColor>()),
        )
        assertEquals(
            "transparent",
            colorToString(js("({ type: 'transparent' })").unsafeCast<TransparentColor>()),
        )
        assertEquals("transparent", colorToString(rgb(255.0, 255.0, 0.0, 0.0)))
        assertEquals("var(--foo)", colorToString(function("var", "--foo")))
    }

    @Test
    fun parsesHexadecimal() {
        assertNull(stringToColor("12"))

        assertRgb(rgb(17.0, 170.0, 51.0), stringToColor("1a3"))
        assertEquals("#1a3", colorToString(rgb(17.0, 170.0, 51.0)))
        assertNull(stringToColor("g12"))

        assertRgb(rgb(17.0, 170.0, 51.0), stringToColor("#1a3"))
        assertRgb(rgb(255.0, 17.0, 34.0, 51.0 / 255.0), stringToColor("F123"))
        assertNull(stringToColor("-123"))
        assertNull(stringToColor("#zx2"))

        assertNull(stringToColor("12345"))

        assertRgb(rgb(18.0, 52.0, 246.0), stringToColor("1234F6"))
        assertEquals("#1234f6", colorToString(rgb(18.0, 52.0, 246.0)))

        assertRgb(rgb(17.0, 170.0, 51.0), stringToColor("#11Aa33"))
        assertEquals("#1a3", colorToString(rgb(17.0, 170.0, 51.0)))
        assertNull(stringToColor("1234567"))

        assertRgb(rgb(165.0, 18.0, 52.0, 246.0 / 255.0), stringToColor("a51234f6"))
        assertEquals("rgba(165, 18, 52, 0.96)", colorToString(rgb(165.0, 18.0, 52.0, 0.96)))

        assertRgb(rgb(165.0, 18.0, 52.0, 246.0 / 255.0), stringToColor("#a51234f6"))
        assertNull(stringToColor("123456789"))

        assertNull(stringToColor("1234567890"))
    }

    @Test
    fun parsesRgb() {
        assertNull(stringToColor("rgb(0, 0, 0, 1"))
        assertNull(stringToColor("rgb(var(--foo), 0, 0, 1)"))
        assertNull(stringToColor("rgb(0%, 0, 0)"))
        assertNull(stringToColor("rgb(0%, 0, 0%)"))
        assertNull(stringToColor("rgb(0, 0%, 0%)"))
        assertNull(stringToColor("rgb(0, 0, 0%)"))

        assertRgb(rgb(10.0, 20.0, 30.0), stringToColor("rgb(10, 20, 30)"))
        assertRgb(rgb(10.0, 20.0, 30.0), stringToColor("rgb(10 20 30)"))
        assertEquals("#0a141e", colorToString(rgb(10.0, 20.0, 30.0)))

        assertRgb(rgb(12.5, 20.0, 30.0), stringToColor("rgba(12.5, 20, 30)"))
        assertEquals("rgb(12.5, 20, 30)", colorToString(rgb(12.5, 20.0, 30.0)))

        assertRgb(rgb(10.0, 25.0, 30.0, 0.5), stringToColor("rgb(10, 25, 30, .5)"))
        assertEquals("rgba(10, 25, 30, 0.5)", colorToString(rgb(10.0, 25.0, 30.0, 0.5)))

        assertRgb(rgb(255.0, 127.5, 51.0), stringToColor("rgba(100%, 50%, 20%)"))
        assertRgb(rgb(10.0, 20.0, 31.0, 0.5), stringToColor("rgba(10, 20, 31, 50%)"))
    }

    @Test
    fun parsesHsl() {
        assertNull(stringToColor("hsl(0, 0, 0, 1, 2)"))
        assertNull(stringToColor("hsl(0%, 0%, 0%, 1)"))
        assertNull(stringToColor("hsl(0%, 0, 0%, 1)"))
        assertNull(stringToColor("hsl(0%, 0%, 0)"))
        assertNull(stringToColor("hsl(0, 0%, 0)"))
        assertNull(stringToColor("hsl(0, 0, 0%)"))
        assertNull(stringToColor("hsl(0, 0, 0, 50%)"))

        val simple = assertNotNull(stringToColor("hsl(10, 20%, 30%)"))
        assertEquals("hsl", simple.type)
        assertEquals(10.0, simple.unsafeCast<HSLColor>().h)
        assertEquals(20.0, simple.unsafeCast<HSLColor>().s)
        assertEquals(30.0, simple.unsafeCast<HSLColor>().l)
        assertEquals(1.0, simple.unsafeCast<HSLColor>().alpha)

        val spaces = assertNotNull(stringToColor("hsl(11 22%   33%)"))
        assertEquals(11.0, spaces.unsafeCast<HSLColor>().h)
        assertEquals(22.0, spaces.unsafeCast<HSLColor>().s)
        assertEquals(33.0, spaces.unsafeCast<HSLColor>().l)

        assertEquals("hsl(10, 20%, 30%)", colorToString(hsl(10.0, 20.0, 30.0)))
        assertType("hsl", stringToColor("hsla(90, 20%, 30%)"))

        val weird = assertNotNull(stringToColor("HSLA ( 10 , 20% , 30% , .2 )"))
        assertEquals(10.0, weird.unsafeCast<HSLColor>().h)
        assertEquals(20.0, weird.unsafeCast<HSLColor>().s)
        assertEquals(30.0, weird.unsafeCast<HSLColor>().l)
        assertEquals(0.2, weird.unsafeCast<HSLColor>().alpha)
        assertEquals("hsla(10, 20%, 30%, 0.2)", colorToString(hsl(10.0, 20.0, 30.0, 0.2)))

        val floating = assertNotNull(stringToColor("hsl(10.4, 20.7%, 30.1%, 84.5%)"))
        assertEquals(10.4, floating.unsafeCast<HSLColor>().h)
        assertEquals(20.7, floating.unsafeCast<HSLColor>().s)
        assertEquals(30.1, floating.unsafeCast<HSLColor>().l)
        assertEquals(0.845, floating.unsafeCast<HSLColor>().alpha)
        assertEquals(
            "hsla(10.4, 20.7%, 30.1%, 0.845)",
            colorToString(hsl(10.4, 20.7, 30.1, 0.845)),
        )
    }

    @Test
    fun parsesLab() {
        assertNull(stringToColor("lab(10% 20 30 0.5)"))
        assertNull(stringToColor("lab(10%, 20%, 30%)"))
        assertNull(stringToColor("lab(10%, 20, 30%)"))
        assertNull(stringToColor("lab(10%, 20%, 30)"))
        assertNull(stringToColor("lab(10, 20, 30)"))

        val simple = assertNotNull(stringToColor("lab(10%, 20, 30)"))
        assertEquals("lab", simple.type)
        assertEquals(10.0, simple.unsafeCast<LABColor>().l)
        assertEquals(20.0, simple.unsafeCast<LABColor>().a)
        assertEquals(30.0, simple.unsafeCast<LABColor>().b)
        assertEquals(1.0, simple.unsafeCast<LABColor>().alpha)

        val slash = assertNotNull(stringToColor("lab(11% 22 33 / 1)"))
        assertEquals(11.0, slash.unsafeCast<LABColor>().l)
        assertEquals(22.0, slash.unsafeCast<LABColor>().a)
        assertEquals(33.0, slash.unsafeCast<LABColor>().b)

        assertEquals("lab(10% 20 30)", colorToString(lab(10.0, 20.0, 30.0)))

        val floating = assertNotNull(stringToColor("lab(10.4%, 20.7, 30.1 / 84.5%)"))
        assertEquals(10.4, floating.unsafeCast<LABColor>().l)
        assertEquals(20.7, floating.unsafeCast<LABColor>().a)
        assertEquals(30.1, floating.unsafeCast<LABColor>().b)
        assertEquals(0.845, floating.unsafeCast<LABColor>().alpha)
        assertEquals(
            "lab(10.4% 20.7 30.1 / 0.845)",
            colorToString(lab(10.4, 20.7, 30.1, 0.845)),
        )
    }

    @Test
    fun parsesLch() {
        assertNull(stringToColor("lch(10% 20 30 0.5)"))
        assertNull(stringToColor("lch(10%, 20%, 30%)"))
        assertNull(stringToColor("lch(10%, 20, 30%)"))
        assertNull(stringToColor("lch(10%, 20%, 30)"))
        assertNull(stringToColor("lch(10, 20, 30)"))

        val simple = assertNotNull(stringToColor("lch(10%, 20, 30)"))
        assertEquals("lch", simple.type)
        assertEquals(10.0, simple.unsafeCast<LCHColor>().l)
        assertEquals(20.0, simple.unsafeCast<LCHColor>().c)
        assertEquals(30.0, simple.unsafeCast<LCHColor>().h)
        assertEquals(1.0, simple.unsafeCast<LCHColor>().alpha)

        val slash = assertNotNull(stringToColor("lch(11% 22 33 / 1)"))
        assertEquals(11.0, slash.unsafeCast<LCHColor>().l)
        assertEquals(22.0, slash.unsafeCast<LCHColor>().c)
        assertEquals(33.0, slash.unsafeCast<LCHColor>().h)

        assertEquals("lch(10% 20 30)", colorToString(lch(10.0, 20.0, 30.0)))

        val floating = assertNotNull(stringToColor("lch(10.4%, 20.7, 30.1 / 84.5%)"))
        assertEquals(10.4, floating.unsafeCast<LCHColor>().l)
        assertEquals(20.7, floating.unsafeCast<LCHColor>().c)
        assertEquals(30.1, floating.unsafeCast<LCHColor>().h)
        assertEquals(0.845, floating.unsafeCast<LCHColor>().alpha)
        assertEquals(
            "lch(10.4% 20.7 30.1 / 0.845)",
            colorToString(lch(10.4, 20.7, 30.1, 0.845)),
        )
    }

    @Test
    fun parsesFunctions() {
        assertNull(stringToColor("var(--foo"))

        val variable = assertNotNull(stringToColor("var(--foo)"))
        assertEquals("function", variable.type)
        assertEquals("var", variable.unsafeCast<FunctionColor>().`func`)
        assertEquals("--foo", variable.unsafeCast<FunctionColor>().value)

        val url = assertNotNull(stringToColor("url(#a)"))
        assertEquals("url", url.unsafeCast<FunctionColor>().`func`)
        assertEquals("#a", url.unsafeCast<FunctionColor>().value)
    }

    @Test
    fun comparesColors() {
        assertTrue(compareColors(color("var(--foo)"), color("var(--foo)")))
        assertTrue(compareColors(color("rgb(0, 0, 0, 1)"), color("#000")))
        assertTrue(compareColors(color("rgb(0, 0, 0, 1)"), color("hsl(100, 0%, 0%)")))
        assertTrue(compareColors(color("rgb(0, 255, 0, 0)"), color("hsl(100, 0%, 0%, 0)")))
        assertTrue(compareColors(color("transparent"), color("hsl(80, 20%, 50%, 0)")))
        assertTrue(compareColors(color("transparent"), color("#f8a0")))
        assertEquals(false, compareColors(color("transparent"), color("hsl(100, 0%, 0%, .1)")))
    }

    private fun color(value: String): Color = assertNotNull(stringToColor(value), value)

    @Test
    fun convertsRgbToHex() {
        assertEquals("#f00", colorToHexString(rgb(255.0, 0.0, 0.0)))
        assertEquals("#ff0000", colorToHexString(rgb(255.0, 0.0, 0.0), canCompact = false))
        assertEquals(
            "#ff000080",
            colorToHexString(rgb(255.0, 0.0, 0.0, 0.5), canRound = true),
        )
        assertEquals(
            "#ff000080",
            colorToHexString(rgb(255.0, 0.0, 0.0, 0.5), canCompact = true, canRound = true),
        )
        assertNull(colorToHexString(rgb(12.5, 20.0, 30.0)))
        assertEquals("#0d141e", colorToHexString(rgb(12.5, 20.0, 30.0), canRound = true))
    }

    @Test
    fun providesColorKeywords() {
        val red = colorKeywords.unsafeCast<dynamic>().red
        assertEquals("rgb", red.type.unsafeCast<String>())
        assertEquals(255.0, red.r.unsafeCast<Double>())
        assertEquals(0.0, red.g.unsafeCast<Double>())
        assertEquals(0.0, red.b.unsafeCast<Double>())
        assertEquals(1.0, red.alpha.unsafeCast<Double>())

        val rebeccapurple = colorKeywords.unsafeCast<dynamic>().rebeccapurple
        assertEquals(102.0, rebeccapurple.r.unsafeCast<Double>())
        assertEquals(51.0, rebeccapurple.g.unsafeCast<Double>())
        assertEquals(153.0, rebeccapurple.b.unsafeCast<Double>())
    }
}
