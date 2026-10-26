package iconify.tools

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private data class MappedId(val tag: String, val usedAsMask: Boolean, val usedAsPaint: Boolean)

private data class MappedLink(
    val id: String,
    val usedByTag: String,
    val usedAsMask: Boolean,
)

private data class MappedElement(
    val tag: String,
    val parentTag: String? = null,
    val childTags: List<String>? = null,
    val id: String? = null,
    val reusableId: String? = null,
    val ids: List<String>? = null,
    val usedAsMask: Boolean? = null,
    val usedAsPaint: Boolean? = null,
)

private data class MappedTree(
    val tag: String,
    val usedAsMask: Boolean,
    val parentTag: String? = null,
    val children: List<MappedTree>,
)

private fun stringOrNull(value: dynamic): String? = if (value == null) null else value as String

private fun booleanOrNull(value: dynamic): Boolean? = if (value == null) null else value as Boolean

private fun ElementsMap.elementAt(index: Double): ExtendedTagElement =
    get(index) ?: error("Missing element at index $index")

private fun mapIds(result: AnalyseSVGStructureResult): Map<String, MappedId> {
    val mapped = linkedMapOf<String, MappedId>()
    for (id in jsKeys(result.ids)) {
        val element = result.elements.elementAt(result.ids[id]!!)
        mapped[id] = MappedId(
            tag = element.tag,
            usedAsMask = element._usedAsMask,
            usedAsPaint = element._usedAsPaint,
        )
    }
    return mapped
}

private fun mapLinks(result: AnalyseSVGStructureResult): List<MappedLink> {
    val elements = result.elements
    return result.links.map { item ->
        MappedLink(
            id = item.id,
            usedByTag = elements.elementAt(item.usedByIndex).tag,
            usedAsMask = item.usedAsMask,
        )
    }
}

private fun mapElements(result: AnalyseSVGStructureResult): List<MappedElement> {
    val elements = result.elements
    return jsArray(elements).map { pair ->
        val element = pair[1]
        MappedElement(
            tag = element.tag as String,
            parentTag = if (element._parentElement == null) {
                null
            } else {
                elements.elementAt(element._parentElement!!).tag
            },
            childTags = if (element._childElements == null) {
                null
            } else {
                jsArray(element._childElements).map { index ->
                    elements.elementAt(index as Double).tag
                }
            },
            id = stringOrNull(element._id),
            reusableId = if (element._reusableElement == null) {
                null
            } else {
                element._reusableElement.id as String
            },
            ids = if (element._belongsTo == null) {
                null
            } else {
                jsArray(element._belongsTo).map { it.id as String }
            },
            usedAsMask = booleanOrNull(element._usedAsMask),
            usedAsPaint = booleanOrNull(element._usedAsPaint),
        )
    }
}

private fun mapTree(result: AnalyseSVGStructureResult): MappedTree {
    val elements = result.elements

    fun map(item: ElementsTreeItem): MappedTree {
        val element = elements.elementAt(item.index)
        return MappedTree(
            tag = element.tag,
            usedAsMask = item.usedAsMask,
            parentTag = if (item.parent == null) {
                null
            } else {
                elements.elementAt(item.parent!!.index).tag
            },
            children = item.children.map { map(it) },
        )
    }

    return map(result.tree)
}

