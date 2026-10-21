package iconify.utils

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CustomisationsTest {
    private fun customisations(json: String): IconifyIconCustomisations =
        jsObject(json).unsafeCast<IconifyIconCustomisations>()

    @Test
    fun exposesDefaults() {
        val customisations = defaultIconCustomisations
        assertNull(customisations.width.unsafeCast<Any?>())
        assertNull(customisations.height.unsafeCast<Any?>())
        assertEquals(0.0, customisations.rotate)
        assertEquals(false, customisations.hFlip)
        assertEquals(false, customisations.vFlip)

        assertEquals(null, defaultIconSizeCustomisations.unsafeCast<dynamic>().width as Any?)
        assertEquals(null, defaultIconSizeCustomisations.unsafeCast<dynamic>().height as Any?)
    }

    @Test
    fun mergesEmptyCustomisations() {
        val merged = mergeCustomisations(defaultIconCustomisations, customisations("({})"))

        assertEquals(null, merged.width.unsafeCast<Any?>())
        assertEquals(null, merged.height.unsafeCast<Any?>())
        assertEquals(0.0, merged.rotate)
        assertEquals(false, merged.hFlip)
        assertEquals(false, merged.vFlip)
    }

    @Test
    fun mergesFlips() {
        val merged = mergeCustomisations(defaultIconCustomisations, customisations("({ hFlip: true })"))

        assertEquals(true, merged.hFlip)
        assertEquals(false, merged.vFlip)
    }

    @Test
    fun normalisesRotation() {
        val merged = mergeCustomisations(defaultIconCustomisations, customisations("({ rotate: 10 })"))
        assertEquals(2.0, merged.rotate)

        val negative = mergeCustomisations(
            defaultIconCustomisations,
            customisations("({ rotate: -1 })"),
        )
        assertEquals(-1.0, negative.rotate)
    }

    @Test
    fun mergesDimensions() {
        val merged = mergeCustomisations(
            defaultIconCustomisations,
            customisations("({ width: '1em', height: 20 })"),
        )

        assertEquals("1em", merged.width.unsafeCast<String>())
        assertEquals(20.0, merged.height.unsafeCast<Double>())
    }

    @Test
    fun ignoresValuesWithWrongType() {
        val merged = mergeCustomisations(
            defaultIconCustomisations,
            js("({ width: true, height: {}, rotate: '1', hFlip: 1 })")
                .unsafeCast<IconifyIconCustomisations>(),
        )

        assertEquals(null, merged.width.unsafeCast<Any?>())
        assertEquals(null, merged.height.unsafeCast<Any?>())
        assertEquals(0.0, merged.rotate)
        assertEquals(false, merged.hFlip)
    }

    @Test
    fun appliesFlipString() {
        val custom = customisations("({})")
        flipFromString(custom, "horizontal")
        assertEquals(true, custom.hFlip)
        assertNull(custom.vFlip)

        val vertical = customisations("({})")
        flipFromString(vertical, "vertical")
        assertNull(vertical.hFlip)
        assertEquals(true, vertical.vFlip)

        val both = customisations("({})")
        flipFromString(both, "horizontal,vertical")
        assertEquals(true, both.hFlip)
        assertEquals(true, both.vFlip)

        val spaces = customisations("({})")
        flipFromString(spaces, "vertical  horizontal")
        assertEquals(true, spaces.hFlip)
        assertEquals(true, spaces.vFlip)

        val ignored = customisations("({})")
        flipFromString(ignored, "diagonal")
        assertNull(ignored.hFlip)
        assertNull(ignored.vFlip)
    }

    @Test
    fun parsesRotationStrings() {
        assertEquals(0, rotateFromString("0"))
        assertEquals(1, rotateFromString("1"))
        assertEquals(2, rotateFromString("2"))
        assertEquals(3, rotateFromString("3"))
        assertEquals(0, rotateFromString("4"))
        assertEquals(1, rotateFromString("5"))

        assertEquals(3, rotateFromString("-1"))
        assertEquals(2, rotateFromString("-2"))

        assertEquals(1, rotateFromString("90deg"))
        assertEquals(2, rotateFromString("180deg"))
        assertEquals(3, rotateFromString("270deg"))
        assertEquals(0, rotateFromString("360deg"))
        assertEquals(1, rotateFromString("25%"))
        assertEquals(2, rotateFromString("50%"))
        assertEquals(3, rotateFromString("75%"))

        assertEquals(0, rotateFromString("45deg"))
        assertEquals(0, rotateFromString("45%"))
        assertEquals(0, rotateFromString("45deg", 2))
        assertEquals(2, rotateFromString("foo", 2))
        assertEquals(0, rotateFromString("", 2))
    }

    @Test
    fun convertsBooleans() {
        assertEquals(true, toBoolean("data-icon", true, false))
        assertEquals(false, toBoolean("data-icon", false, true))
        assertEquals(true, toBoolean("data-icon", 1, false))
        assertEquals(false, toBoolean("data-icon", 0, true))

        assertEquals(true, toBoolean("data-icon", "1", false))
        assertEquals(true, toBoolean("data-icon", "true", false))
        assertEquals(true, toBoolean("data-icon", "TRUE", false))
        assertEquals(true, toBoolean("data-icon", "data-icon", false))
        assertEquals(true, toBoolean("data-icon", "DATA-ICON", false))

        assertEquals(false, toBoolean("data-icon", "0", true))
        assertEquals(false, toBoolean("data-icon", "false", true))
        assertEquals(false, toBoolean("data-icon", "", true))

        assertEquals(true, toBoolean("data-icon", "yes", true))
        assertEquals(false, toBoolean("data-icon", "yes", false))
        assertEquals(true, toBoolean("data-icon", null, true))
        assertEquals(false, toBoolean("data-icon", js("undefined"), false))
    }
}
