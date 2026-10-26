package iconify.tools

import iconify.IconifyJSON
import iconify.utils.stringToColor
import iconify.utils.trimSVG
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColorsTest {
    private val emptyPath =
        """<path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/>"""

    private fun assertColors(expected: Array<String>, actual: Array<ColorOrString>) {
        assertEquals(expected.size, actual.size)
        expected.forEachIndexed { index, value ->
            assertEquals(jsStringify(stringToColor(value)), jsStringify(actual[index]))
        }
    }

    private fun parseOptions(source: String): ParseColorsOptions =
        jsObject(source).unsafeCast<ParseColorsOptions>()

    @Test
    fun findsNothingWithoutColors() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24">$emptyPath</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(svg)
        assertEquals(0, searchResult.colors.size)
        assertTrue(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
        assertEquals(svgCode, svg.toSvgString())

        val replaceResult = parseColors(svg, parseOptions("{defaultColor: 'currentColor'}"))
        assertEquals("""[{"type":"current"}]""", jsStringify(replaceResult.colors))
        assertFalse(replaceResult.hasUnsetColor)
        assertFalse(replaceResult.hasGlobalStyle)

        assertEquals(
            """<svg viewBox="0 0 24 24" width="24" height="24"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z" fill="currentColor"/></svg>""",
            svg.toSvgString(),
        )
    }

    @Test
    fun findsColorsOnSvgElement() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24" fill="black">$emptyPath</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(svg)
        assertEquals("""[{"type":"rgb","r":0,"g":0,"b":0,"alpha":1}]""", jsStringify(searchResult.colors))
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
        assertEquals(svgCode, svg.toSvgString())

        val options = parseOptions("{}")
        options.callback = { _, _, color, _, _ ->
            assertEquals(
                """{"type":"rgb","r":0,"g":0,"b":0,"alpha":1}""",
                jsStringify(color),
            )
            ColorOrString("white")
        }
        val replaceResult = parseColors(svg, options)
        assertEquals("""[{"type":"rgb","r":255,"g":255,"b":255,"alpha":1}]""", jsStringify(replaceResult.colors))
        assertFalse(replaceResult.hasUnsetColor)
        assertFalse(replaceResult.hasGlobalStyle)
        assertTrue(svg.toSvgString() != svgCode)
    }

    @Test
    fun parsesKeywords() {
        val svgCode = """<svg xmlns="http://www.w3.org/2000/svg" width="10" height="10">
	<g stroke="black">
		<path d="" stroke="inherit" fill="none" />
		<path d="" stroke="transparent" fill="red" />
		<path d="" stroke="hsla(120, 50%, 50%, .5)" fill="currentColor" />
	</g>
</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(svg)
        assertColors(
            arrayOf(
                "#000",
                "none",
                "#f00",
                "transparent",
                "currentColor",
                "hsla(120,50%,50%,.5)",
            ),
            searchResult.colors,
        )
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
    }

    @Test
    fun parsesAnimations() {
        val svgCode = """<svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg" width="10" height="10">
		<rect width="10" height="10">
		  <animate attributeName="fill" values="red;blue;green;red" dur="3s" repeatCount="indefinite"/>
		</rect>
		</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(svg)
        assertColors(arrayOf("red", "blue", "green"), searchResult.colors)
        // Rectangle does not actually have a color, even though the animation sets one
        assertTrue(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)

        assertEquals(trimSVG(svgCode), svg.toMinifiedString())
    }

    @Test
    fun parsesNone() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z" fill="none"/></svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(svg)
        assertEquals("""[{"type":"none"}]""", jsStringify(searchResult.colors))
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
    }

    @Test
    fun parsesFunctionColor() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z" fill="var(--foo)"/></svg>"""
        val svg = SVG(svgCode)

        val options = parseOptions("{}")
        options.callback = { attr, colorStr, color, _, _ ->
            assertEquals(ColorAttributes.fill, attr)
            assertEquals("var(--foo)", colorStr)
            assertEquals("""{"type":"function","func":"var","value":"--foo"}""", jsStringify(color))
            ColorOrString("var(--bar)")
        }

        val searchResult = parseColors(svg, options)
        assertEquals(
            """[{"type":"function","func":"var","value":"--bar"}]""",
            jsStringify(searchResult.colors),
        )
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
    }

    @Test
    fun removesEmptyColor() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z" fill="" stroke="inherit"/></svg>"""
        val svg = SVG(svgCode)

        val options = parseOptions("{}")
        options.callback = { attr, _, _, _, _ ->
            error("Unexpected callback call for \"$attr\"")
        }

        val searchResult = parseColors(svg, options)
        assertEquals(0, searchResult.colors.size)
        assertTrue(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)

        // Empty colors should have been removed
        assertEquals(
            """<svg viewBox="0 0 24 24" width="24" height="24"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/></svg>""",
            svg.toSvgString(),
        )
    }

    @Test
    fun parsesGradient() {
        val svgCode = """<svg width="256px" height="256px" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" preserveAspectRatio="xMidYMid">
			<defs>
				<radialGradient cx="1.55750275e-05%" cy="99.999994%" fx="1.55750275e-05%" fy="99.999994%" r="120.115697%" gradientTransform="translate(0.000000,1.000000),scale(0.832531,1.000000),scale(1.000000,0.832531),translate(-0.000000,-1.000000)" id="radialGradient-1">
					<stop stop-color="#00C812" offset="0%"></stop>
					<stop stop-color="#006E00" offset="100%"></stop>
				</radialGradient>
			</defs>
			<path d="M179.2,230.4 L256,230.4 L133.12,128 C131.433,165.193 147.807,201.58 179.2,230.4" fill="url(#radialGradient-1)"></path>
		</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(
            svg,
            parseOptions("{defaultColor: () => { throw new Error('Unexpected callback call for defaultColor'); }}"),
        )
        assertColors(arrayOf("#00C812", "#006E00"), searchResult.colors)
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
    }

    @Test
    fun parsesMaskThatUsesPath() {
        val svgCode = """<svg width="256px" height="256px" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid">
			<defs>
				<path d="M2.27464661e-14,0 L254.693878,3.04336596e-14 L254.693878,160.344259 C255.3267,161.198982 255.762422,162.157626 256,163.39634 L256,168.36419 C255.762422,169.608049 255.3267,170.691008 254.693878,171.604678 L254.693878,256 L0,256 L0,192 L0,64 L2.27464661e-14,0 Z" id="path-1"></path>
				<radialGradient cx="16.6089694%" cy="17.3718345%" fx="16.6089694%" fy="17.3718345%" r="118.520308%" id="radialGradient-3">
					<stop stop-color="#88CDE7" offset="0%"></stop>
					<stop stop-color="#2274AD" offset="100%"></stop>
				</radialGradient>
			</defs>
			<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
				<mask id="mask-2" fill="white">
					<use href="#path-1"></use>
				</mask>
				<polygon fill="url(#radialGradient-3)" mask="url(#mask-2)" points="0 256 256 256 256 0 0 0"></polygon>
			</g>
		</svg>"""
        val svg = SVG(svgCode)

        val searchResult = parseColors(
            svg,
            parseOptions("{defaultColor: () => { throw new Error('Unexpected callback call for defaultColor'); }}"),
        )
        assertColors(arrayOf("none", "#88CDE7", "#2274AD"), searchResult.colors)
        assertFalse(searchResult.hasUnsetColor)
        assertFalse(searchResult.hasGlobalStyle)
    }

    @Test
    fun rejectsAsyncCallback() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16"><path fill="red" d="M0 0h16v16H0z"/></svg>"""
        )

        val options = parseOptions("{}")
        options.callback = { _, _, _, _, _ ->
            js("Promise.resolve('remove')") as ColorOrString
        }

        assertFails { parseColors(svg, options) }
    }

    @Test
    fun validatesIconWithoutColors() {
        val svgCode =
            """<svg viewBox="0 0 24 24" width="24" height="24">$emptyPath</svg>"""

        var result = validateColors(SVG(svgCode), true)
        assertEquals(0, result.colors.size)
        assertTrue(result.hasUnsetColor)
        assertFalse(result.hasGlobalStyle)

        // currentColor should not have been found either, but hasUnsetColor is set
        result = validateColors(SVG(svgCode), false)
        assertEquals(0, result.colors.size)
        assertTrue(result.hasUnsetColor)
        assertFalse(result.hasGlobalStyle)
    }

    @Test
    fun validatesMonotoneIcon() {
        val svg = SVG(
            """<svg viewBox="0 0 24 24" width="24" height="24">$emptyPath</svg>"""
        )

        val result = validateColors(svg, true, parseOptions("{defaultColor: 'currentColor'}"))
        assertEquals("""[{"type":"current"}]""", jsStringify(result.colors))
        assertFalse(result.hasUnsetColor)
        assertFalse(result.hasGlobalStyle)
    }

    @Test
    fun validatesIconWithPalette() {
        val svgCode =
            """<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" aria-hidden="true" role="img" width="1em" height="1em" preserveAspectRatio="xMidYMid meet" viewBox="0 0 128 128"><path fill="#fff" d="M4 4h120v120H4z"/><g fill="none" stroke="#b0bec5" stroke-width="1.993" stroke-miterlimit="10"><path d="M24.7 4.2v119.6"/><path d="M44.35 4.2v119.6"/><path d="M64 4.2v119.6"/><path d="M83.65 4.2v119.6"/><path d="M103.3 4.2v119.6"/></g><g fill="none" stroke="#b0bec5" stroke-width="2" stroke-miterlimit="10"><path d="M123.8 24.66H4.15"/><path d="M123.81 44.33H4.16"/><path d="M123.83 64H4.17"/><path d="M123.84 83.67H4.19"/><path d="M123.85 103.34H4.2"/></g><path fill="#0b88cf" d="M5.1 5.14l-.08 6.17l26.92 48.51l23.7-21.91l63.78 85.1h3.49l.14-5.16l-66.6-88.86l-23.02 21.28L8.45 5.11z"/><path d="M122.01 5.99V122H5.99V5.99h116.02M124 4H4v120h120V4z" fill="#b0bec5"/></svg>"""
        val svg = SVG(svgCode)

        val result = validateColors(svg, false)
        assertColors(arrayOf("#fff", "none", "#b0bec5", "#0b88cf"), result.colors)
        assertFalse(result.hasUnsetColor)
        assertFalse(result.hasGlobalStyle)

        assertFails { validateColors(svg, true) }
    }

    @Test
    fun validatesMixedIcon() {
        val svgCode =
            """<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" aria-hidden="true" role="img" width="1em" height="1em" preserveAspectRatio="xMidYMid meet" viewBox="0 0 24 24"><g fill="none"><path d="M15.052 14.32h.813L21 19.468L19.467 21l-5.146-5.136v-.813l-.278-.288A6.66 6.66 0 0 1 9.69 16.38a6.69 6.69 0 1 1 6.69-6.69a6.66 6.66 0 0 1-1.617 4.354l.289.278z" fill="currentColor"/><path d="M10.204 7.117H9.175v2.058H7.117v1.03h2.058v2.057h1.03v-2.058h2.057V9.175h-2.058V7.117z" fill="#fff"/></g></svg>"""
        val svg = SVG(svgCode)

        // White color makes it a palette icon
        assertFails { validateColors(svg, true) }

        // currentColor makes it monotone
        assertFails { validateColors(svg, false) }
    }

    private fun createIconSet(icons: String): IconSet =
        IconSet(jsObject("{prefix: 'foo', icons: {$icons}}").unsafeCast<IconifyJSON>())

    @Test
    fun detectsEmptyIconSet() {
        assertNull(detectIconSetPalette(createIconSet("")))
    }

    @Test
    fun detectsPaletteIconSet() {
        val iconSet = createIconSet(
            """foo: {body: '<g fill="red" />'}, bar: {body: '<g fill="green" />'}"""
        )
        assertEquals(true, detectIconSetPalette(iconSet))
    }

    @Test
    fun detectsMonotoneIconSet() {
        val iconSet = createIconSet(
            """foo: {body: '<g fill="currentColor" />'}, bar: {body: '<g fill="currentcolor" />'}"""
        )
        assertEquals(false, detectIconSetPalette(iconSet))
    }

    @Test
    fun detectsMixedIconSet() {
        val iconSet = createIconSet(
            """foo: {body: '<g fill="red" />'}, bar: {body: '<g fill="currentColor" />'}"""
        )
        assertNull(detectIconSetPalette(iconSet))
    }

    @Test
    fun treatsIconSetWithoutColorsAsMixed() {
        val iconSet = createIconSet("""foo: {body: '<g />'}""")
        assertNull(detectIconSetPalette(iconSet))
    }
}
