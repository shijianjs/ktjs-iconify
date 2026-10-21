package iconify.tools

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OptimiseTest {
    @Test
    fun convertsSimpleIconToMask() {
        val path1 =
            """d="M2 6.94975C2 6.06722 2 5.62595 2.06935 5.25839C2.37464 3.64031 3.64031 2.37464 5.25839 2.06935C5.62595 2 6.06722 2 6.94975 2C7.33642 2 7.52976 2 7.71557 2.01738C8.51665 2.09229 9.27652 2.40704 9.89594 2.92051C10.0396 3.03961 10.1763 3.17633 10.4497 3.44975L11 4C11.8158 4.81578 12.2237 5.22367 12.7121 5.49543C12.9804 5.64471 13.2651 5.7626 13.5604 5.84678C14.0979 6 14.6747 6 15.8284 6H16.2021C18.8345 6 20.1506 6 21.0062 6.76946C21.0849 6.84024 21.1598 6.91514 21.2305 6.99383C22 7.84935 22 9.16554 22 11.7979V14C22 17.7712 22 19.6569 20.8284 20.8284C19.6569 22 17.7712 22 14 22H10C6.22876 22 4.34315 22 3.17157 20.8284C2 19.6569 2 17.7712 2 14V6.94975Z""""
        val path2 =
            """fill-rule="evenodd" clip-rule="evenodd" d="M12.25 10C12.25 9.58579 12.5858 9.25 13 9.25H18C18.4142 9.25 18.75 9.58579 18.75 10C18.75 10.4142 18.4142 10.75 18 10.75H13C12.5858 10.75 12.25 10.4142 12.25 10Z""""
        val path3 =
            """d="M16.9856 3.02094C16.8321 3 16.6492 3 16.2835 3H12L12.3699 3.38312C13.0359 4.07299 13.2919 4.33051 13.5877 4.50096C13.7594 4.5999 13.9415 4.67804 14.1304 4.73383C14.4559 4.82993 14.8128 4.83538 15.7546 4.83538L16.089 4.83538C17.0914 4.83536 17.8995 4.83535 18.5389 4.91862C18.6984 4.93939 18.8521 4.96582 19 5C18.8144 3.96313 18.0043 3.15985 16.9856 3.02094Z""""

        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
	<path $path1 fill="#000"/>
	<path $path2 fill="white"/>
	<path $path3 fill="black"/>
</svg>"""
        )
        assertTrue(convertSVGToMask(svg))
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"><defs><mask id="mask"><path $path1 fill="#fff"/><path $path2 fill="#000"/><path $path3 fill="#fff"/></mask></defs><rect mask="url(#mask)" width="24" height="24" fill="currentColor"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun convertsMaskWithCustomColors() {
        val path1 =
            """d="M2 6.94975C2 6.06722 2 5.62595 2.06935 5.25839C2.37464 3.64031 3.64031 2.37464 5.25839 2.06935C5.62595 2 6.06722 2 6.94975 2C7.33642 2 7.52976 2 7.71557 2.01738C8.51665 2.09229 9.27652 2.40704 9.89594 2.92051C10.0396 3.03961 10.1763 3.17633 10.4497 3.44975L11 4C11.8158 4.81578 12.2237 5.22367 12.7121 5.49543C12.9804 5.64471 13.2651 5.7626 13.5604 5.84678C14.0979 6 14.6747 6 15.8284 6H16.2021C18.8345 6 20.1506 6 21.0062 6.76946C21.0849 6.84024 21.1598 6.91514 21.2305 6.99383C22 7.84935 22 9.16554 22 11.7979V14C22 17.7712 22 19.6569 20.8284 20.8284C19.6569 22 17.7712 22 14 22H10C6.22876 22 4.34315 22 3.17157 20.8284C2 19.6569 2 17.7712 2 14V6.94975Z""""
        val path2 =
            """fill-rule="evenodd" clip-rule="evenodd" d="M12.25 10C12.25 9.58579 12.5858 9.25 13 9.25H18C18.4142 9.25 18.75 9.58579 18.75 10C18.75 10.4142 18.4142 10.75 18 10.75H13C12.5858 10.75 12.25 10.4142 12.25 10Z""""
        val path3 =
            """d="M16.9856 3.02094C16.8321 3 16.6492 3 16.2835 3H12L12.3699 3.38312C13.0359 4.07299 13.2919 4.33051 13.5877 4.50096C13.7594 4.5999 13.9415 4.67804 14.1304 4.73383C14.4559 4.82993 14.8128 4.83538 15.7546 4.83538L16.089 4.83538C17.0914 4.83536 17.8995 4.83535 18.5389 4.91862C18.6984 4.93939 18.8521 4.96582 19 5C18.8144 3.96313 18.0043 3.15985 16.9856 3.02094Z""""

        val source =
            """<svg width="32" height="32" viewBox="-4 -4 32 32" fill="none" xmlns="http://www.w3.org/2000/svg"><path $path1 fill="#000"/><path $path2 fill="#111"/><path $path3 fill="Gray"/></svg>"""
        val svg = SVG(source)

        // No colors
        var warnings = captureWarnings {
            assertFalse(convertSVGToMask(svg, jsObject("{}")))
        }
        assertEquals(source, svg.toMinifiedString())
        assertTrue(warnings.isNotEmpty())

        // All colors, but nothing to convert
        warnings = captureWarnings {
            assertFalse(convertSVGToMask(svg, jsObject("{solid: ['#000', '#111', 'gray']}")))
        }
        assertEquals(source, svg.toMinifiedString())
        assertEquals(0, warnings.size)

        // All colors are transparent
        warnings = captureWarnings {
            assertFalse(
                convertSVGToMask(
                    svg,
                    jsObject("{solid: [], transparent: ['#000', '#111', 'gray']}"),
                )
            )
        }
        assertEquals(source, svg.toMinifiedString())
        assertEquals(0, warnings.size)

        // All colors are transparent, callback
        val callbackCalled = mutableListOf<String>()
        val callbackOptions = jsObject("{solid: []}")
        callbackOptions.transparent = { colorStr: dynamic ->
            callbackCalled.add(colorStr as String)
            true
        }
        warnings = captureWarnings {
            assertFalse(convertSVGToMask(svg, callbackOptions))
        }
        assertEquals(source, svg.toMinifiedString())
        assertEquals(listOf("#000", "#111", "Gray"), callbackCalled)
        assertEquals(0, warnings.size)

        // Valid SVG
        warnings = captureWarnings {
            assertTrue(
                convertSVGToMask(
                    svg,
                    jsObject("{id: 'test', solid: ['#000', '#111'], transparent: ['gray']}"),
                )
            )
        }
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="-4 -4 32 32" fill="none"><defs><mask id="test"><path $path1 fill="#fff"/><path $path2 fill="#fff"/><path $path3 fill="#000"/></mask></defs><rect mask="url(#test)" x="-4" y="-4" width="32" height="32" fill="currentColor"/></svg>""",
            svg.toMinifiedString(),
        )
        assertEquals(0, warnings.size)
    }

    @Test
    fun scalesIcon() {
        val svg = SVG(
            """<svg viewBox="0 0 1200 400" xmlns="http://www.w3.org/2000/svg" width="1200" height="400"><path d="M300 200H150A150 150 0 10300 50z"/></svg>"""
        )
        scaleSVG(svg, 1.0 / 20)
        assertEquals(
            """<svg width="60" height="20" viewBox="0 0 60 20"><path d="M15 10H7.5A7.5 7.5 0 1 0 15 2.5z"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun scalesAndShiftsIcon() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 96 960 960"><path d="M480 696h60v-90l70 90h73l-93-120 93-120h-73l-70 90v-90h-60v240Zm-140 0h60V456H280v60h60v180ZM200 936q-33 0-56.5-23.5T120 856V296q0-33 23.5-56.5T200 216h560q33 0 56.5 23.5T840 296v560q0 33-23.5 56.5T760 936H200Zm0-80h560V296H200v560Zm0-560v560-560Z"/></svg>"""
        )
        assertEquals(0.0, svg.viewBox.left)
        assertEquals(96.0, svg.viewBox.top)
        assertEquals(960.0, svg.viewBox.width)
        assertEquals(960.0, svg.viewBox.height)

        scaleSVG(svg, 1.0 / 40)
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24"><path d="M12 15h1.5v-2.25L15.25 15h1.825l-2.325-3 2.325-3H15.25l-1.75 2.25V9H12zm-3.5 0H10V9H7v1.5h1.5zM5 21q-.825 0-1.412-.587T3 19V5q0-.825.588-1.412T5 3h14q.825 0 1.413.588T21 5v14q0 .825-.587 1.413T19 21zm0-2h14V5H5zM5 5v14z"/></svg>""",
            svg.toMinifiedString(),
        )
        assertEquals(0.0, svg.viewBox.left)
        assertEquals(0.0, svg.viewBox.top)
        assertEquals(24.0, svg.viewBox.width)
        assertEquals(24.0, svg.viewBox.height)
    }

    @Test
    fun decompressesArcs() {
        val svg = SVG(
            """<svg viewBox="0 0 1200 400" xmlns="http://www.w3.org/2000/svg" width="1200" height="400"><path d="M300 200H150A150 150 0 10300 50z"/></svg>"""
        )
        deOptimisePaths(svg)
        assertEquals(
            """<svg viewBox="0 0 1200 400" xmlns="http://www.w3.org/2000/svg" width="1200" height="400"><path d="M300 200H150A150 150 0 1 0 300 50z"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun decompressesLines() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"><path fill="currentColor" d="m8 8 8-8 8 8z"/></svg>"""
        )
        deOptimisePaths(svg)
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"><path fill="currentColor" d="m8 8l8-8l8 8z"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun removesFigmaClipPath() {
        val paths =
            """<path d="M19 13.5V18.6518C19 18.8671 18.8846 19.0659 18.6977 19.1728L12.2977 22.8299C12.1132 22.9353 11.8868 22.9353 11.7023 22.8299L5.30233 19.1728C5.11539 19.0659 5.00001 18.8671 5.00001 18.6518L5 13" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"/>"""
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <g clip-path="url(#clip0_3377_18972)">
        $paths
    </g>
    <defs>
        <clipPath id="clip0_3377_18972">
            <rect width="24" height="24" fill="white"/>
        </clipPath>
    </defs>
</svg>"""
        )
        removeFigmaClipPathFromSVG(svg)
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><g>$paths</g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun removesClipPathFromPathsWithoutGroup() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9" stroke="#292929" stroke-linecap="round" stroke-linejoin="round" clip-path="url(#clip0_3377_18972)" />
    <rect x="12.01" y="12" width="0.01" height="0.01" transform="rotate(90 12.01 12)" stroke="#292929" stroke-width="1.5" stroke-linejoin="round" clip-path="url(#clip0_3377_18972)"/>
    <defs>
        <clipPath id="clip0_3377_18972">
            <rect width="24" height="24" fill="white"/>
        </clipPath>
    </defs>
</svg>"""
        )
        removeFigmaClipPathFromSVG(svg)
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="9" stroke="#292929" stroke-linecap="round" stroke-linejoin="round"/><rect x="12.01" y="12" width="0.01" height="0.01" transform="rotate(90 12.01 12)" stroke="#292929" stroke-width="1.5" stroke-linejoin="round"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun movesClipPathAttributes() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <g clip-path="url(#clip0_3377_18972)">
        <path d="M0 0z" stroke="currentColor" />
    </g>
    <defs>
        <clipPath id="clip0_3377_18972">
            <rect width="24" height="24" fill="#fff" stroke-width="1.5" transform="translate(0 0.000976562)"/>
        </clipPath>
    </defs>
