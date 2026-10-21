package iconify.utils

import iconify.IconifyIcon
import iconify.IconifyJSON
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals

class CssTest {
    private fun icon(json: String): IconifyIcon = jsObject(json).unsafeCast<IconifyIcon>()

    private fun iconSet(json: String): IconifyJSON = jsObject(json).unsafeCast<IconifyJSON>()

    private fun options(json: String): IconCSSIconOptions =
        jsObject(json).unsafeCast<IconCSSIconOptions>()

    private fun contentOptions(json: String): IconContentIconOptions =
        jsObject(json).unsafeCast<IconContentIconOptions>()

    private fun iconSetOptions(json: String): IconCSSIconSetOptions =
        jsObject(json).unsafeCast<IconCSSIconSetOptions>()

    private fun iconSetContentOptions(json: String): IconContentIconSetOptions =
        jsObject(json).unsafeCast<IconContentIconSetOptions>()

    private fun svgURL(body: String, viewBox: String, width: String, height: String): String =
        svgToURL(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"$viewBox\" width=\"$width\" height=\"$height\">$body</svg>",
        )

    @Test
    fun generatesBackgroundCSS() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f80\" />"
        val icon = icon("{ body: '$body', width: 24, height: 16 }")
        val expectedURL = svgURL(body, "0 0 24 16", "24", "16")

