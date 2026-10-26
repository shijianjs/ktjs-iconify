package iconify.tools

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ParseStyleTest {
    @Test
    fun parsesGlobalStyle() {
        val svg = SVG(ARROW_SVG)

        parseSVGStyle(svg) { rawItem ->
            assertEquals(ParseSVGStyleCallbackItemTypes.global, rawItem.type)
            val item = rawItem.unsafeCast<ParseSVGStyleCallbackItemGlobal>()

            when (item.prop) {
                "fill" -> {
                    when (item.selectors.joinToString(" ")) {
                        ".cls-1" -> {
                            assertEquals("#fff", item.value)
                            "black"
                        }
                        ".cls-2" -> {
                            assertEquals("#231f20", item.value)
                            js("undefined") as String?
                        }
                        else -> error("Unexpected selectors")
                    }
                }
                "opacity" -> {
                    assertEquals("0", item.value)
                    assertEquals(listOf(".cls-1"), item.selectors.toList())

                    val selectorToken = item.selectorTokens[0].unsafeCast<CSSSelectorToken>()
                    if (selectorToken.type != CSSTokenTypes.selector) {
                        error("Bad selector token")
                    }
                    selectorToken.selectors = arrayOf(".whatever")
                    item.token.prop = "fill-opacity"
                    item.value
                }
                else -> error("Unexpected property: ${item.prop}")
            }
        }

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><defs><style>.whatever {fill: black;fill-opacity: 0;}</style></defs><title>arrow-circle-right</title><g id="Layer_2" data-name="Layer 2"><g id="arrow-circle-right"><g id="arrow-circle-right-2" data-name="arrow-circle-right"><rect class="cls-1" width="24" height="24" transform="translate(0 24) rotate(-90)"/><path class="cls-2" d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z"/></g></g></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun parsesAnimationInGlobalStyle() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
<style>
	.spin-path {
		animation: 0.75s linear infinite rotate;
		transform-origin: center;
	}
	@keyframes rotate {
		from {
			transform: rotate(0deg)
		}
		to {
			transform: rotate(360deg)
		}
	}
</style>
<path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/>
<path class="spin-path" d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z" />
</svg>"""
        )

        parseSVGStyle(svg) { item ->
            when (item.type) {
                ParseSVGStyleCallbackItemTypes.global -> {
                    if (item.prop == "animation" && item.value == "0.75s linear infinite rotate") {
                        "2s linear infinite spin"
                    } else {
                        item.value
                    }
                }
                ParseSVGStyleCallbackItemTypes.keyframes -> {
                    val keyframes =
                        item.unsafeCast<ParseSVGStyleCallbackItemGlobalKeyframesAtRule>()
                    assertEquals("""{"transform":"rotate(0deg)"}""", jsStringify(keyframes.from))
                    assertEquals("rotate", item.value)
                    "spin"
                }
                else -> error("Unexpected type: ${item.type}")
            }
        }

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><style>.spin-path {animation: 2s linear infinite spin;transform-origin: center;}@keyframes spin {from {transform: rotate(0deg);}to {transform: rotate(360deg);}}</style><path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/><path class="spin-path" d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z"/></svg>""",
            svg.toSvgString(),
        )
    }

    @Test
    fun rejectsAsyncCallback() {
        val svg = SVG(ARROW_SVG)

        assertFails {
            parseSVGStyle(svg) {
                js("Promise.resolve('remove')")
            }
        }
    }

    @Test
    fun parsesInlineStyle() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><rect width="24" height="24" style="fill: red; stroke: blue"/></svg>"""
        )
        var inlineRules = 0

        parseSVGStyle(svg) { item ->
            assertEquals(ParseSVGStyleCallbackItemTypes.inline, item.type)
            inlineRules++
            item.value
        }

        assertEquals(2, inlineRules)
    }
}
