package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyIcon
import iconify.IconifyJSON
import iconify.IconifyTransformations
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class IconTest {
    private fun name(value: String, validate: Boolean = false, allowSimpleName: Boolean = false): IconifyIconName? =
        stringToIcon(value, validate, allowSimpleName)

    private fun assertName(
        expectedProvider: String,
        expectedPrefix: String,
        expectedName: String,
        actual: IconifyIconName?,
    ) {
        val name = assertNotNull(actual)
        assertEquals(expectedProvider, name.provider)
        assertEquals(expectedPrefix, name.prefix)
        assertEquals(expectedName, name.name)
    }

    @Test
    fun parsesSimpleIconNames() {
        assertName("", "fa", "home", name("fa-home"))
        assertTrue(validateIconName(name("fa-home")))

        assertName("", "fa", "arrow-left", name("fa:arrow-left"))
        assertTrue(validateIconName(name("fa:arrow-left")))

        assertName("", "mdi-light", "home-outline", name("mdi-light:home-outline"))
        assertTrue(validateIconName(name("mdi-light:home-outline")))

        assertNull(name("home"))
        assertFalse(validateIconName(null))

        assertName("", "", "home", name("home", allowSimpleName = true))
        assertFalse(validateIconName(name("home", allowSimpleName = true)))
        assertTrue(validateIconName(name("home", allowSimpleName = true), true))

        assertNull(name("@iconify-home-icon"))
        assertNull(name("@iconify-home-icon", allowSimpleName = true))

        assertNull(name("fa_home"))
        assertName("", "", "fa_home", name("fa_home", allowSimpleName = true))
        assertFalse(validateIconName(name("fa_home", allowSimpleName = true)))
        assertTrue(validateIconName(name("fa_home", allowSimpleName = true), true))

        assertName("", "fa", "home_outline", name("fa:home_outline"))
        assertTrue(validateIconName(name("fa:home_outline")))

        assertNull(name("mdi:light:home:outline"))

        assertName("", "MD", "Home", name("MD:Home"))
        assertTrue(validateIconName(name("MD:Home")))

        assertName("", "1", "foo", name("1:foo"))
        assertTrue(validateIconName(name("1:foo")))

        assertName("", "md", "fõö", name("md-fõö"))
        assertTrue(validateIconName(name("md-fõö")))
    }

    @Test
    fun parsesProviders() {
        assertName("iconify", "fa", "home", name("@iconify:fa-home"))
        assertTrue(validateIconName(name("@iconify:fa-home")))

        assertName("iconify", "fa", "arrow-left", name("@iconify:fa:arrow-left"))
        assertTrue(validateIconName(name("@iconify:fa:arrow-left")))

        assertName(
            "iconify-backup",
            "mdi-light",
            "home-outline",
            name("@iconify-backup:mdi-light:home-outline"),
        )
        assertTrue(validateIconName(name("@iconify-backup:mdi-light:home-outline")))

        assertName(
            "iconify-backup",
            "mdi-light",
            "home-outline",
            name("iconify-backup:mdi-light:home-outline"),
        )
        assertTrue(validateIconName(name("iconify-backup:mdi-light:home-outline")))

        assertNull(name("@mdi:light:home:outline"))
        assertFalse(validateIconName(null))

        assertName("MD", "home", "outline", name("@MD:home-outline"))
        assertTrue(validateIconName(name("@MD:home-outline")))

        // Default provider
        assertName("custom", "fa", "home", stringToIcon("fa-home", false, false, "custom"))
        assertName("custom", "fa", "home", stringToIcon("fa:home", false, false, "custom"))
        assertNull(stringToIcon("home", false, false, "custom"))
    }

    @Test
    fun matchesValidIconNameParts() {
        assertTrue(matchIconName.test("home"))
        assertTrue(matchIconName.test("arrow-left"))
        assertTrue(matchIconName.test("home2"))
        assertTrue(matchIconName.test("2-home"))

        assertFalse(matchIconName.test("Home"))
        assertFalse(matchIconName.test("home_icon"))
        assertFalse(matchIconName.test("home--icon"))
        assertFalse(matchIconName.test("-home"))
        assertFalse(matchIconName.test("home-"))
    }

    @Test
    fun exposesDefaults() {
        assertEquals(0.0, defaultIconDimensions.unsafeCast<dynamic>().left.unsafeCast<Double>())
        assertEquals(0.0, defaultIconDimensions.unsafeCast<dynamic>().top.unsafeCast<Double>())
        assertEquals(16.0, defaultIconDimensions.unsafeCast<dynamic>().width.unsafeCast<Double>())
        assertEquals(16.0, defaultIconDimensions.unsafeCast<dynamic>().height.unsafeCast<Double>())

        assertEquals(0.0, defaultIconTransformations.rotate)
        assertEquals(false, defaultIconTransformations.hFlip)
        assertEquals(false, defaultIconTransformations.vFlip)

        assertEquals(16.0, defaultIconProps.unsafeCast<dynamic>().width.unsafeCast<Double>())
        assertEquals(
            false,
            defaultIconProps.unsafeCast<dynamic>().hFlip.unsafeCast<Boolean>(),
        )

        assertEquals("", defaultExtendedIconProps.unsafeCast<dynamic>().body.unsafeCast<String>())
        assertEquals(
            false,
            defaultExtendedIconProps.unsafeCast<dynamic>().hidden.unsafeCast<Boolean>(),
        )
        assertEquals(
            16.0,
            defaultExtendedIconProps.unsafeCast<dynamic>().height.unsafeCast<Double>(),
        )
    }

    @Test
    fun getsIconData() {
        val simple = assertNotNull(
            getIconData(
                js("({ prefix: 'foo', icons: { bar: { body: '<g />', width: 24 } } })")
                    .unsafeCast<IconifyJSON>(),
                "bar",
            ),
        )
        assertEquals("<g />", simple.body)
        assertEquals(24.0, simple.width)
        assertNull(simple.height)

        val minified = assertNotNull(
            getIconData(
                js(
                    """({
                        prefix: 'test_set',
                        icons: { test_icon: { body: '<g />' } },
                        width: 24,
                        height: 24
                    })""",
                ).unsafeCast<IconifyJSON>(),
                "test_icon",
            ),
        )
        assertEquals("<g />", minified.body)
        assertEquals(24.0, minified.width)
        assertEquals(24.0, minified.height)

        val alias = assertNotNull(
            getIconData(
                js(
                    """({
                        prefix: 'test_set',
                        icons: { test_icon: { body: '<g />' } },
                        aliases: { test_alias: { parent: 'test_icon', rotate: 2 } },
                        width: 24,
                        height: 24
                    })""",
                ).unsafeCast<IconifyJSON>(),
                "test_alias",
            ),
        )
        assertEquals("<g />", alias.body)
        assertEquals(24.0, alias.width)
        assertEquals(24.0, alias.height)
        assertEquals(2.0, alias.rotate)

        val iconSet = js(
            """({
                prefix: 'test_set',
                icons: { base: { body: '<g />' } },
                aliases: { first: { parent: 'base', rotate: 1 }, second: { parent: 'first', hFlip: true } }
            })""",
        ).unsafeCast<IconifyJSON>()
        val chained = assertNotNull(getIconData(iconSet, "second"))
        assertEquals("<g />", chained.body)
        assertEquals(1.0, chained.rotate)
        assertEquals(true, chained.hFlip)

        assertNull(getIconData(iconSet, "missing"))
        assertNull(
            getIconData(
                js("({ prefix: 'test', icons: {}, aliases: { bad: { parent: 'missing' } } })")
                    .unsafeCast<IconifyJSON>(),
                "bad",
            ),
        )
    }

    private fun icon(json: String): ExtendedIconifyIcon =
        jsObject(json).unsafeCast<ExtendedIconifyIcon>()

    @Test
    fun mergesIconData() {
        val nothing = mergeIconData(icon("({ body: '<g />' })"), js("({})"))
        assertEquals("<g />", nothing.body)
        assertNull(nothing.width)
        assertNull(nothing.rotate)

        val full = mergeIconData(
            icon(
                """({
                    body: '<g />', width: 24, height: 24, left: 0, top: 0,
                    rotate: 0, hFlip: false, vFlip: false
                })""",
            ),
            js("({})"),
        )
        assertEquals("<g />", full.body)
        assertEquals(24.0, full.width)
        assertEquals(24.0, full.height)
        assertEquals(0.0, full.left)
        assertEquals(0.0, full.top)
        assertEquals(0.0, full.rotate)
        assertEquals(false, full.hFlip)
        assertEquals(false, full.vFlip)

        val copy = mergeIconData(
            icon("({ body: '<g />', width: 24 })"),
            js("({ height: 32 })"),
        )
        assertEquals("<g />", copy.body)
        assertEquals(24.0, copy.width)
        assertEquals(32.0, copy.height)

        val override = mergeIconData(
            icon("({ body: '<g />', width: 24, height: 24 })"),
            js("({ height: 32 })"),
        )
        assertEquals(24.0, override.width)
        assertEquals(32.0, override.height)

        val transformations = mergeIconData(
            icon("({ body: '<g />', width: 24, height: 24, hFlip: true, rotate: 3 })"),
            js("({ height: 32, vFlip: true, rotate: 2 })"),
        )
        assertEquals("<g />", transformations.body)
        assertEquals(24.0, transformations.width)
        assertEquals(32.0, transformations.height)
        assertEquals(true, transformations.hFlip)
        assertEquals(true, transformations.vFlip)
        assertEquals(1.0, transformations.rotate)
    }

    @Test
    fun mergesWithDefaults() {
        val merged = mergeIconData(
            defaultExtendedIconProps.unsafeCast<ExtendedIconifyIcon>(),
            js("({ body: '<g />' })"),
        )

        assertEquals("<g />", merged.body)
        assertEquals(0.0, merged.left)
        assertEquals(0.0, merged.top)
        assertEquals(16.0, merged.width)
        assertEquals(16.0, merged.height)
        assertEquals(0.0, merged.rotate)
        assertEquals(false, merged.hFlip)
        assertEquals(false, merged.vFlip)
        assertEquals(false, merged.hidden)
    }

    private fun transformations(json: String): IconifyTransformations =
        jsObject(json).unsafeCast<IconifyTransformations>()

    @Test
    fun mergesTransformations() {
        val nothing = mergeIconTransformations(transformations("({})"), transformations("({})"))
        assertNull(nothing.hFlip)
        assertNull(nothing.vFlip)
        assertNull(nothing.rotate)

        val flips = mergeIconTransformations(
            transformations("({ hFlip: true })"),
            transformations("({ vFlip: true })"),
        )
        assertEquals(true, flips.hFlip)
        assertEquals(true, flips.vFlip)
        assertNull(flips.rotate)

        val cancelled = mergeIconTransformations(
            transformations("({ hFlip: true })"),
            transformations("({ hFlip: true })"),
        )
        assertNull(cancelled.hFlip)

        val rotation = mergeIconTransformations(
            transformations("({ rotate: 3 })"),
            transformations("({ rotate: 2 })"),
        )
        assertEquals(1.0, rotation.rotate)

        val fullRotation = mergeIconTransformations(
            transformations("({ rotate: 2 })"),
            transformations("({ rotate: 2 })"),
        )
        assertNull(fullRotation.rotate)
    }

    @Test
    fun makesIconsSquare() {
        val square = js("({ body: '<g />', left: 0, top: 0, width: 24, height: 24, rotate: 0, hFlip: false, vFlip: false })")
            .unsafeCast<FullIconifyIcon>()
        assertSame(square, makeIconSquare(square))

        val rectangle = js("({ body: '<g />', left: 0, top: 0, width: 24, height: 16, rotate: 0, hFlip: false, vFlip: false })")
            .unsafeCast<FullIconifyIcon>()
        val result = makeIconSquare(rectangle)
        assertEquals(24.0, result.width)
        assertEquals(24.0, result.height)
        assertEquals(0.0, result.left)
        assertEquals(-4.0, result.top)
        assertEquals("<g />", result.body)
    }

    @Test
    fun makesViewBoxesSquare() {
        val square = arrayOf(0.0, 0.0, 24.0, 24.0)
        assertSame(square, makeViewBoxSquare(square))

        val wide = makeViewBoxSquare(arrayOf(0.0, 0.0, 24.0, 16.0))
        assertContentEquals(arrayOf(0.0, -4.0, 24.0, 24.0), wide)

        val offset = makeViewBoxSquare(arrayOf(1.0, 2.0, 20.0, 10.0))
        assertContentEquals(arrayOf(1.0, -3.0, 20.0, 20.0), offset)
    }

    @Test
    fun keepsBodyOfIcon() {
        val icon = js("({ body: '<path/>' })").unsafeCast<IconifyIcon>()
        assertEquals("<path/>", icon.body)
    }
}