        assertEquals(
            ".icon {\n" +
                "  display: inline-block;\n" +
                "  width: 1.5em;\n" +
                "  height: 1em;\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: 100% 100%;\n" +
                "  background-image: $expectedURL;\n" +
                "}\n",
            getIconCSS(icon, options("{ format: 'expanded' }")),
        )
    }

    @Test
    fun appliesTransformationsToBackgroundCSS() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f80\" />"
        val icon = icon("{ body: '$body', rotate: 1, width: 24, height: 16 }")
        val expectedURL = svgURL(
            "<g transform=\"rotate(90 8 8)\">$body</g>",
            "0 0 16 24",
            "16",
            "24",
        )

        assertEquals(
            ".icon {\n" +
                "  display: inline-block;\n" +
                "  width: 0.67em;\n" +
                "  height: 1em;\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: 100% 100%;\n" +
                "  background-image: $expectedURL;\n" +
                "}\n",
            getIconCSS(icon, options("{ format: 'expanded' }")),
        )
    }

    @Test
    fun appliesBackgroundOptions() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f80\" />"
        val icon = icon("{ body: '$body', width: 24, height: 16 }")
        val expectedURL = svgURL(
            body.replace("f80", "fff"),
            "0 -4 24 24",
            "24",
            "24",
        )

        val options = options(
            "{ iconSelector: '.test-icon:after', pseudoSelector: true, varName: 'svg', forceSquare: true, format: 'expanded', rules: { visibility: 'visible' } }",
        )
        options.customise = { content -> content.replace("f80", "fff") }

        assertEquals(
            ".test-icon:after {\n" +
                "  visibility: visible;\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  content: '';\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: 100% 100%;\n" +
                "  background-image: $expectedURL;\n" +
                "}\n",
            getIconCSS(icon, options),
        )
    }

    @Test
    fun generatesMaskCSS() {
        val body =
            "<path d=\"M0 0h16v16z\" fill=\"currentColor\" stroke=\"currentColor\" stroke-width=\"1\" />"
        val icon = icon("{ body: '$body' }")
        val expectedURL = svgURL(body.replace("currentColor", "black"), "0 0 16 16", "16", "16")

        assertEquals(
            ".icon {\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-color: currentColor;\n" +
                "  -webkit-mask-image: var(--svg);\n" +
                "  mask-image: var(--svg);\n" +
                "  -webkit-mask-repeat: no-repeat;\n" +
                "  mask-repeat: no-repeat;\n" +
                "  -webkit-mask-size: 100% 100%;\n" +
                "  mask-size: 100% 100%;\n" +
                "  --svg: $expectedURL;\n" +
                "}\n",
            getIconCSS(icon, options("{ format: 'expanded' }")),
        )
    }

    @Test
    fun replacesColorInCSS() {
        val body =
            "<path d=\"M0 0h16v16z\" fill=\"currentColor\" stroke=\"currentColor\" stroke-width=\"1\" />"
        val icon = icon("{ body: '$body' }")
        val expectedURL = svgURL(body.replace("currentColor", "purple"), "0 0 16 16", "16", "16")

        val expected =
            ".icon {\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: 100% 100%;\n" +
                "  background-image: $expectedURL;\n" +
                "}\n"

        assertEquals(
            expected,
            getIconCSS(icon, options("{ format: 'expanded', color: 'purple' }")),
        )

        val customised = options("{ format: 'expanded' }")
        customised.customise = { content -> content.replace("currentColor", "purple") }
        assertEquals(expected, getIconCSS(icon, customised))
    }

    @Test
    fun generatesInlineMaskCSS() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f00\" />"
        val icon = icon("{ body: '$body' }")
        val expectedURL = svgURL(body, "0 0 16 16", "16", "16")

        assertEquals(
            ".icon {\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-color: currentColor;\n" +
                "  -webkit-mask-repeat: no-repeat;\n" +
                "  mask-repeat: no-repeat;\n" +
                "  -webkit-mask-size: 100% 100%;\n" +
                "  mask-size: 100% 100%;\n" +
                "  -webkit-mask-image: $expectedURL;\n" +
                "  mask-image: $expectedURL;\n" +
                "}\n",
            getIconCSS(icon, options("{ format: 'expanded', varName: null, mode: 'mask' }")),
        )
    }

    @Test
    fun generatesContentCSS() {
        val body = "<path d=\"M0 0h16v16z\" fill=\"#f80\" />"
        val icon = icon("{ body: '$body', width: 24, height: 16 }")
        val expectedURL = svgURL(body, "0 0 24 16", "48", "32")

        assertEquals(
            ".icon::after {\n  content: $expectedURL;\n}\n",
            getIconContentCSS(icon, contentOptions("{ height: 32, format: 'expanded' }")),
        )
    }

    @Test
    fun generatesContentCSSWithOptions() {
        val body =
            "<path d=\"M0 0h16v16z\" fill=\"currentColor\" stroke=\"currentColor\" stroke-width=\"1\" />"
        val icon = icon("{ body: '$body' }")
        val expectedURL = svgURL(body.replace("currentColor", "purple"), "0 0 16 16", "32", "24")

        assertEquals(
            ".test-icon::before {\n  visibility: visible;\n  content: $expectedURL;\n}\n",
            getIconContentCSS(
                icon,
                contentOptions(
                    "{ width: 32, height: 24, format: 'expanded', color: 'purple', iconSelector: '.test-icon::before', rules: { visibility: 'visible' } }",
                ),
            ),
        )
    }

    @Test
    fun appliesTransformationsToContentCSS() {
        val body =
            "<path d=\"M0 0h16v16z\" fill=\"currentColor\" stroke=\"currentColor\" stroke-width=\"1\" />"
        val icon = icon("{ body: '$body', hFlip: true }")
        val expectedURL = svgURL(
            "<g transform=\"translate(16 0) scale(-1 1)\">${body.replace("currentColor", "black")}</g>",
            "0 0 16 16",
            "24",
            "24",
        )

        assertEquals(
            ".test-icon::before {\n  content: $expectedURL;\n}\n",
            getIconContentCSS(
                icon,
                contentOptions("{ height: 24, format: 'expanded', iconSelector: '.test-icon::before' }"),
            ),
        )
    }

    private val monotoneSet = iconSet(
        "{ prefix: 'test-prefix', icons: { " +
            "'activity': { body: '<path fill=\"currentColor\" d=\"M6 2Z\"/>' }, " +
            "'123': { body: '<path fill=\"currentColor\" d=\"M2 3Z\"/>' }, " +
            "'empty': { body: '<g />' } } }",
    )

    @Test
    fun generatesCSSForIconSet() {
        val activity = "<path fill=\"currentColor\" d=\"M6 2Z\"/>"
        val expectedURL = svgURL(activity.replace("currentColor", "black"), "0 0 16 16", "16", "16")

        assertEquals(
            ".icon--test-prefix {\n" +
                "  visibility: visible;\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-color: currentColor;\n" +
                "  -webkit-mask-image: var(--svg);\n" +
                "  mask-image: var(--svg);\n" +
                "  -webkit-mask-repeat: no-repeat;\n" +
                "  mask-repeat: no-repeat;\n" +
                "  -webkit-mask-size: 100% 100%;\n" +
                "  mask-size: 100% 100%;\n" +
                "}\n" +
                "\n" +
                ".icon--test-prefix--activity {\n" +
                "  --svg: $expectedURL;\n" +
                "}\n" +
                "\n" +
                "/* Could not find icon: missing */\n",
            getIconsCSS(
                monotoneSet,
                arrayOf("activity", "missing"),
                iconSetOptions("{ format: 'expanded', rules: { visibility: 'visible' } }"),
            ),
        )
    }

    @Test
    fun generatesBackgroundCSSForIconSet() {
        val body = "<path fill=\"currentColor\" d=\"M2 3Z\"/>"
        val expectedURL = svgURL(body.replace("currentColor", "black"), "0 0 16 16", "16", "16")

        val options = iconSetOptions("{ format: 'expanded', mode: 'background' }")
        options.customise = { content, name ->
            if (name == "123") "<path fill=\"currentColor\" d=\"M2 3Z\"/>" else content
        }

        assertEquals(
            ".icon--test-prefix {\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-repeat: no-repeat;\n" +
                "  background-size: 100% 100%;\n" +
                "}\n" +
                "\n" +
                ".icon--test-prefix--123 {\n" +
                "  background-image: $expectedURL;\n" +
                "}\n",
            getIconsCSS(monotoneSet, arrayOf("123"), options),
        )
    }

    @Test
    fun generatesCSSWithInlineMaskData() {
        val body = "<path fill=\"currentColor\" d=\"M6 2Z\"/>"

        val set = iconSet(
            "{ prefix: 'bi', width: 24, icons: { 'activity': { body: '<path fill=\"currentColor\" d=\"M6 2Z\"/>' } } }",
        )
        val expectedActivityURL = svgURL(
            body.replace("currentColor", "black"),
            "0 0 24 16",
            "24",
            "16",
        )

        assertEquals(
            ".icon--bi {\n" +
                "  visibility: visible;\n" +
                "  display: inline-block;\n" +
                "  width: 1em;\n" +
                "  height: 1em;\n" +
                "  background-color: currentColor;\n" +
                "  -webkit-mask-repeat: no-repeat;\n" +
                "  mask-repeat: no-repeat;\n" +
                "  -webkit-mask-size: 100% 100%;\n" +
                "  mask-size: 100% 100%;\n" +
                "}\n" +
                "\n" +
                ".icon--bi.icon--bi--activity {\n" +
                "  width: 1.5em;\n" +
                "  -webkit-mask-image: $expectedActivityURL;\n" +
                "  mask-image: $expectedActivityURL;\n" +
                "}\n",
            getIconsCSS(
                set,
                arrayOf("activity"),
                iconSetOptions("{ format: 'expanded', varName: null, rules: { visibility: 'visible' } }"),
            ),
        )
    }

    @Test
    fun generatesCSSWithCustomSelector() {
        val set = iconSet(
            "{ prefix: 'bi', width: 24, icons: { 'activity': { body: '<path fill=\"currentColor\" d=\"M6 2Z\"/>' } } }",
        )
        val expectedURL = svgURL(
            "<path fill=\"currentColor\" d=\"M6 2Z\"/>".replace("currentColor", "black"),
            "0 0 24 16",
            "24",
            "16",
        )

        assertEquals(
            ".test--activity {\n" +
                "  display: inline-block;\n" +
                "  width: 1.5em;\n" +
                "  height: 1em;\n" +
                "  background-color: currentColor;\n" +
                "  -webkit-mask-image: var(--svg);\n" +
                "  mask-image: var(--svg);\n" +
                "  -webkit-mask-repeat: no-repeat;\n" +
                "  mask-repeat: no-repeat;\n" +
                "  -webkit-mask-size: 100% 100%;\n" +
                "  mask-size: 100% 100%;\n" +
                "  --svg: $expectedURL;\n" +
                "}\n",
            getIconsCSS(
                set,
                arrayOf("activity"),
                iconSetOptions("{ format: 'expanded', iconSelector: '.test--{name}' }"),
            ),
        )
    }

    @Test
    fun generatesContentCSSForIconSet() {
        val body = "<path fill=\"currentColor\" d=\"M6 2Z\"/>"
        val expectedURL = svgURL(body.replace("currentColor", "black"), "0 0 16 16", "16", "16")

        assertEquals(
            ".icon--test-prefix--activity::after {\n" +
                "  display: inline-block;\n" +
                "  content: $expectedURL;\n" +
                "}\n" +
                "\n" +
                "/* Could not find icon: whatever */\n",
            getIconsContentCSS(
                monotoneSet,
                arrayOf("activity", "whatever"),
                iconSetContentOptions(
                    "{ height: 16, format: 'expanded', rules: { display: 'inline-block' } }",
                ),
            ),
        )
    }
}
