package iconify.utils

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MiscTest {
    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    @Test
    fun convertsStringCases() {
        assertEquals("fooBar", camelize("foo-bar"))
        assertEquals("fooBarBaz", camelize("foo-bar-baz"))
        assertEquals("foo1Bar", camelize("foo1-bar"))
        assertEquals("fooBar", camelize("fooBar"))

        assertEquals("FooBar", pascalize("foo-bar"))
        assertEquals("FooBarBaz", pascalize("foo-bar-baz"))
        assertEquals("FooBar", pascalize("fooBar"))

        assertEquals("foo-bar", camelToKebab("fooBar"))
        assertEquals("foo-bar-baz", camelToKebab("fooBarBaz"))
        assertEquals("foo-bar", camelToKebab("foo:bar"))
        assertEquals("mdi-home", camelToKebab("mdi:home"))
        assertEquals("foo-bar", camelToKebab("fooBar"))

        assertEquals("foo_bar", snakelize("fooBar"))
        assertEquals("foo_bar_baz", snakelize("fooBarBaz"))
        assertEquals("mdi_home", snakelize("mdi-home"))
    }

    @Test
    fun comparesObjects() {
        val obj1 = js("({ a: 1, b: 2 })")
        val obj2 = js("({ a: 1, b: 2 })")
        val obj3 = js("({ a: 1, b: 3 })")
        val obj4 = js("({ a: 1, b: 2, c: 3 })")

        assertEquals(true, compareObjects(obj1, obj2))
        assertEquals(false, compareObjects(obj1, obj3))
        assertEquals(false, compareObjects(obj1, obj4))

        // Reference object limits compared keys, but key count must still match
        assertEquals(true, compareObjects(obj1, js("({ a: 1, b: 3 })"), js("({ a: 1 })")))
        assertEquals(false, compareObjects(obj1, js("({ a: 1 })"), js("({ a: 1 })")))
        assertEquals(false, compareObjects(obj1, obj4, js("({ a: 1 })")))
    }

    @Test
    fun unmergesObjects() {
        val result = unmergeObjects(
            js("({ a: 1, b: 2, c: 3 })"),
            js("({ b: 2, d: 4 })"),
        )

        assertContentEquals(arrayOf("a", "c"), keys(result).sortedArray())
        assertEquals(1.0, result.a.unsafeCast<Double>())
        assertEquals(3.0, result.c.unsafeCast<Double>())

        val unchanged = unmergeObjects(js("({ a: 1 })"), js("({ b: 2 })"))
        assertContentEquals(arrayOf("a"), keys(unchanged))
    }

    @Test
    fun getsCommonObjectProps() {
        val result = commonObjectProps(
            js("({ a: 1, b: 2, c: 3 })"),
            js("({ a: true, c: true, d: true })"),
        )

        assertContentEquals(arrayOf("a", "c"), keys(result).sortedArray())
        assertEquals(1.0, result.a.unsafeCast<Double>())
        assertEquals(3.0, result.c.unsafeCast<Double>())

        // Result has no prototype
        assertEquals("undefined", js("typeof result.hasOwnProperty"))
    }

    @Test
    fun sanitisesTitleAttribute() {
        assertEquals("Hello world", sanitiseTitleAttribute("Hello world"))
        assertEquals("Hello world", sanitiseTitleAttribute("Hello <world>"))
        assertEquals("foo bar  baz", sanitiseTitleAttribute("foo <bar> & baz"))
        assertEquals("abc", sanitiseTitleAttribute("<a>&b<c>"))
        assertEquals("", sanitiseTitleAttribute("<>&"))
    }
}
