package iconify.tools

import iconify.utils.IconifyIconCustomisations
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SvgTest {
    private val simpleSvg =
        """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40"><path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/></svg>"""

    private val simplePath = """<path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/>"""

    @Test
    fun loadsSimpleSvg() {
        val svg = SVG(simpleSvg)

        assertEquals(-8.0, svg.viewBox.left)
        assertEquals(-16.0, svg.viewBox.top)
        assertEquals(24.0, svg.viewBox.width)
        assertEquals(40.0, svg.viewBox.height)

        assertEquals(simpleSvg, svg.toSvgString())
        assertEquals(simpleSvg, svg.toMinifiedString())
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40">
	<path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z" />
</svg>
""",
            svg.toPrettyString(),
        )
        assertEquals(simplePath, svg.getBody())
    }

    @Test
    fun appliesCustomisations() {
        val svg = SVG(simpleSvg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="0.6em" height="1em" viewBox="-8 -16 24 40">$simplePath</svg>""",
            svg.toMinifiedString(jsObject("{}").unsafeCast<IconifyIconCustomisations>()),
        )

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="40" viewBox="-8 -16 24 40">$simplePath</svg>""",
            svg.toMinifiedString(jsObject("{height: 'auto'}").unsafeCast<IconifyIconCustomisations>()),
        )

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 40"><g transform="translate(16 16) scale(-1 1)">$simplePath</g></svg>""",
            svg.toMinifiedString(
                jsObject("{width: '1em', height: '1em', hFlip: true}")
                    .unsafeCast<IconifyIconCustomisations>(),
            ),
        )
    }

    @Test
    fun rejectsMissingDimensions() {
        assertFails {
            SVG("""<svg xmlns="http://www.w3.org/2000/svg">$simplePath</svg>""")
        }
    }

    @Test
    fun rejectsEmptyDocument() {
        assertFails { SVG("") }
    }

    @Test
    fun printsBuggedSvg() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3h7v18h-14v-18h7Z"/><path d="M14.5 3.5v3h-5v-3"/><path d="M9 13l2 2l4 -4" stroke-dasharray="10" stroke-dashoffset="10"><animate attributeName="stroke-dashoffset" values="10;0" dur="0.2s" fill="freeze"/></path></g><path d="M6 4H10V6H14V4H18V20H6V4Z" fill="currentColor" fill-opacity="0.3"/></svg>"""
        )

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
	<g fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
		<path d="M12 3h7v18h-14v-18h7Z" />
		<path d="M14.5 3.5v3h-5v-3" />
		<path d="M9 13l2 2l4 -4" stroke-dasharray="10" stroke-dashoffset="10">
			<animate attributeName="stroke-dashoffset" values="10;0" dur="0.2s" fill="freeze" />
		</path>
	</g>
	<path d="M6 4H10V6H14V4H18V20H6V4Z" fill="currentColor" fill-opacity="0.3" />
</svg>
""",
            svg.toSvgString(
                jsObject("{height: 'auto'}").unsafeCast<IconifyIconCustomisations>(),
                true,
            ),
        )
    }

    @Test
    fun handlesSizeInPixels() {
        val svg = SVG(
            """<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"	width="250px" height="250px">
<rect fill="#00A0DF" width="250" height="250"/>
</svg>
"""
        )

        assertEquals(0.0, svg.viewBox.left)
        assertEquals(0.0, svg.viewBox.top)
        assertEquals(250.0, svg.viewBox.width)
        assertEquals(250.0, svg.viewBox.height)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="250" height="250" viewBox="0 0 250 250">
	<rect fill="#00A0DF" width="250" height="250" />
</svg>
""",
            svg.toSvgString(
                jsObject("{height: 'auto'}").unsafeCast<IconifyIconCustomisations>(),
                true,
            ),
        )
    }

    @Test
    fun loadsAfterModification() {
        val svg = SVG(simpleSvg)
        svg.load(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 10 10"><rect width="10" height="10" fill="red"/></svg>"""
        )

        assertEquals(10.0, svg.viewBox.width)
        assertEquals("""<rect width="10" height="10" fill="red"/>""", svg.getBody())
    }

    @Test
    fun returnsIconData() {
        val icon = SVG(simpleSvg).getIcon()

        assertEquals(24.0, icon.width)
        assertEquals(40.0, icon.height)
        assertEquals(-8.0, icon.left)
        assertEquals(-16.0, icon.top)
        assertEquals(simplePath, icon.body)
    }
}
