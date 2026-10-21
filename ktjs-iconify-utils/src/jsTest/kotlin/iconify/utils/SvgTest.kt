package iconify.utils

import iconify.IconifyIcon
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SvgTest {
    private fun icon(json: String): IconifyIcon = jsObject(json).unsafeCast<IconifyIcon>()

    private fun customisations(json: String): IconifyIconCustomisations =
        jsObject(json).unsafeCast<IconifyIconCustomisations>()

    private fun assertViewBox(expected: Array<Double>, actual: Array<Double>?) {
        assertContentEquals(expected, actual)
    }

    @Test
    fun parsesViewBox() {
        assertViewBox(arrayOf(1.0, 2.0, 3.0, 4.0), getSVGViewBox("1 2 3 4"))
        assertViewBox(arrayOf(-1.0, 0.0, 25.5, -123.5), getSVGViewBox("-1 0 25.5 -123.5"))
        assertViewBox(arrayOf(1.0, 2.0, 3.0, 4.0), getSVGViewBox("  1\t2   3\n4\t "))

        assertNull(getSVGViewBox("1 2 3"))
        assertNull(getSVGViewBox("1 2 3 4 5"))
        assertNull(getSVGViewBox("a 1 2 3"))
        assertNull(getSVGViewBox("0 1 2 b"))
        assertNull(getSVGViewBox("1 2 3 4b"))
    }

    @Test
    fun calculatesSize() {
        val width = 36.0
        val height = 48.0

        assertEquals("36", calculateSize("48", width / height).unsafeCast<String>())
        assertEquals("48", calculateSize("36", height / width).unsafeCast<String>())
        assertEquals(36.0, calculateSize(48, width / height).unsafeCast<Double>())
        assertEquals(48.0, calculateSize(36, height / width).unsafeCast<Double>())

        assertEquals(18.0, calculateSize(24, width / height).unsafeCast<Double>())
        assertEquals(22.5, calculateSize(30, width / height).unsafeCast<Double>())
        assertEquals(74.25, calculateSize(99, width / height).unsafeCast<Double>())
        assertEquals(44.45, calculateSize(100.0 / 3, height / width).unsafeCast<Double>())
        assertEquals(8.34, calculateSize(11.1111111, width / height).unsafeCast<Double>())
        assertEquals(8.334, calculateSize(11.1111111, width / height, 1000).unsafeCast<Double>())

        assertEquals("36px", calculateSize("48px", width / height).unsafeCast<String>())
        assertEquals("18%", calculateSize("24%", width / height).unsafeCast<String>())
        assertEquals("0.75em", calculateSize("1em", width / height).unsafeCast<String>())
        assertEquals("18 Pixels", calculateSize("24 Pixels", width / height).unsafeCast<String>())
        assertEquals(
            "36% + 3.75em",
            calculateSize("48% + 5em", width / height).unsafeCast<String>(),
        )
        assertEquals(
            "calc(1.34em + 10.67px)",
            calculateSize("calc(1em + 8px)", height / width).unsafeCast<String>(),
        )
        assertEquals(
            "-webkit-calc(0.75em + 6px)",
            calculateSize("-webkit-calc(1em + 8px)", width / height).unsafeCast<String>(),
        )
        assertEquals("-.", calculateSize("-.", width / height).unsafeCast<String>())
        assertEquals("@foo", calculateSize("@foo", width / height).unsafeCast<String>())
    }

    @Test
    fun buildsEmptyIcon() {
        val result = iconToSVG(icon("{ body: '' }"))
        assertEquals("1em", result.attributes.width)
        assertEquals("1em", result.attributes.height)
        assertEquals("0 0 16 16", result.attributes.viewBox)
        assertViewBox(arrayOf(0.0, 0.0, 16.0, 16.0), result.viewBox)
        assertEquals("", result.body)

        assertEquals(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\"></svg>",
            iconToHTML(result.body, result.attributes),
        )
    }

    @Test
    fun buildsIconWithAutoSize() {
        val icon = icon("{ body: '<path d=\"\" />' }")

        var result = iconToSVG(icon, customisations("{ height: 'auto' }"))
        assertEquals("16", result.attributes.width)
        assertEquals("16", result.attributes.height)
        assertEquals("0 0 16 16", result.attributes.viewBox)

        result = iconToSVG(icon, customisations("{ width: 'auto', height: 'auto' }"))
        assertEquals("16", result.attributes.width)
        assertEquals("16", result.attributes.height)
    }

    @Test
    fun buildsIconWithCustomSize() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />' }")

        val result = iconToSVG(icon, customisations("{ height: 'auto' }"))
        assertEquals("20", result.attributes.width)
        assertEquals("16", result.attributes.height)
        assertEquals("0 0 20 16", result.attributes.viewBox)
        assertViewBox(arrayOf(0.0, 0.0, 20.0, 16.0), result.viewBox)
        assertEquals("<path d=\"...\" />", result.body)
    }

    @Test
    fun removesUnsetDimensions() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />' }")

        val unsetHeight = iconToSVG(icon, customisations("{ height: 'unset', width: 'auto' }"))
        assertEquals("20", unsetHeight.attributes.width)
        assertNull(unsetHeight.attributes.height)
        assertEquals("0 0 20 16", unsetHeight.attributes.viewBox)

        val unsetSize =
            iconToSVG(icon, customisations("{ width: 'undefined', height: 'none' }"))
        assertNull(unsetSize.attributes.width)
        assertNull(unsetSize.attributes.height)
        assertEquals("0 0 20 16", unsetSize.attributes.viewBox)
    }

    @Test
    fun rotatesIcon() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />' }")

        val result = iconToSVG(icon, customisations("{ height: '40px', rotate: 1 }"))
        assertEquals("32px", result.attributes.width)
        assertEquals("40px", result.attributes.height)
        assertEquals("0 0 16 20", result.attributes.viewBox)
        assertEquals("<g transform=\"rotate(90 8 8)\"><path d=\"...\" /></g>", result.body)

        val negative = iconToSVG(icon, customisations("{ height: '40px', rotate: -1 }"))
        assertEquals("32px", negative.attributes.width)
        assertEquals("40px", negative.attributes.height)
        assertEquals("<g transform=\"rotate(-90 10 10)\"><path d=\"...\" /></g>", negative.body)
    }

    @Test
    fun flipsIcon() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />' }")

        val result = iconToSVG(icon, customisations("{ height: '32', hFlip: true }"))
        assertEquals("40", result.attributes.width)
        assertEquals("32", result.attributes.height)
        assertEquals(
            "<g transform=\"translate(20 0) scale(-1 1)\"><path d=\"...\" /></g>",
            result.body,
        )

        val flippedAndRotated =
            iconToSVG(icon, customisations("{ hFlip: true, rotate: 1 }"))
        assertEquals("0.8em", flippedAndRotated.attributes.width)
        assertEquals("1em", flippedAndRotated.attributes.height)
        assertEquals("0 0 16 20", flippedAndRotated.attributes.viewBox)
        assertEquals(
            "<g transform=\"rotate(90 8 8) translate(20 0) scale(-1 1)\"><path d=\"...\" /></g>",
            flippedAndRotated.body,
        )
    }

    @Test
    fun flipsIconThatIsRotatedByDefault() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />', rotate: 1 }")

        val result = iconToSVG(icon, customisations("{ hFlip: true }"))
        assertEquals("0.8em", result.attributes.width)
        assertEquals("1em", result.attributes.height)
        assertEquals("0 0 16 20", result.attributes.viewBox)
        assertEquals(
            "<g transform=\"translate(16 0) scale(-1 1)\"><g transform=\"rotate(90 8 8)\"><path d=\"...\" /></g></g>",
            result.body,
        )
    }

    @Test
    fun cancelsFlipAndRotation() {
        val icon = icon("{ width: 20, height: 16, body: '<path d=\"...\" />' }")

        val result = iconToSVG(
            icon,
            customisations("{ width: '1em', height: 'auto', hFlip: true, vFlip: true, rotate: 2 }"),
        )
        assertEquals("1em", result.attributes.width)
        assertEquals("16", result.attributes.height)
        assertEquals("0 0 20 16", result.attributes.viewBox)
        assertEquals("<path d=\"...\" />", result.body)
    }

    @Test
    fun flipsIconWithOwnFlipFlag() {
        val iconBody =
            "<g stroke=\"currentColor\" stroke-width=\"16\"><path d=\"M40 64l48-48\"/></g>"
        val icon = icon("{ body: '$iconBody', width: 128, height: 128, hFlip: true }")

        val result = iconToSVG(icon)
        assertEquals("1em", result.attributes.width)
        assertEquals("1em", result.attributes.height)
        assertEquals("0 0 128 128", result.attributes.viewBox)
        assertEquals("<g transform=\"translate(128 0) scale(-1 1)\">$iconBody</g>", result.body)
    }

    @Test
    fun buildsHTMLWithNamespaces() {
        assertEquals(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\"></svg>",
            iconToHTML("", jsObject("{ width: '16', height: '16' }")),
        )

        assertEquals(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><use xlink:href=\"#icon\" /></svg>",
            iconToHTML("<use xlink:href=\"#icon\" />", jsObject("{}")),
        )
    }

    @Test
    fun parsesSimpleSVG() {
        val body = "<path d=\"M12,21L15.6,16.2Z\" opacity=\"0\"></path>"
        val svg = "<svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\">$body</svg>"

        val parsed = assertNotNull(parseSVGContent(svg))
        assertEquals("24", prop(parsed.attribs, "width").unsafeCast<String>())
        assertEquals("24", prop(parsed.attribs, "height").unsafeCast<String>())
        assertEquals("0 0 24 24", prop(parsed.attribs, "viewBox").unsafeCast<String>())
        assertEquals("http://www.w3.org/2000/svg", prop(parsed.attribs, "xmlns").unsafeCast<String>())
        assertEquals(body, parsed.body)

        val built = assertNotNull(buildParsedSVG(parsed))
        assertEquals("24", built.attributes.width)
        assertEquals("24", built.attributes.height)
        assertEquals("0 0 24 24", built.attributes.viewBox)
        assertViewBox(arrayOf(0.0, 0.0, 24.0, 24.0), built.viewBox)
        assertEquals(body, built.body)

        val converted = assertNotNull(convertParsedSVG(parsed))
        assertEquals(0.0, converted.left)
        assertEquals(0.0, converted.top)
        assertEquals(24.0, converted.width)
        assertEquals(24.0, converted.height)
        assertEquals(body, converted.body)

        val split = splitSVGDefs(body)
        assertEquals("", split.defs)
        assertEquals(body, split.content)
    }

    @Test
    fun parsesSVGWithXmlHeading() {
        val svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
            "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"
        val body = "<circle cx=\"60\" cy=\"60\" r=\"50\"/>"

        val parsed = assertNotNull(parseSVGContent(svg))
        assertEquals("0 0 120 120", prop(parsed.attribs, "viewBox").unsafeCast<String>())
        assertEquals(body, parsed.body)

        val built = assertNotNull(buildParsedSVG(parsed))
        assertEquals("0 0 120 120", built.attributes.viewBox)
        assertNull(built.attributes.width)
        assertEquals(body, built.body)
    }

    @Test
    fun parsesSVGWithPresentationAttributes() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f80\"/>"
        val svg = "<svg width=\"32\" height=\"32\" viewBox=\"0 0 32 32\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">$body</svg>"

        val parsed = assertNotNull(parseSVGContent(svg))
        assertEquals("none", prop(parsed.attribs, "fill").unsafeCast<String>())

        val built = assertNotNull(buildParsedSVG(parsed))
        assertEquals("<g fill=\"none\">$body</g>", built.body)
    }

    @Test
    fun parsesNestedSVG() {
        val body = "<circle cx=\"50\" cy=\"50\" r=\"40\" />\n" +
            "<svg viewBox=\"0 0 10 10\" x=\"200\" width=\"100\"><circle cx=\"5\" cy=\"5\" r=\"4\" /></svg>"
        val svg = "<svg viewBox=\"0 0 300 100\" xmlns=\"http://www.w3.org/2000/svg\" stroke=\"red\" fill=\"grey\">\n$body\n</svg>"

        val parsed = assertNotNull(parseSVGContent(svg))
        assertEquals("0 0 300 100", prop(parsed.attribs, "viewBox").unsafeCast<String>())
        assertEquals("red", prop(parsed.attribs, "stroke").unsafeCast<String>())
        assertEquals(body, parsed.body)
    }

    @Test
    fun returnsNullForContentWithoutSVG() {
        assertNull(parseSVGContent("<div></div>"))
    }

    @Test
    fun splitsAndMergesDefs() {
        val defs = "<clipPath id=\"clip\"><path d=\"M0 0h1v1z\"/></clipPath>"
        val content = "<rect width=\"1\" height=\"1\"/>"
        val body = "<defs>$defs</defs>$content"

        val split = splitSVGDefs(body)
        assertEquals(defs, split.defs)
        assertEquals(content, split.content)

        assertEquals(body, mergeDefsAndContent(defs, content))
        assertEquals(content, mergeDefsAndContent("", content))
    }

    @Test
    fun wrapsContentLeavingDefsUnwrapped() {
        val defs = "<linearGradient id=\"g\"/>"
        val content = "<path d=\"M0 0\"/>"

        assertEquals(
            "<defs>$defs</defs><g fill=\"none\">$content</g>",
            wrapSVGContent("<defs>$defs</defs>$content", "<g fill=\"none\">", "</g>"),
        )
    }

    @Test
    fun replacesIDs() {
        val body = "<defs><path id=\"test1\"/></defs><use fill=\"#FFA000\" xlink:href=\"#test1\"/>"
        val expected = "<defs><path id=\"test\"/></defs><use fill=\"#FFA000\" xlink:href=\"#test\"/>"

        clearIDCache()
        assertEquals(expected, replaceIDs(body))
    }

    @Test
    fun replacesMatchingIDsWithoutBreakingReferences() {
        val body =
            "<defs><path id=\"test1\"/><path id=\"test\"/></defs><use fill=\"#FFA000\" xlink:href=\"#test1\"/><use fill=\"#00f\" xlink:href=\"#test\"/>"
        val expected =
            "<defs><path id=\"test\"/><path id=\"test1\"/></defs><use fill=\"#FFA000\" xlink:href=\"#test\"/><use fill=\"#00f\" xlink:href=\"#test1\"/>"

        clearIDCache()
        assertEquals(expected, replaceIDs(body))
    }

    @Test
    fun replacesIDsInAnimations() {
        val body =
            "<rect x=\"2\" y=\"2\" width=\"20\" height=\"10\" fill=\"currentColor\"><animate id=\"animation1\" attributeName=\"height\" values=\"10;5\" dur=\"0.5s\" fill=\"freeze\" /><animate id=\"animation2\" attributeName=\"width\" values=\"20;5\" dur=\"0.2s\" begin=\"animation1.end+1s\" fill=\"freeze\" /></rect><rect x=\"2\" y=\"12\" width=\"20\" height=\"5\" fill=\"currentColor\"><animate attributeName=\"height\" values=\"5;10;5\" begin=\"animation1.end+0.2s;animation2.end-0.2s\" dur=\"0.3s\" /></rect>"

        clearIDCache()
        assertEquals(
            "<rect x=\"2\" y=\"2\" width=\"20\" height=\"10\" fill=\"currentColor\"><animate id=\"animation\" attributeName=\"height\" values=\"10;5\" dur=\"0.5s\" fill=\"freeze\" /><animate id=\"animation1\" attributeName=\"width\" values=\"20;5\" dur=\"0.2s\" begin=\"animation.end+1s\" fill=\"freeze\" /></rect><rect x=\"2\" y=\"12\" width=\"20\" height=\"5\" fill=\"currentColor\"><animate attributeName=\"height\" values=\"5;10;5\" begin=\"animation.end+0.2s;animation1.end-0.2s\" dur=\"0.3s\" /></rect>",
            replaceIDs(body),
        )
        assertEquals(
            "<rect x=\"2\" y=\"2\" width=\"20\" height=\"10\" fill=\"currentColor\"><animate id=\"animation2\" attributeName=\"height\" values=\"10;5\" dur=\"0.5s\" fill=\"freeze\" /><animate id=\"animation3\" attributeName=\"width\" values=\"20;5\" dur=\"0.2s\" begin=\"animation2.end+1s\" fill=\"freeze\" /></rect><rect x=\"2\" y=\"12\" width=\"20\" height=\"5\" fill=\"currentColor\"><animate attributeName=\"height\" values=\"5;10;5\" begin=\"animation2.end+0.2s;animation3.end-0.2s\" dur=\"0.3s\" /></rect>",
            replaceIDs(body),
        )
    }

    @Test
    fun replacesIDsMatchingTagsAndAttributes() {
        val body =
            "<path d=\"M12 10.9c1.1zM12 8.M12\" fill=\"currentColor\"><animateTransform id=\"d\" attributeName=\"transform\" begin=\"0;M12.end\"/><animateTransform id=\"path\" attributeName=\"transform\" begin=\"d.end\"/><animateTransform id=\"M12\" attributeName=\"transform\" begin=\"path.end\"/></path>"

        clearIDCache()
        assertEquals(
            "<path d=\"M12 10.9c1.1zM12 8.M12\" fill=\"currentColor\"><animateTransform id=\"d\" attributeName=\"transform\" begin=\"0;M.end\"/><animateTransform id=\"path\" attributeName=\"transform\" begin=\"d.end\"/><animateTransform id=\"M\" attributeName=\"transform\" begin=\"path.end\"/></path>",
            replaceIDs(body),
        )
        assertEquals(
            "<path d=\"M12 10.9c1.1zM12 8.M12\" fill=\"currentColor\"><animateTransform id=\"d1\" attributeName=\"transform\" begin=\"0;M1.end\"/><animateTransform id=\"path1\" attributeName=\"transform\" begin=\"d1.end\"/><animateTransform id=\"M1\" attributeName=\"transform\" begin=\"path1.end\"/></path>",
            replaceIDs(body),
        )
    }

    @Test
    fun trimsWhitespace() {
        assertEquals(
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
            trimSVG(
                "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>\n",
            ),
        )
        assertEquals(
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
            trimSVG(
                "\n<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\">\n    <circle cx=\"60\" cy=\"60\" r=\"50\"/>\n</svg>\n",
            ),
        )
        assertEquals(
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
            trimSVG(
                "<svg viewBox=\"0 0 120 120\"\n\t\t\t\txmlns=\"http://www.w3.org/2000/svg\"\n\t\t\t\t><circle cx=\"60\" cy=\"60\" r=\"50\"\n\t\t\t\t/></svg>",
            ),
        )
    }

    @Test
    fun prettifiesSVG() {
        assertEquals(
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\">\n\t<circle cx=\"60\" cy=\"60\" r=\"50\">\n\t</circle>\n</svg>\n",
            prettifySVG(
                "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"></circle></svg>",
            ),
        )
        assertEquals(
            "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\">\n\t<circle cx=\"60\" cy=\"60\" r=\"50\" />\n</svg>\n",
            prettifySVG(
                "<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
            ),
        )
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\">\n\t<circle cx=\"60\" cy=\"60\" r=\"50\" />\n</svg>\n",
            prettifySVG(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg viewBox=\"0 0 120 120\" xmlns=\"http://www.w3.org/2000/svg\">\n<circle cx=\"60\" cy=\"60\" r=\"50\"/>\n</svg>\n\t",
            ),
        )
    }

    @Test
    fun prettifiesTextAndStyle() {
        assertEquals(
            "<svg>\n\t<title>Trimmed Text</title>\n</svg>\n",
            prettifySVG("<svg><title>Trimmed Text</title></svg>"),
        )
        assertEquals(
            "<svg>\n\t<title> Right Trimmed Text</title>\n</svg>\n",
            prettifySVG("<svg><title> Right Trimmed Text</title></svg>"),
        )
        assertEquals(
            "<svg>\n\t<title>Left Trimmed Text </title>\n</svg>\n",
            prettifySVG("<svg><title>Left Trimmed Text </title></svg>"),
        )
        assertEquals(
            "<svg>\n\t<title>\n\t\tText\n\t</title>\n</svg>\n",
            prettifySVG("<svg><title> Text </title></svg>"),
        )
        assertEquals(
            "<svg>\n\t<style>path { stroke-width: 1; }</style>\n</svg>\n",
            prettifySVG("<svg><style>path { stroke-width: 1; }</style></svg>"),
        )
        assertEquals(
            "<svg>\n\t<script lang=\"foo\"> console.log('Alert'); </script>\n</svg>\n",
            prettifySVG("<svg><script lang=\"foo\"> console.log('Alert'); </script></svg>"),
        )
        assertEquals(
            "<svg>\n\t<style>g > path { stroke-width: 1; }</style>\n\t<g>\n\t</g>\n</svg>\n",
            prettifySVG("<svg><style>g > path { stroke-width: 1; }</style><g></g></svg>"),
        )
    }

    @Test
    fun returnsNullForBadCode() {
        assertNull(prettifySVG("<svg><title>Incomplete SVG</title>"))
        assertNull(prettifySVG("<svg><title>Incomplete SVG</title>/svg>"))
        assertNull(prettifySVG("<svg><title>Incomplete SVG</title></g></svg>"))
        assertNull(prettifySVG("<svg><title>Unescaped < text</title></svg>"))
        assertNull(prettifySVG("<svg><title>Unescaped > text</title></svg>"))
        assertNull(prettifySVG("<svg><title foo=\">\">Unescaped attr</title></svg>"))
        assertNull(prettifySVG("<svg><title foo=\"<\">Unescaped attr</title></svg>"))
    }

    @Test
    fun encodesSVGForCSS() {
        assertEquals(
            "%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' width='16' height='16'%3E%3Cpath d='M0 0h16v16z' /%3E%3C/svg%3E",
            encodeSvgForCss(
                "<svg viewBox=\"0 0 16 16\" width=\"16\" height=\"16\"><path d=\"M0 0h16v16z\" /></svg>",
            ),
        )
        assertEquals(
            "%3Csvg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' viewBox='0 0 16 16'%3E%3Cuse xlink:href='%23icon' /%3E%3C/svg%3E",
            encodeSvgForCss("<svg viewBox=\"0 0 16 16\"><use xlink:href=\"#icon\" /></svg>"),
        )
        assertEquals(
            "%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3E%3Cpath d='M0 0h16v16z' /%3E%3C/svg%3E",
            encodeSvgForCss(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 16 16\"><path d=\"M0 0h16v16z\" /></svg>",
            ),
        )
    }

    @Test
    fun generatesDataURL() {
        val html =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" viewBox=\"0 0 16 16\"><path d=\"M0 0h16v16z\" fill=\"#f80\" /></svg>"

        assertEquals(
            "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M0 0h16v16z' fill='%23f80' /%3E%3C/svg%3E",
            svgToData(html),
        )
        assertEquals(
            "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M0 0h16v16z' fill='%23f80' /%3E%3C/svg%3E\")",
            svgToURL(html),
        )
    }

    @Test
    fun cleansUpInnerHTML() {
        val html = "<svg><path d=\"M0 0\"/></svg>"
        assertEquals(html, cleanUpInnerHTML(html).toString())
    }

    private fun assertNotNull(value: dynamic): dynamic {
        kotlin.test.assertNotNull(value)
        return value
    }
}

private fun assertTrue(value: Boolean) {
    kotlin.test.assertTrue(value)
}