</svg>"""
        )
        removeFigmaClipPathFromSVG(svg)
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><g stroke-width="1.5"><path d="M0 0z" stroke="currentColor"/></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun cleansPenpotDoubleClipPath() {
        val svg = SVG(
            """<svg xmlns:xlink="http://www.w3.org/1999/xlink" width="12" xmlns="http://www.w3.org/2000/svg" height="12" id="screenshot-baf79b5d-6e18-809d-8003-dfd1a1d08748" viewBox="0 0 12 12" style="-webkit-print-color-adjust: exact;" fill="none" version="1.1">
	<g id="shape-baf79b5d-6e18-809d-8003-dfd1a1d08748">
		<defs>
			<clipPath class="frame-clip-def frame-clip" id="frame-clip-baf79b5d-6e18-809d-8003-dfd1a1d08748-rumext-id-1">
				<rect rx="0" ry="0" x="0" y="0" width="12" height="12" transform="" />
			</clipPath>
		</defs>
		<g clip-path="url(#frame-clip-baf79b5d-6e18-809d-8003-dfd1a1d08748-rumext-id-1)">
			<clipPath class="frame-clip-def frame-clip" id="frame-clip-baf79b5d-6e18-809d-8003-dfd1a1d08748-rumext-id-1">
				<rect rx="0" ry="0" x="0" y="0" width="12" height="12" transform="" />
			</clipPath>
			<g class="fills" id="fills-baf79b5d-6e18-809d-8003-dfd1a1d08748">
				<rect rx="0" ry="0" x="0" y="0" transform="" width="12" height="12" class="frame-background" />
			</g>
			<g class="frame-children">
				<g id="shape-baf79b5d-6e18-809d-8003-dfceca1d6601">
					<g class="fills" id="fills-baf79b5d-6e18-809d-8003-dfceca1d6601">
						<path rx="0" ry="0" d="M6.000,5.293L10.789,0.503L11.496,1.211L6.706,6.001L11.496,10.790L10.789,11.497L5.999,6.707L1.210,11.497L0.503,10.790L5.293,6.000L0.502,1.211L1.210,0.504L6.000,5.293ZL6.000,5.293ZZ" style="fill: rgb(0, 0, 0);" />
					</g>
				</g>
			</g>
		</g>
	</g>