class AnalyseTest {
    @Test
    fun analysesMaskThatUsesPath() {
        val svg = SVG(
            """<svg width="256px" height="256px" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid">
			<defs>
				<path d="M2.27464661e-14,0 L254.693878,3.04336596e-14 L254.693878,160.344259 C255.3267,161.198982 255.762422,162.157626 256,163.39634 L256,168.36419 C255.762422,169.608049 255.3267,170.691008 254.693878,171.604678 L254.693878,256 L0,256 L0,192 L0,64 L2.27464661e-14,0 Z" id="path-1"></path>
				<radialGradient cx="16.6089694%" cy="17.3718345%" fx="16.6089694%" fy="17.3718345%" r="118.520308%" id="radialGradient-3">
					<stop stop-color="#88CDE7" offset="0%"></stop>
					<stop id="test2" stop-color="#2274AD" offset="100%"></stop>
				</radialGradient>
			</defs>
			<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
				<mask id="mask-2" fill="white">
					<use href="#path-1"></use>
				</mask>
				<polygon id="test" fill="url(#radialGradient-3)" mask="url(#mask-2)" points="0 256 256 256 256 0 0 0"></polygon>
			</g>
		</svg>"""
        )

        val result = analyseSVGStructure(svg)

        assertEquals(
            mapOf(
                "path-1" to MappedId("path", usedAsMask = true, usedAsPaint = false),
                "radialGradient-3" to MappedId("radialGradient", usedAsMask = false, usedAsPaint = true),
                "mask-2" to MappedId("mask", usedAsMask = true, usedAsPaint = false),
                "test" to MappedId("polygon", usedAsMask = false, usedAsPaint = true),
                "test2" to MappedId("stop", usedAsMask = false, usedAsPaint = true),
            ),
            mapIds(result),
        )

        assertEquals(
            listOf(
                MappedLink("path-1", "use", usedAsMask = false),
                MappedLink("radialGradient-3", "polygon", usedAsMask = false),
                MappedLink("mask-2", "polygon", usedAsMask = true),
            ),
            mapLinks(result),
        )

        assertEquals(
            listOf(
                MappedElement("svg", childTags = listOf("g"), usedAsMask = false, usedAsPaint = true),
                MappedElement("defs", usedAsMask = false, usedAsPaint = false),
                MappedElement(
                    "path",
                    id = "path-1",
                    reusableId = "path-1",
                    ids = listOf("path-1"),
                    usedAsMask = true,
                    usedAsPaint = false,
                ),
                MappedElement(
                    "radialGradient",
                    childTags = listOf("stop", "stop"),
                    id = "radialGradient-3",
                    reusableId = "radialGradient-3",
                    ids = listOf("radialGradient-3"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement(
                    "stop",
                    parentTag = "radialGradient",
                    reusableId = "radialGradient-3",
                    ids = listOf("radialGradient-3"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement(
                    "stop",
                    parentTag = "radialGradient",
                    id = "test2",
                    reusableId = "radialGradient-3",
                    ids = listOf("radialGradient-3", "test2"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement(
                    "g",
                    parentTag = "svg",
                    childTags = listOf("polygon"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement(
                    "mask",
                    childTags = listOf("use"),
                    id = "mask-2",
                    reusableId = "mask-2",
                    ids = listOf("mask-2"),
                    usedAsMask = true,
                    usedAsPaint = false,
                ),
                MappedElement(
                    "use",
                    parentTag = "mask",
                    reusableId = "mask-2",
                    ids = listOf("mask-2"),
                    usedAsMask = true,
                    usedAsPaint = false,
                ),
                MappedElement(
                    "polygon",
                    parentTag = "g",
                    id = "test",
                    ids = listOf("test"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
            ),
            mapElements(result),
        )

        assertEquals(
            MappedTree(
                tag = "svg",
                usedAsMask = false,
                children = listOf(
                    MappedTree(
                        tag = "g",
                        usedAsMask = false,
                        parentTag = "svg",
                        children = listOf(
                            MappedTree(
                                tag = "polygon",
                                usedAsMask = false,
                                parentTag = "g",
                                children = listOf(
                                    MappedTree(
                                        tag = "radialGradient",
                                        usedAsMask = false,
                                        parentTag = "polygon",
                                        children = listOf(
                                            MappedTree("stop", false, "radialGradient", emptyList()),
                                            MappedTree("stop", false, "radialGradient", emptyList()),
                                        ),
                                    ),
                                    MappedTree(
                                        tag = "mask",
                                        usedAsMask = true,
                                        parentTag = "polygon",
                                        children = listOf(
                                            MappedTree(
                                                tag = "use",
                                                usedAsMask = false,
                                                parentTag = "mask",
                                                children = listOf(
                                                    MappedTree("path", false, "use", emptyList()),
                                                ),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            mapTree(result),
        )
    }

    @Test
    fun analysesSeveralReferencesToSameElement() {
        val svg = SVG(
            """<svg width="256" height="256" viewBox="0 0 256 256">
			<defs>
				<symbol id="def1" fill="purple">
					<rect x="0" y="0" width="64" height="64" id="def2" />
				</symbol>
			</defs>
			<use href="#def1" fill="red" />
			<use href="#def2" transform="translate(32 32)" />
			<use href="#def2" fill="teal" transform="translate(64 64)" />
		</svg>"""
        )

        val result = analyseSVGStructure(svg)

        assertEquals(
            mapOf(
                "def1" to MappedId("symbol", usedAsMask = false, usedAsPaint = true),
                "def2" to MappedId("rect", usedAsMask = false, usedAsPaint = true),
            ),
            mapIds(result),
        )

        assertEquals(
            listOf(
                MappedLink("def1", "use", usedAsMask = false),
                MappedLink("def2", "use", usedAsMask = false),
                MappedLink("def2", "use", usedAsMask = false),
            ),
            mapLinks(result),
        )

        assertEquals(
            listOf(
                MappedElement(
                    "svg",
                    childTags = listOf("use", "use", "use"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement("defs", usedAsMask = false, usedAsPaint = false),
                MappedElement(
                    "symbol",
                    childTags = listOf("rect"),
                    id = "def1",
                    reusableId = "def1",
                    ids = listOf("def1"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement(
                    "rect",
                    parentTag = "symbol",
                    id = "def2",
                    reusableId = "def1",
                    ids = listOf("def1", "def2"),
                    usedAsMask = false,
                    usedAsPaint = true,
                ),
                MappedElement("use", parentTag = "svg", usedAsMask = false, usedAsPaint = true),
                MappedElement("use", parentTag = "svg", usedAsMask = false, usedAsPaint = true),
                MappedElement("use", parentTag = "svg", usedAsMask = false, usedAsPaint = true),
            ),
            mapElements(result),
        )

        assertEquals(
            MappedTree(
                tag = "svg",
                usedAsMask = false,
                children = listOf(
                    MappedTree(
                        tag = "use",
                        usedAsMask = false,
                        parentTag = "svg",
                        children = listOf(
                            MappedTree(
                                tag = "symbol",
                                usedAsMask = false,
                                parentTag = "use",
                                children = listOf(MappedTree("rect", false, "symbol", emptyList())),
                            ),
                        ),
                    ),
                    MappedTree(
                        tag = "use",
                        usedAsMask = false,
                        parentTag = "svg",
                        children = listOf(MappedTree("rect", false, "use", emptyList())),
                    ),
                    MappedTree(
                        tag = "use",
                        usedAsMask = false,
                        parentTag = "svg",
                        children = listOf(MappedTree("rect", false, "use", emptyList())),
                    ),
                ),
            ),
            mapTree(result),
        )
    }

    @Test
    fun rejectsMissingDefinition() {
        val svg = SVG(
            """<svg width="256px" height="256px" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid">
			<defs>
				<radialGradient cx="16.6089694%" cy="17.3718345%" fx="16.6089694%" fy="17.3718345%" r="118.520308%" id="radialGradient-3">
					<stop stop-color="#88CDE7" offset="0%"></stop>
					<stop id="test2" stop-color="#2274AD" offset="100%"></stop>
				</radialGradient>
			</defs>
			<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
				<mask id="mask-2" fill="white">
					<use href="#path-1"></use>
				</mask>
				<polygon id="test" fill="url(#radialGradient-3)" mask="url(#mask-2)" points="0 256 256 256 256 0 0 0"></polygon>
			</g>
		</svg>"""
        )

        val error = assertFailsWith<Throwable> { analyseSVGStructure(svg) }
        assertEquals("""Missing element with id="path-1"""", errorMessageOf(error))
    }

    @Test
    fun rejectsRecursion() {
        val svg = SVG(
            """<svg width="256px" height="256px" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid">
			<g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
				<mask id="mask-2" fill="white">
					<use href="#test"></use>
				</mask>
				<polygon id="test" mask="url(#mask-2)" points="0 256 256 256 256 0 0 0"></polygon>
			</g>
		</svg>"""
        )

        val error = assertFailsWith<Throwable> { analyseSVGStructure(svg) }
        assertEquals("Recursion", errorMessageOf(error))
    }

    @Test
    fun rejectsClipPathWithoutId() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
			<defs><clipPath><path fill="none" d="M124-288l388-672 388 672H124z" clip-rule="evenodd"/></clipPath></defs><path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43zm370.72-458.44L836 122.88a8 8 0 0 0-11.31 0L715.37 232.23Q624.91 186 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 0 0 0 51.5q56.7 119.43 136.55 191.45L112.56 835a8 8 0 0 0 0 11.31L155.25 889a8 8 0 0 0 11.31 0l712.16-712.12a8 8 0 0 0 0-11.32zM332 512a176 176 0 0 1 258.88-155.28l-48.62 48.62a112.08 112.08 0 0 0-140.92 140.92l-48.62 48.62A175.09 175.09 0 0 1 332 512z"/><path d="M942.2 486.2Q889.4 375 816.51 304.85L672.37 449A176.08 176.08 0 0 1 445 676.37L322.74 798.63Q407.82 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 0 0 0-51.5z"/>
		</svg>"""
        )

        val error = assertFailsWith<Throwable> { analyseSVGStructure(svg) }
        assertEquals("Definition element <clipPath> does not have id", errorMessageOf(error))
    }

    @Test
    fun fixesClipPathWithoutId() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
			<defs><clipPath><path fill="none" d="M124-288l388-672 388 672H124z" clip-rule="evenodd"/></clipPath></defs><path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43zm370.72-458.44L836 122.88a8 8 0 0 0-11.31 0L715.37 232.23Q624.91 186 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 0 0 0 51.5q56.7 119.43 136.55 191.45L112.56 835a8 8 0 0 0 0 11.31L155.25 889a8 8 0 0 0 11.31 0l712.16-712.12a8 8 0 0 0 0-11.32zM332 512a176 176 0 0 1 258.88-155.28l-48.62 48.62a112.08 112.08 0 0 0-140.92 140.92l-48.62 48.62A175.09 175.09 0 0 1 332 512z"/><path d="M942.2 486.2Q889.4 375 816.51 304.85L672.37 449A176.08 176.08 0 0 1 445 676.37L322.74 798.63Q407.82 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 0 0 0-51.5z"/>
		</svg>"""
        )

        val warnings = captureWarnings {
            analyseSVGStructure(svg, jsObject("{fixErrors: true}").unsafeCast<AnalyseSVGStructureOptions>())
        }

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024"><defs/><path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43zm370.72-458.44L836 122.88a8 8 0 0 0-11.31 0L715.37 232.23Q624.91 186 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 0 0 0 51.5q56.7 119.43 136.55 191.45L112.56 835a8 8 0 0 0 0 11.31L155.25 889a8 8 0 0 0 11.31 0l712.16-712.12a8 8 0 0 0 0-11.32zM332 512a176 176 0 0 1 258.88-155.28l-48.62 48.62a112.08 112.08 0 0 0-140.92 140.92l-48.62 48.62A175.09 175.09 0 0 1 332 512z"/><path d="M942.2 486.2Q889.4 375 816.51 304.85L672.37 449A176.08 176.08 0 0 1 445 676.37L322.74 798.63Q407.82 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 0 0 0-51.5z"/></svg>""",
            svg.toMinifiedString(),
        )
        assertTrue(warnings.isNotEmpty())
    }

    @Test
    fun rejectsMissingSymbol() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
			<path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43z"/>
			<use href="#leaf"/>
		</svg>"""
        )

        val error = assertFailsWith<Throwable> { analyseSVGStructure(svg) }
        assertEquals("""Missing element with id="leaf"""", errorMessageOf(error))
    }

    @Test
    fun fixesMissingSymbol() {
        val svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
			<use href="#leaf"/>
			<path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43z"/>
			<path d="M942.2 486.2Q889.4 375 816.51 304.85z" fill="url(#missing-gradient)"/>
		</svg>"""
        )

        val warnings = captureWarnings {
            analyseSVGStructure(svg, jsObject("{fixErrors: true}").unsafeCast<AnalyseSVGStructureOptions>())
        }

        assertEquals(
            """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024"><path d="M508 624a112 112 0 0 0 112-112c0-3.28-.15-6.53-.43-9.74L498.26 623.57c3.21.28 6.45.43 9.74.43z"/><path d="M942.2 486.2Q889.4 375 816.51 304.85z"/></svg>""",
            svg.toMinifiedString(),
        )
        assertTrue(warnings.isNotEmpty())
    }

    @Test
    fun reportsUnorderedAndOrderedIds() {
        val result = analyseSVGStructure(
            SVG("""<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><defs><linearGradient id="a"><stop id="b"/></linearGradient></defs><rect fill="url(#a)" width="24" height="24"/></svg>""")
        )

        assertEquals(listOf("a", "b"), jsKeys(result.ids).toList())
        assertEquals(5, jsArray(result.elements).size)
    }

    private fun errorMessageOf(error: Throwable): String = error.message ?: ""
}
