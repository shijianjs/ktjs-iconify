package iconify.tools

import iconify.IconifyJSON
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IconSetMetaTest {
    private fun iconSet(source: String): IconSet =
        IconSet(jsObject(source).unsafeCast<IconifyJSON>())

    private fun addIcon(iconSet: IconSet, name: String) {
        iconSet.setIcon(
            name,
            jsObject("{body: '<g id=\"$name\" />'}").unsafeCast<ResolvedIconifyIcon>(),
        )
    }

    @Test
    fun addsNoTagsToEmptyIconSet() {
        val iconSet = iconSet(
            """{prefix: 'foo', icons: {foo: {body: '<g/>', hidden: true}}}"""
        )

        assertEquals(0, addTagsToIconSet(iconSet).size)
    }

    @Test
    fun addsGridAndSquareTags() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                info: {name: 'Test', author: {name: 'Test'}, license: {title: 'MIT'}},
                icons: {
                    foo: {body: '<g/>'},
                    bar: {body: '<g/>'},
                    hidden: {body: '<g/>', hidden: true, width: 24, height: 32},
                },
            }"""
        )

        val tags = addTagsToIconSet(iconSet)
        assertEquals(listOf("Grid: 16", "Square"), tags.toList())
        assertJsObject(
            """{
                prefix: 'foo',
                info: {
                    name: 'Test',
                    author: {name: 'Test'},
                    license: {title: 'MIT'},
                    total: 2,
                    tags: ['Grid: 16', 'Square'],
                },
                icons: {
                    foo: {body: '<g/>'},
                    bar: {body: '<g/>'},
                    hidden: {body: '<g/>', hidden: true, width: 24, height: 32},
                },
            }""",
            iconSet.export(),
        )
    }

    @Test
    fun addsHeightTag() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    foo: {body: '<g/>', width: 24, height: 24},
                    bar: {body: '<g/>', width: 20, height: 24},
                },
            }"""
        )

        assertEquals(listOf("Height: 24"), addTagsToIconSet(iconSet).toList())
    }

    @Test
    fun usesPaletteFromInfo() {
        val monotone = iconSet(
            """{
                prefix: 'foo',
                info: {name: 'Foo', author: {name: 'Whatever'}, license: {title: 'MIT'}, palette: false},
                icons: {
                    foo: {body: '<g/>', width: 16, height: 16},
                    bar: {body: '<g/>', width: 24, height: 32},
                },
            }"""
        )
        assertEquals(listOf("Monotone"), addTagsToIconSet(monotone).toList())

        val palette = iconSet(
            """{
                prefix: 'foo',
                info: {name: 'Foo', author: {name: 'Whatever'}, license: {title: 'MIT'}, palette: true},
                icons: {
                    foo: {body: '<g/>', width: 16, height: 16},
                    bar: {body: '<g/>', width: 24, height: 32},
                },
            }"""
        )
        assertEquals(listOf("Has Colors"), addTagsToIconSet(palette).toList())
    }

    @Test
    fun detectsPalette() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    foo: {body: '<g fill="red" />', width: 16, height: 16},
                    bar: {body: '<g fill="green" />', width: 24, height: 32},
                },
            }"""
        )

        assertEquals(listOf("Has Colors"), addTagsToIconSet(iconSet).toList())
    }

    @Test
    fun checksSuffixThemes() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {},
                suffixes: {line: 'Line', bold: 'Bold', '': 'Regular'},
            }"""
        )

        val shortNames = listOf("alarm", "home", "app")
        val allNames = mutableListOf<String>()
        for (name in shortNames) {
            for (suffix in listOf("-line", "-bold", "")) {
                addIcon(iconSet, name + suffix)
                allNames.add(name + suffix)
            }
        }

        assertEquals(9, iconSet.count())

        val prefixes = iconSet.checkTheme(true)
        assertJsObject("{}", prefixes.valid)
        assertEquals(allNames, prefixes.invalid.toList())

        val suffixes = iconSet.checkTheme(false)
        assertJsObject(
            """{
                bold: ['alarm-bold', 'home-bold', 'app-bold'],
                line: ['alarm-line', 'home-line', 'app-line'],
                '': ['alarm', 'home', 'app'],
            }""",
            suffixes.valid,
        )
        assertEquals(0, suffixes.invalid.size)

        val exported = iconSet.export()
        assertTrue(exported.prefixes == null)
        assertJsObject("{line: 'Line', bold: 'Bold', '': 'Regular'}", exported.suffixes)
        assertEquals(listOf("line", "bold", ""), jsKeys(exported.suffixes).toList())
    }

    @Test
    fun checksPrefixThemes() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {},
                prefixes: {line: 'Line', bold: 'Bold', solid: 'Solid'},
            }"""
        )

        val shortNames = listOf("alarm", "home", "app")
        val allNames = mutableListOf<String>()
        for (name in shortNames) {
            for (prefix in listOf("line-", "bold-", "twotone-", "")) {
                addIcon(iconSet, prefix + name)
                allNames.add(prefix + name)
            }
        }

        assertEquals(12, iconSet.count())

        val invalid = mutableListOf<String>()
        for (name in shortNames) {
            invalid.add("twotone-$name")
            invalid.add(name)
        }
        val prefixes = iconSet.checkTheme(true)
        assertJsObject(
            """{
                bold: ['bold-alarm', 'bold-home', 'bold-app'],
                line: ['line-alarm', 'line-home', 'line-app'],
                solid: [],
            }""",
            prefixes.valid,
        )
        assertEquals(invalid, prefixes.invalid.toList())

        val suffixes = iconSet.checkTheme(false)
        assertJsObject("{}", suffixes.valid)
        assertEquals(allNames, suffixes.invalid.toList())

        val exported = iconSet.export()
        assertTrue(exported.suffixes == null)
        assertJsObject("{line: 'Line', bold: 'Bold'}", exported.prefixes)
        assertEquals(listOf("line", "bold"), jsKeys(exported.prefixes).toList())
    }

    @Test
    fun convertsLegacyThemes() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {},
                themes: {
                    test1: {title: '24 Outline', prefix: '24-outline-'},
                    test2: {title: '24', prefix: '24-'},
                    test3: {title: '24 Solid', prefix: '24-solid-'},
                    invalid: {title: '24 TwoTone', prefix: '24-twotone'},
                },
            }"""
        )

        val shortNames = listOf("alarm", "home", "app")
        val allNames = mutableListOf<String>()
        for (name in shortNames) {
            for (prefix in listOf("24-outline-", "24-twotone-", "24-", "20-")) {
                addIcon(iconSet, prefix + name)
                allNames.add(prefix + name)
            }
        }

        assertEquals(12, iconSet.count())

        val suffixes = iconSet.checkTheme(false)
        assertJsObject("{}", suffixes.valid)
        assertEquals(allNames, suffixes.invalid.toList())

        val other24 = mutableListOf<String>()
        for (name in shortNames) {
            other24.add("24-twotone-$name")
            other24.add("24-$name")
        }
        val prefixes = iconSet.checkTheme(true)
        assertJsObject(
            """{
                '24-outline': ['24-outline-alarm', '24-outline-home', '24-outline-app'],
                '24-solid': [],
                '24': ['24-twotone-alarm', '24-alarm', '24-twotone-home', '24-home', '24-twotone-app', '24-app'],
            }""",
            prefixes.valid,
        )
        assertEquals(listOf("20-alarm", "20-home", "20-app"), prefixes.invalid.toList())

        val exported = iconSet.export()
        assertTrue(exported.suffixes == null)
        assertJsObject("{'24-outline': '24 Outline', '24': '24'}", exported.prefixes)
    }
}