</svg>"""
        )
        removeFigmaClipPathFromSVG(svg)
        assertEquals(
            """<svg width="12" xmlns="http://www.w3.org/2000/svg" height="12" viewBox="0 0 12 12" fill="none"><g><g><rect width="12" height="12"/></g><g><g><g><path d="M6.000,5.293L10.789,0.503L11.496,1.211L6.706,6.001L11.496,10.790L10.789,11.497L5.999,6.707L1.210,11.497L0.503,10.790L5.293,6.000L0.502,1.211L1.210,0.504L6.000,5.293ZL6.000,5.293ZZ" fill="#000"/></g></g></g></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun cleansPenpotTransformationMatrix() {
        val svg = SVG(
            """<svg xmlns:xlink="http://www.w3.org/1999/xlink" width="24" xmlns="http://www.w3.org/2000/svg" height="24" id="screenshot-775c72fc-4c66-80f9-8003-e040725f2984" viewBox="0 0 24 24" style="-webkit-print-color-adjust: exact;" fill="none" version="1.1">
		<g id="shape-775c72fc-4c66-80f9-8003-e040725f2984">
		 <defs>
		  <clipPath class="frame-clip-def frame-clip" id="frame-clip-775c72fc-4c66-80f9-8003-e040725f2984-rumext-id-1">
		   <rect rx="0" ry="0" x="0" y="0" width="24" height="24" transform="" style="fill: rgb(255, 255, 255); fill-opacity: 1;" />
		  </clipPath>
		 </defs>
		 <g clip-path="url(#frame-clip-775c72fc-4c66-80f9-8003-e040725f2984-rumext-id-1)" fill="none">
		  <clipPath class="frame-clip-def frame-clip" id="frame-clip-775c72fc-4c66-80f9-8003-e040725f2984-rumext-id-1">
		   <rect rx="0" ry="0" x="0" y="0" width="24" height="24" transform="" style="fill: rgb(255, 255, 255); fill-opacity: 1;" />
		  </clipPath>
		  <g class="fills" id="fills-775c72fc-4c66-80f9-8003-e040725f2984">
		   <rect width="24" height="24" class="frame-background" x="0" transform="" style="fill: rgb(255, 255, 255); fill-opacity: 1;" ry="0" rx="0" y="0" />
		  </g>
		  <g class="frame-children">
		   <g id="shape-775c72fc-4c66-80f9-8003-e04068de381e">
			<g class="fills" id="fills-775c72fc-4c66-80f9-8003-e04068de381e">
			 <ellipse rx="12" ry="12" cx="12" cy="12" transform="matrix(1.000000, 0.000000, 0.000000, 1.000000, 0.000000, 0.000000)" style="fill: rgb(177, 178, 181); fill-opacity: 1;" />
			</g>
		   </g>
		  </g>
		 </g>
		</g>
	   </svg>"""
        )
        removeFigmaClipPathFromSVG(svg)
        assertEquals(
            """<svg width="24" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" fill="none"><g fill="none"><g><rect width="24" height="24" fill="#fff"/></g><g><g><g><ellipse rx="12" ry="12" cx="12" cy="12" fill="#b1b2b5"/></g></g></g></g></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun keepsShapeWithAnimation() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24"><rect x="2" y="4" width="20" height="16" fill="#307594"><set attributeName="height" to="0" /><set attributeName="opacity" to="0" /><animate attributeName="height" values="0;16" dur="1s" fill="freeze" /><animate attributeName="opacity" values="0;1" dur="1.5s" fill="freeze" /></rect></svg>"""
        )
        runSVGO(svg)
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24"><rect width="20" height="16" x="2" y="4" fill="#307594"><set attributeName="height" to="0"/><set attributeName="opacity" to="0"/><animate fill="freeze" attributeName="height" dur="1s" values="0;16"/><animate fill="freeze" attributeName="opacity" dur="1.5s" values="0;1"/></rect></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun doesNotBreakAnimation() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24"><rect x="2" y="4" width="20" height="16" fill="#307594"><set attributeName="height" to="0" /><set attributeName="opacity" to="0" /><animate attributeName="height" values="0;16" dur="1s" fill="freeze" /><animate attributeName="opacity" values="0;1" dur="1.5s" fill="freeze" /></rect></svg>"""
        )
        runSVGO(svg, jsObject("{keepShapes: false}").unsafeCast<SVGOptions>())
        assertEquals(
            """<svg width="24" height="24" viewBox="0 0 24 24"><rect width="20" height="16" x="2" y="4" fill="#307594"><set attributeName="height" to="0"/><set attributeName="opacity" to="0"/><animate fill="freeze" attributeName="height" dur="1s" values="0;16"/><animate fill="freeze" attributeName="opacity" dur="1.5s" values="0;1"/></rect></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun keepsPathsUsedByAnimationClasses() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" preserveAspectRatio="xMidYMid meet" viewBox="0 0 24 24"><g fill="none"><path d="M12 12L19 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="il-md-length-15 il-md-duration-4 il-md-delay-0"/><path d="M12 12L5 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="il-md-length-15 il-md-duration-4 il-md-delay-0"/><path d="M12 12L5 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="il-md-length-15 il-md-duration-4 il-md-delay-0"/><path d="M12 12L19 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="il-md-length-15 il-md-duration-4 il-md-delay-0"/></g></svg>"""
        )
        runSVGO(svg, jsObject("{keepShapes: false}").unsafeCast<SVGOptions>())
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="2" d="m12 12 7 7m-7-7L5 5m7 7-7 7m7-7 7-7" class="il-md-length-15 il-md-duration-4 il-md-delay-0"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun replacesIds() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><mask id="a"><g fill="none" stroke="#fff" stroke-width="4"><circle cx="24" cy="24" r="20" fill="#555"/><path stroke-linecap="round" stroke-linejoin="round" d="M32 16H16m8 18V16"/></g></mask><path fill="currentColor" d="M0 0h48v48H0z" mask="url(#a)"/></svg>"""
        )
        runSVGO(svg)
        analyseSVGStructure(svg)
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><mask id="svgID0"><g fill="none" stroke="#fff" stroke-width="4"><circle cx="24" cy="24" r="20" fill="#555"/><path stroke-linecap="round" stroke-linejoin="round" d="M32 16H16m8 18V16"/></g></mask><path fill="currentColor" d="M0 0h48v48H0z" mask="url(#svgID0)"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun replacesIdsWithCustomPrefix() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><defs><linearGradient id="a" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#FF4D91"/><stop offset="1" stop-color="#F34A5F"/></linearGradient></defs><rect width="24" height="24" fill="url(#a)"/></svg>"""
        )
        runSVGO(svg, jsObject("{cleanupIDs: 'test'}").unsafeCast<SVGOptions>())
        analyseSVGStructure(svg)
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><defs><linearGradient id="test0" x1="0" x2="1" y1="0" y2="1"><stop stop-color="#ff4d91"/><stop offset="1" stop-color="#f34a5f"/></linearGradient></defs><path fill="url(#test0)" d="M0 0h24v24H0z"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun replacesReusableElements() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><mask id="mask"><g fill="none"><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M44 44L4 4V44H44Z"/><path fill="#555" fill-rule="evenodd" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path fill="#555" fill-rule="evenodd" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path fill="#555" fill-rule="evenodd" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path fill="#555" fill-rule="evenodd" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M13 35H25L13 23V35Z" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M30 44V41"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M24 44V41"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M18 44V41"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M12 44V41"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M4 36H7"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M4 30H7"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M4 24H7"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M4 18H7"/></g></mask><rect mask="url(#mask)" x="0" y="0" width="48" height="48" fill="#000"/></svg>"""
        )
        runSVGO(svg)
        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><defs><path id="svgID0" fill="#555" d="M13 35h12L13 23z"/><path id="svgID1" stroke="#fff" d="M13 35h12L13 23z"/></defs><mask id="svgID2"><g fill="none"><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M44 44 4 4v40z"/><use href="#svgID0" fill-rule="evenodd" clip-rule="evenodd"/><use href="#svgID0" fill-rule="evenodd" clip-rule="evenodd"/><use href="#svgID0" fill-rule="evenodd" clip-rule="evenodd"/><use href="#svgID0" fill-rule="evenodd" clip-rule="evenodd"/><use href="#svgID1" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" clip-rule="evenodd"/><use href="#svgID1" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" clip-rule="evenodd"/><use href="#svgID1" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" clip-rule="evenodd"/><use href="#svgID1" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" clip-rule="evenodd"/><path stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="4" d="M30 44v-3m-6 3v-3m-6 3v-3m-6 3v-3m-8-5h3m-3-6h3m-3-6h3m-3-6h3"/></g></mask><path d="M0 0h48v48H0z" mask="url(#svgID2)"/></svg>""",
            svg.toMinifiedString(),
        )
    }

    @Test
    fun keepsAnimatedIds() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M12,21L15.6,16.2C14.6,15.45 13.35,15 12,15C10.65,15 9.4,15.45 8.4,16.2L12,21" opacity="0"><animate id="spinner_jbAr" begin="0;spinner_8ff3.end+0.2s" attributeName="opacity" calcMode="discrete" dur="0.25s" values="0;1" fill="freeze"/><animate id="spinner_8ff3" begin="spinner_aTlH.end+0.5s" attributeName="opacity" dur="0.001s" values="1;0" fill="freeze"/></path><path d="M12,9C9.3,9 6.81,9.89 4.8,11.4L6.6,13.8C8.1,12.67 9.97,12 12,12C14.03,12 15.9,12.67 17.4,13.8L19.2,11.4C17.19,9.89 14.7,9 12,9Z" opacity="0"><animate id="spinner_dof4" begin="spinner_jbAr.end" attributeName="opacity" calcMode="discrete" dur="0.25s" values="0;1" fill="freeze"/><animate begin="spinner_aTlH.end+0.5s" attributeName="opacity" dur="0.001s" values="1;0" fill="freeze"/></path><path d="M12,3C7.95,3 4.21,4.34 1.2,6.6L3,9C5.5,7.12 8.62,6 12,6C15.38,6 18.5,7.12 21,9L22.8,6.6C19.79,4.34 16.05,3 12,3" opacity="0"><animate id="spinner_aTlH" begin="spinner_dof4.end" attributeName="opacity" calcMode="discrete" dur="0.25s" values="0;1" fill="freeze"/><animate begin="spinner_aTlH.end+0.5s" attributeName="opacity" dur="0.001s" values="1;0" fill="freeze"/></path></svg>"""
        )
        runSVGO(svg, jsObject("{keepShapes: true}").unsafeCast<SVGOptions>())
        assertFalse(svg.toMinifiedString().contains("spinner_"))
    }

    @Test
    fun keepsFilterWithoutTransform() {
        val svg = SVG(
            """<svg width="320" height="320" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg"><g filter="url(#filter1_i_18_3767)"><rect x="19.087" y="12.7969" width="14" height="6" rx="3" transform="rotate(31.3889 19.087 12.7969)" fill="#DE773D"/><rect x="19.087" y="12.7969" width="14" height="6" rx="3" transform="rotate(31.3889 19.087 12.7969)" fill="url(#paint8_radial_18_3767)"/><rect x="19.087" y="12.7969" width="14" height="6" rx="3" transform="rotate(31.3889 19.087 12.7969)" fill="url(#paint9_linear_18_3767)"/><rect x="19.087" y="12.7969" width="14" height="6" rx="3" transform="rotate(31.3889 19.087 12.7969)" fill="url(#paint10_radial_18_3767)"/><rect x="19.087" y="12.7969" width="14" height="6" rx="3" transform="rotate(31.3889 19.087 12.7969)" fill="url(#paint11_radial_18_3767)"/></g><defs><filter id="filter1_i_18_3767" x="17.085" y="13.7699" width="12.9801" height="10.3176" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB"><feFlood flood-opacity="0" result="BackgroundImageFix"/><feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/><feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/><feOffset dx="0.15" dy="-0.15"/><feGaussianBlur stdDeviation="0.5"/><feComposite in2="hardAlpha" operator="arithmetic" k2="-1" k3="1"/><feColorMatrix type="matrix" values="0 0 0 0 0.580392 0 0 0 0 0.25098 0 0 0 0 0.309804 0 0 0 1 0"/><feBlend mode="normal" in2="shape" result="effect1_innerShadow_18_3767"/></filter><radialGradient id="paint8_radial_18_3767" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(28.5912 19.9011) rotate(-84.3783) scale(5.08751 7.13795)"><stop offset="0.175294" stop-color="#983C50"/><stop offset="1" stop-color="#983C50" stop-opacity="0"/></radialGradient><linearGradient id="paint9_linear_18_3767" x1="26.0987" y1="12.6361" x2="25.9033" y2="14.4759" gradientUnits="userSpaceOnUse"><stop stop-color="#EB9251"/><stop offset="1" stop-color="#EB9251" stop-opacity="0"/></linearGradient><radialGradient id="paint10_radial_18_3767" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(31.5998 13.6726) rotate(95.481) scale(2.5 1.64694)"><stop stop-color="#FFB075"/><stop offset="1" stop-color="#FFB075" stop-opacity="0"/></radialGradient><radialGradient id="paint11_radial_18_3767" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(23.9116 17.6313) rotate(-45.9881) scale(3.47146 4.6882)"><stop offset="0.845647" stop-color="#FFAA6F"/><stop offset="1" stop-color="#FFAA6F" stop-opacity="0"/></radialGradient></defs></svg>"""
        )
        runSVGO(svg, jsObject("{keepShapes: true}").unsafeCast<SVGOptions>())

        val code = svg.toMinifiedString()
        val groupStart = code.indexOf("<g ")
        val groupEnd = code.indexOf(">", groupStart)
        val group = code.substring(groupStart, groupEnd)
        assertTrue(group.contains("filter="))
        assertFalse(group.contains("transform="))
    }

    @Test
    fun keepsTitle() {
        val svg = SVG(
            """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><title>Test</title><path d="M0 0V24H24V0z" /></svg>"""
        )
        runSVGO(svg)
        assertTrue(svg.toMinifiedString().contains("<title>Test</title>"))
    }

    @Test
    fun handlesSvgoRegression() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128">
		<path fill="#BD202E" d="M15.852 62.452Z"/>
		<path fill="#BD202E" d="M15.852 62.452Z" opacity=".35"/>
	</svg>"""
        )
        runSVGO(svg)
        analyseSVGStructure(svg, jsObject("{fixErrors: false}").unsafeCast<AnalyseSVGStructureOptions>())
    }
}
