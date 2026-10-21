package iconify.tools

import iconify.utils.IconifyIconCustomisations
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class CleanupTest {
    private val simplePath = """<path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/>"""

    @Test
    fun movesRootFillToContent() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40" fill="red">$simplePath</svg>"""
        )
        cleanupSVGRoot(svg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40"><g fill="red">$simplePath</g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun cleansUpRootWithStyle() {
        val svg = SVG(
            """<svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg" fill-opacity="0.5">
			<style>
			rect { cursor: pointer }
			.round { rx: 5px; fill: green; }
			</style>
		
			<rect id="me" width="10" height="10">
			<set attributeName="class" to="round" begin="me.click" dur="2s" />
			</rect>
			<rect width="5" height="5" />
		</svg>"""
        )
        cleanupSVGRoot(svg)

        assertEquals(
            """<svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg" width="10" height="10"><style>rect { cursor: pointer }.round { rx: 5px; fill: green; }</style><g fill-opacity="0.5"><rect id="me" width="10" height="10"><set attributeName="class" to="round" begin="me.click" dur="2s"/></rect><rect width="5" height="5"/></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun cleansUpRootWithKeyframes() {
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
        cleanupSVGRoot(svg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><style>.spin-path {animation: 0.75s linear infinite rotate;transform-origin: center;}@keyframes rotate {from {transform: rotate(0deg) }to {transform: rotate(360deg) }}</style><path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/><path class="spin-path" d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun movesFillToContent() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40" fill="red">$simplePath</svg>"""
        )
        cleanupSVG(svg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40"><g fill="red">$simplePath</g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun removesJunk() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" enable-background="new 0 0 24 24" id="irrelevant"><defs><style>.cls-1{fill:#fff;opacity:0;cursor:pointer;}.cls-2{fill:#231f20;}</style></defs><title>arrow-circle-right</title><g id="Layer_2" data-name="Layer 2"><g id="arrow-circle-right"><g id="arrow-circle-right-2" data-name="arrow-circle-right"><rect class="cls-1" width="24" height="24" transform="translate(0 24) rotate(-90)"/><path class="cls-2" d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z"/></g></g></g></svg>"""
        )
        cleanupSVG(svg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><g id="Layer_2"><g id="arrow-circle-right"><g id="arrow-circle-right-2"><rect width="24" height="24" transform="translate(0 24) rotate(-90)" fill="#fff" opacity="0"/><path d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z" fill="#231f20"/></g></g></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun keepsTitle() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" enable-background="new 0 0 24 24" id="irrelevant"><defs><style>.cls-1{fill:#fff;opacity:0;cursor:pointer;}.cls-2{fill:#231f20;}</style></defs><title>arrow-circle-right</title><g id="Layer_2" data-name="Layer 2"><g id="arrow-circle-right"><g id="arrow-circle-right-2" data-name="arrow-circle-right"><rect class="cls-1" width="24" height="24" transform="translate(0 24) rotate(-90)"/><path class="cls-2" d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z"/></g></g></g></svg>"""
        )
        cleanupSVG(svg, jsObject("{keepTitles: true}").unsafeCast<CleanupSVGOptions>())

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><title>arrow-circle-right</title><g id="Layer_2"><g id="arrow-circle-right"><g id="arrow-circle-right-2"><rect width="24" height="24" transform="translate(0 24) rotate(-90)" fill="#fff" opacity="0"/><path d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z" fill="#231f20"/></g></g></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun readsAttributesFromDefs() {
        val svg = SVG(
            """<svg width="256" height="256" viewBox="0 0 256 256">
		<defs fill="green" opacity="0" style="display: none">
			<symbol id="def1" fill="purple">
				<rect x="0" y="0" width="64" height="64" id="def2" />
			</symbol>
		</defs>
		<use href="#def1" fill="red" />
		<use href="#def2" transform="translate(32 32)" />
		<use href="#def2" fill="teal" transform="translate(64 64)" />
	</svg>"""
        )

        val messages = captureWarnings {
            cleanupSVG(svg)
        }

        assertEquals(
            """<svg width="256" height="256" viewBox="0 0 256 256"><defs><symbol id="def1" fill="purple"><rect x="0" y="0" width="64" height="64" id="def2"/></symbol></defs><use href="#def1" fill="red"/><use href="#def2" transform="translate(32 32)"/><use href="#def2" fill="teal" transform="translate(64 64)"/></svg>""",
            svg.toMinifiedString(),
        )
        assertTrue(messages.isNotEmpty())
    }

    @Test
    fun keepsFilter() {
        val content = """<svg xmlns="http://www.w3.org/2000/svg" width="320" height="320" viewBox="0 0 32 32">
		<g fill="none" filter="url(#filter0_iii_18_1526)">
			<path fill="#4686EC" d="m14.035 3.552l4.264 7.284l-6.016 1.71C8.128 11.173 5.681 8.53 4.205 5.814c-.622-1.146-.617-3.25 1.856-3.25h6.248a2 2 0 0 1 1.726.989Z" />
		</g>
		<defs>
			<filter id="filter0_iii_18_1526" width="14.889" height="10.434" x="3.61" y="2.362" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
				<feColorMatrix in="SourceAlpha" result="hardAlpha" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" />
				<feOffset dx="-.25" dy=".25" />
				<feGaussianBlur stdDeviation=".25" />
				<feComposite in2="hardAlpha" k2="-1" k3="1" operator="arithmetic" />
				<feColorMatrix values="0 0 0 0 0.372549 0 0 0 0 0.607843 0 0 0 0 0.960784 0 0 0 1 0" />
			</filter>
		</defs>
	</svg>
	"""

        val svg = SVG(content)
        cleanupSVG(svg)

        assertEquals(
            content.replace(Regex("\\s*\\n\\s*"), "").replace(Regex("\" />"), "\"/>"),
            svg.toMinifiedString(),
        )
    }

    @Test
    fun keepsGlobalAndInlineStyle() {
        val code =
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><style>@keyframes rotate{from{transform:rotate(0deg)}to{transform:rotate(360deg)}}</style><path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/><path d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z" style="animation:0.75s linear infinite rotate" transform-origin="center"/></svg>"""
        val svg = SVG(code)
        cleanupSVG(svg)

        assertEquals(code, svg.toMinifiedString())
    }

    @Test
    fun keepsTitleAndMovesAttributes() {
        val code = """<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
		<title>TEST</title>
		<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4" />
		</svg>"""
        val svg = SVG(code)
        cleanupSVG(svg, jsObject("{keepTitles: true}").unsafeCast<CleanupSVGOptions>())

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><title>TEST</title><g fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4"/></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun handlesP3Color() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="50" height="72" fill="none" viewBox="0 0 50 72"><path fill="#fa5d19" d="M41.715 23.193c-2.762.82-4.844 2.675-6.37 4.69-.327.432-1.01.107-.88-.423 2.92-12.007-.937-21.986-12.961-26.898a.803.803 0 0 0-1.085.937c5.47 21.961-17.537 20.109-14.63 45.005.05.427-.43.72-.78.47-1.09-.782-2.307-2.415-3.142-3.562a.502.502 0 0 0-.887.16c-.665 2.404-.98 4.67-.98 6.92 0 8.749 4.497 16.45 11.304 20.915.39.255.89-.11.758-.557a13.5 13.5 0 0 1-.563-3.697c0-.788.05-1.593.173-2.343.285-1.885.94-3.68 2.04-5.314 3.772-5.663 11.334-11.132 10.127-18.56-.078-.47.477-.78.827-.457 5.328 4.868 6.383 11.415 5.508 17.287-.075.51.564.782.887.382a11.6 11.6 0 0 1 2.892-2.587c.27-.168.63-.04.733.26.602 1.752 1.497 3.397 2.342 5.042a13.46 13.46 0 0 1 .905 9.982.502.502 0 0 0 .755.57C45.5 66.95 50 59.248 50 50.494c0-3.043-.532-6.025-1.54-8.82-2.112-5.862-7.472-10.264-6.117-17.904.065-.365-.273-.682-.628-.577" style="fill:#fa5d19;fill:color(display-p3 .9816 .3634 .0984);fill-opacity:1"/></svg>"""
        )
        cleanupSVG(svg)

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="50" height="72" viewBox="0 0 50 72"><g fill="none"><path fill="#fa5d19" d="M41.715 23.193c-2.762.82-4.844 2.675-6.37 4.69-.327.432-1.01.107-.88-.423 2.92-12.007-.937-21.986-12.961-26.898a.803.803 0 0 0-1.085.937c5.47 21.961-17.537 20.109-14.63 45.005.05.427-.43.72-.78.47-1.09-.782-2.307-2.415-3.142-3.562a.502.502 0 0 0-.887.16c-.665 2.404-.98 4.67-.98 6.92 0 8.749 4.497 16.45 11.304 20.915.39.255.89-.11.758-.557a13.5 13.5 0 0 1-.563-3.697c0-.788.05-1.593.173-2.343.285-1.885.94-3.68 2.04-5.314 3.772-5.663 11.334-11.132 10.127-18.56-.078-.47.477-.78.827-.457 5.328 4.868 6.383 11.415 5.508 17.287-.075.51.564.782.887.382a11.6 11.6 0 0 1 2.892-2.587c.27-.168.63-.04.733.26.602 1.752 1.497 3.397 2.342 5.042a13.46 13.46 0 0 1 .905 9.982.502.502 0 0 0 .755.57C45.5 66.95 50 59.248 50 50.494c0-3.043-.532-6.025-1.54-8.82-2.112-5.862-7.472-10.264-6.117-17.904.065-.365-.273-.682-.628-.577" fill-opacity="1"/></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun rejectsComplexGlobalStyle() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><style>@font-face { font-family: feedback-iconfont; src: url("x.woff2") format("woff2"), url("x.woff") format("woff"); }</style><rect width="24" height="24"/></svg>"""
        )
        assertFails { cleanupSVG(svg) }
    }

    @Test
    fun keepsAnimationAsIs() {
        val code =
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3h7v18h-14v-18h7Z"><animate attributeName="opacity" values="0;1" dur="1s" repeatCount="indefinite"/></path></g></svg>"""
        val svg = SVG(code)
        cleanupSVG(svg)

        assertEquals(code, svg.toMinifiedString())
    }
}
