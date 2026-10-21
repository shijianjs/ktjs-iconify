package iconify.tools

import iconify.IconifyJSON
import iconify.utils.trimSVG
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IconSetTest {
    private fun iconSet(source: String): IconSet =
        IconSet(jsObject(source).unsafeCast<IconifyJSON>())

    private fun setIcon(iconSet: IconSet, name: String, source: String): Boolean =
        iconSet.setIcon(name, jsObject(source).unsafeCast<ResolvedIconifyIcon>())

    @Test
    fun resolvesAliases() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    bar: {body: '<g id="bar"/>', width: 16, height: 16},
                    TestIcon: {body: '<g id="TestIcon"/>', width: 24, height: 24},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'TestIcon', hFlip: true},
                    invalid: {parent: 'missing'},
                    alias2: {parent: 'alias1'},
                    alias3: {parent: 'alias2'},
                    alias4: {parent: 'alias3'},
                    alias5: {parent: 'alias4'},
                    alias6: {parent: 'alias5'},
                    alias7: {parent: 'alias6'},
                },
            }"""
        )

        assertEquals(listOf("bar", "TestIcon", "variation_1"), iconSet.list().toList())
        assertEquals(3, iconSet.count())

        val expectedBar = "{body: '<g id=\"bar\"/>'}"
        assertJsObject(expectedBar, iconSet.resolve("bar"))
        assertJsObject(expectedBar, iconSet.resolve("alias1"))
        assertJsObject(expectedBar, iconSet.resolve("alias2"))
        assertJsObject(expectedBar, iconSet.resolve("alias3"))
        assertJsObject(expectedBar, iconSet.resolve("alias4"))
        assertJsObject(expectedBar, iconSet.resolve("alias5"))
        assertJsObject(expectedBar, iconSet.resolve("alias6"))
        assertJsObject(expectedBar, iconSet.resolve("alias7"))

        assertJsObject(
            "{body: '<g id=\"TestIcon\"/>', width: 24, height: 24}",
            iconSet.resolve("TestIcon"),
        )
        assertJsObject(
            "{body: '<g id=\"TestIcon\"/>', width: 24, height: 24, hFlip: true}",
            iconSet.resolve("variation_1"),
        )
        assertNull(iconSet.resolve("invalid"))

        // Remove 'alias3' and aliases that depend on it
        assertEquals(5, iconSet.remove("alias3"))
        assertJsObject(expectedBar, iconSet.resolve("alias2"))
        assertNull(iconSet.resolve("alias3"))
        assertNull(iconSet.resolve("alias4"))
        assertNull(iconSet.resolve("alias5"))
    }

    @Test
    fun resolvesDeepAliasChains() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {test: {body: '<g/>'}},
                aliases: {
                    alias1: {parent: 'test'},
                    alias2: {parent: 'alias1'},
                    alias3: {parent: 'alias2'},
                    alias4: {parent: 'alias3'},
                    alias5: {parent: 'alias4'},
                    alias6: {parent: 'alias5'},
                    alias7: {parent: 'alias6'},
                    alias8: {parent: 'alias7'},
                },
            }"""
        )

        assertEquals(listOf("test"), iconSet.list().toList())
        for (name in listOf("test", "alias1", "alias2", "alias3", "alias4", "alias5", "alias6", "alias7", "alias8")) {
            assertJsObject("{body: '<g/>'}", iconSet.resolve(name))
        }
    }

    @Test
    fun countsHiddenIconsAndRemovesDependants() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    bar: {body: '<g id="bar"/>', width: 16, height: 16},
                    TestIcon: {body: '<g id="TestIcon"/>', width: 24, height: 24, hidden: true},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'TestIcon', hFlip: true},
                    invalid: {parent: 'missing'},
                },
            }"""
        )

        assertEquals(listOf("bar", "TestIcon", "variation_1"), iconSet.list().toList())
        assertEquals(1, iconSet.count())

        assertTrue(iconSet.exists("alias1"))
        assertTrue(iconSet.exists("TestIcon"))
        assertTrue(iconSet.exists("variation_1"))

        assertEquals(2, iconSet.remove("TestIcon"))

        assertTrue(iconSet.exists("alias1"))
        assertFalse(iconSet.exists("TestIcon"))
        assertFalse(iconSet.exists("variation_1"))
    }

    @Test
    fun renamesIconWithCharsAndCategories() {
        val lastModified = 12345.0
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    bar: {body: '<g id="bar"/>', width: 16, height: 16},
                    TestIcon: {body: '<g id="TestIcon"/>', width: 24, height: 24, hidden: true},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'TestIcon', hFlip: true},
                    invalid: {parent: 'missing'},
                },
                chars: {f00: 'bar', f01: 'TestIcon', f02: 'alias1', f03: 'variation_1'},
                categories: {
                    'To Rename': ['TestIcon'],
                    Other: ['bar', 'variation_1', 'no-such-icon'],
                    Empty: ['no-such-icon'],
                },
            }"""
        )

        assertEquals(listOf("bar", "TestIcon", "variation_1"), iconSet.list().toList())
        assertEquals(1, iconSet.count())
        assertTrue(iconSet.exists("alias1"))
        assertTrue(iconSet.exists("TestIcon"))
        assertTrue(iconSet.exists("variation_1"))
        assertFalse(iconSet.exists("foo"))
        assertEquals(lastModified, iconSet.lastModified)

        assertTrue(iconSet.rename("TestIcon", "foo"))

        assertTrue(iconSet.exists("alias1"))
        assertFalse(iconSet.exists("TestIcon"))
        assertTrue(iconSet.exists("variation_1"))
        assertTrue(iconSet.exists("foo"))
        assertTrue(iconSet.lastModified != lastModified)

        assertJsObject(
            "{body: '<g id=\"TestIcon\"/>', width: 24, height: 24, hidden: true}",
            iconSet.resolve("foo"),
        )
        assertJsObject(
            "{body: '<g id=\"TestIcon\"/>', width: 24, height: 24, hidden: true, hFlip: true}",
            iconSet.resolve("variation_1"),
        )

        val expected =
            """{
                prefix: 'foo',
                icons: {
                    bar: {body: '<g id="bar"/>'},
                    foo: {body: '<g id="TestIcon"/>', width: 24, height: 24, hidden: true},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'foo', hFlip: true},
                    invalid: {parent: 'missing'},
                },
                chars: {f00: 'bar', f01: 'foo', f02: 'alias1', f03: 'variation_1'},
                categories: {Other: ['bar']},
            }"""
        val exported = jsObject(expected)
        exported.lastModified = iconSet.lastModified
        assertJsDeepEquals(exported, iconSet.export(false))

        // Export with validation: invalid alias should not be there
        js("(object, key) => { delete object[key]; }")
            .unsafeCast<(dynamic, String) -> Unit>()(exported.aliases, "invalid")
        assertJsDeepEquals(exported, iconSet.export())
    }

    @Test
    fun ignoresCategoriesForAliases() {
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    bar: {body: '<g id="bar"/>'},
                    TestIcon: {body: '<g id="TestIcon"/>'},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'TestIcon', hFlip: true},
                },
                categories: {
                    Bar: ['bar'],
                    TestIcon: ['TestIcon'],
                    Other: ['alias1', 'variation_1'],
                },
            }"""
        )

        assertJsObject(
            """{
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    bar: {body: '<g id="bar"/>'},
                    TestIcon: {body: '<g id="TestIcon"/>'},
                },
                aliases: {
                    alias1: {parent: 'bar'},
                    variation_1: {parent: 'TestIcon', hFlip: true},
                },
                categories: {
                    Bar: ['bar'],
                    TestIcon: ['TestIcon'],
                },
            }""",
            iconSet.export(false),
        )
    }

    @Test
    fun loadsSimpleIconSet() {
        val lastModified = 123.0
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                lastModified: 123,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>'},
                },
                aliases: {
                    baz: {parent: 'bar', hFlip: true},
                    foo: {parent: 'bar'},
                },
            }"""
        )

        assertEquals(listOf("foo", "bar", "baz"), iconSet.list().toList())

        val entries = iconSet.entries
        assertEquals("icon", entries.foo.type as String)
        assertEquals("""<g id="foo"/>""", entries.foo.body as String)
        assertEquals("""{}""", jsStringify(entries.foo.props))
        assertEquals(0, entries.foo.chars.size as Int)
        assertEquals(0, entries.foo.categories.size as Int)
        assertEquals("variation", entries.baz.type as String)
        assertEquals("bar", entries.baz.parent as String)
        assertTrue(entries.baz.props.hFlip as Boolean)

        assertEquals(lastModified, iconSet.lastModified)

        assertTrue(iconSet.exists("foo"))
        assertTrue(iconSet.exists("bar"))
        assertTrue(iconSet.exists("baz"))
        assertFalse(iconSet.exists("bar2"))

        assertJsObject("{body: '<g id=\"bar\"/>'}", iconSet.resolve("bar"))
        assertJsObject("{body: '<g id=\"bar\"/>'}", iconSet.resolve("bar", false))
        assertJsObject("{body: '<g id=\"foo\"/>'}", iconSet.resolve("foo"))

        assertJsObject(
            """{
                prefix: 'foo',
                lastModified: 123,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>'},
                },
                aliases: {
                    baz: {parent: 'bar', hFlip: true},
                },
            }""",
            iconSet.export(),
        )

        assertEquals(3, iconSet.count())
        assertNull(iconSet.info)
    }

    @Test
    fun loadsPropertiesAndThemes() {
        val lastModified = 13579.0
        val iconSet = iconSet(
            """{
                prefix: 'foo',
                lastModified: 13579,
                icons: {
                    'bar-32': {body: '<g/>', width: 32, hFlip: true, vFlip: false, hidden: true},
                    'bar-20': {body: '<g id="baz"/>', width: 20, height: 20, left: 0, top: 0, rotate: 1},
                },
                height: 24,
                chars: {f00: 'bar-32', f01: 'foo'},
                themes: {
                    valid32px: {title: '32px', suffix: '-32'},
                    valid20px: {title: '20px', suffix: '-20'},
                    invalid1: {title: '24px', suffix: '24'},
                    validPrefix: {title: 'Bar', prefix: 'bar-'},
                    invalid2: {title: 'Baz', prefix: 'baz'},
                },
            }"""
        )

        assertEquals(listOf("bar-32", "bar-20"), iconSet.list().toList())

        val entries = iconSet.entries
        assertJsObject(
            """{type: 'icon', body: '<g/>', props: {width: 32, height: 24, hFlip: true, hidden: true}, chars: {}, categories: {}}""",
            entries["bar-32"],
        )
        assertJsObject(
            """{type: 'icon', body: '<g id="baz"/>', props: {width: 20, height: 20, rotate: 1}, chars: {}, categories: {}}""",
            entries["bar-20"],
        )
        assertEquals(1, entries["bar-32"].chars.size as Int)
        assertTrue(entries["bar-32"].chars.has("f00") as Boolean)

        assertEquals(lastModified, iconSet.lastModified)
        assertTrue(iconSet.exists("bar-32"))
        assertTrue(iconSet.exists("bar-20"))
        assertFalse(iconSet.exists("foo"))

        assertJsObject(
            "{body: '<g/>', width: 32, height: 24, hFlip: true, hidden: true}",
            iconSet.resolve("bar-32"),
        )
        assertJsObject(
            """{body: '<g/>', width: 32, height: 24, left: 0, top: 0, rotate: 0, hFlip: true, vFlip: false, hidden: true}""",
            iconSet.resolve("bar-32", true),
        )
        assertJsObject(
            "{body: '<g id=\"baz\"/>', width: 20, height: 20, rotate: 1}",
            iconSet.resolve("bar-20"),
        )
        assertNull(iconSet.resolve("missing"))

        assertJsObject(
            """{
                prefix: 'foo',
                lastModified: 13579,
                icons: {
                    'bar-32': {body: '<g/>', width: 32, height: 24, hFlip: true, hidden: true},
                    'bar-20': {body: '<g id="baz"/>', width: 20, height: 20, rotate: 1},
                },
                chars: {f00: 'bar-32'},
                prefixes: {bar: 'Bar'},
                suffixes: {'20': '20px'},
            }""",
            iconSet.export(),
        )

        assertEquals(1, iconSet.count())
        assertNull(iconSet.info)

        val themes = iconSet.checkTheme(false)
        assertJsObject("{'20': ['bar-20'], '32': []}", themes.valid)
        assertEquals(0, themes.invalid.size)
    }

    @Test
    fun addsIcons() {
        val lastModified = 12345.0
        val iconSet = blankIconSet("test")
        assertEquals(0, iconSet.list().size)
        assertJsObject("{prefix: 'test', icons: {}}", iconSet.export())
        assertEquals(0.0, iconSet.lastModified)

        iconSet.updateLastModified(lastModified)
        assertEquals(lastModified, iconSet.lastModified)

        assertTrue(setIcon(iconSet, "foo", "{body: '<g id=\"foo\" />'}"))
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)
        assertEquals(lastModified, iconSet.lastModified)
        assertEquals(listOf("foo"), iconSet.list().toList())
        assertJsObject(
            "{prefix: 'test', lastModified: 12345, icons: {foo: {body: '<g id=\"foo\"/>'}}}",
            iconSet.export(),
        )

        assertTrue(
            setIcon(iconSet, "bar", "{body: '<g id=\"bar\" />', width: 24, height: 24}")
        )
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)
        assertEquals(listOf("foo", "bar"), iconSet.list().toList())
        assertJsObject(
            """{
                prefix: 'test',
                lastModified: 12345,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>', width: 24, height: 24},
                },
            }""",
            iconSet.export(),
        )

        // Bad alias
        assertFalse(iconSet.setAlias("foo-alias", "no-parent"))
        assertEquals(lastModified, iconSet.lastModified)
        assertEquals(listOf("foo", "bar"), iconSet.list().toList())

        // Good alias
        assertTrue(iconSet.setAlias("foo-alias", "foo"))
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)
        assertEquals(listOf("foo", "bar"), iconSet.list().toList())
        assertJsObject(
            """{
                prefix: 'test',
                lastModified: 12345,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>', width: 24, height: 24},
                },
                aliases: {'foo-alias': {parent: 'foo'}},
            }""",
            iconSet.export(),
        )

        // Bad variation
        assertFalse(
            iconSet.setVariation(
                "foo-flip",
                "no-parent",
                jsObject("{hFlip: true}").unsafeCast<CommonIconProps>(),
            )
        )
        assertEquals(lastModified, iconSet.lastModified)
        assertEquals(listOf("foo", "bar"), iconSet.list().toList())

        // Good variation
        assertTrue(
            iconSet.setVariation(
                "foo-flip",
                "foo",
                jsObject("{hFlip: true}").unsafeCast<CommonIconProps>(),
            )
        )
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)
        assertEquals(listOf("foo", "bar", "foo-flip"), iconSet.list().toList())
        assertJsObject(
            """{
                prefix: 'test',
                lastModified: 12345,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>', width: 24, height: 24},
                },
                aliases: {
                    'foo-alias': {parent: 'foo'},
                    'foo-flip': {parent: 'foo', hFlip: true},
                },
            }""",
            iconSet.export(),
        )

        // Overwrite variation
        assertTrue(
            iconSet.setVariation(
                "foo-flip",
                "foo",
                jsObject("{vFlip: true}").unsafeCast<CommonIconProps>(),
            )
        )
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)
        assertEquals(listOf("foo", "bar", "foo-flip"), iconSet.list().toList())
        assertJsObject(
            """{
                prefix: 'test',
                lastModified: 12345,
                icons: {
                    foo: {body: '<g id="foo"/>'},
                    bar: {body: '<g id="bar"/>', width: 24, height: 24},
                },
                aliases: {
                    'foo-alias': {parent: 'foo'},
                    'foo-flip': {parent: 'foo', vFlip: true},
                },
            }""",
            iconSet.export(),
        )
    }

    @Test
    fun updatesFromSvg() {
        val lastModified = 123455.0
        val iconSet = blankIconSet("test")
        val exported = jsObject("{prefix: 'test', icons: {}}")
        assertEquals(0, iconSet.list().size)
        assertJsDeepEquals(exported, iconSet.export())
        assertEquals(0.0, iconSet.lastModified)

        val svgBody =
            """<path d="M3 0v1h4v5h-4v1h5v-7h-5zm1 2v1h-4v1h4v1l2-1.5-2-1.5z"/>"""
        var svg = SVG(
            """<svg xmlns="http://www.w3.org/2000/svg" width="12" height="20" viewBox="-8 -16 24 40">$svgBody</svg>"""
        )
        assertTrue(iconSet.fromSVG("foo", svg))
        assertTrue(iconSet.lastModified > 0.0)

        iconSet.updateLastModified(lastModified)
        exported.lastModified = lastModified
        exported.icons.foo = jsObject(
            "{body: '$svgBody', left: -8, top: -16, width: 24, height: 40}"
        )
        assertEquals(listOf("foo"), iconSet.list().toList())
        assertJsDeepEquals(exported, iconSet.export())

        assertTrue(iconSet.toggleCharacter("foo", "f00", true))
        assertTrue(iconSet.toggleCategory("foo", "Test", true))
        assertEquals(lastModified, iconSet.lastModified)
        exported.chars = jsObject("{f00: 'foo'}")
        exported.categories = jsObject("{Test: ['foo']}")

        svg = iconSet.toSVG("foo")!!
        assertEquals(-8.0, svg.viewBox.left)
        assertEquals(-16.0, svg.viewBox.top)
        assertEquals(24.0, svg.viewBox.width)
        assertEquals(40.0, svg.viewBox.height)
        assertEquals(svgBody, svg.getBody())
        assertEquals(lastModified, iconSet.lastModified)

        svg.viewBox = jsObject("{left: 0, top: 0, width: 32, height: 32}")
            .unsafeCast<IconViewBox>()
        assertEquals(lastModified, iconSet.lastModified)
        iconSet.fromSVG("bar", svg)
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)

        exported.icons.bar = jsObject("{body: '$svgBody', width: 32, height: 32}")
        assertEquals(listOf("foo", "bar"), iconSet.list().toList())
        assertJsDeepEquals(exported, iconSet.export())

        val svgBody2 = """<g>
        <path d="M62.73,49.109c5.347-1.103,9.76-5.94,9.76-12.985c0-7.553-5.517-14.428-16.295-14.428H29.011
            c-1.438,0-2.604,1.166-2.604,2.604v51.399c0,1.438,1.166,2.604,2.604,2.604h28.118c10.863,0,16.464-6.79,16.464-15.361
            C73.594,55.899,68.841,50.042,62.73,49.109z M38.458,32.305h15.107c4.074,0,6.62,2.461,6.62,5.94c0,3.649-2.546,5.941-6.62,5.941
            H38.458V32.305z M54.073,67.695H38.458v-12.9h15.616c4.668,0,7.214,2.886,7.214,6.45C61.288,65.319,58.572,67.695,54.073,67.695z"
            />
    </g>"""
        svg.load(
            """<?xml version="1.0" encoding="utf-8"?>
        <!-- Generator: Adobe Illustrator 17.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  -->
        <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
        <svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
             width="100px" height="100px" viewBox="0 0 100 100" enable-background="new 0 0 100 100" xml:space="preserve">
        $svgBody2
        </svg>
        """
        )
        assertEquals(0.0, svg.viewBox.left)
        assertEquals(0.0, svg.viewBox.top)
        assertEquals(100.0, svg.viewBox.width)
        assertEquals(100.0, svg.viewBox.height)
        assertEquals(trimSVG(svgBody2), trimSVG(svg.getBody()))
        assertEquals(lastModified, iconSet.lastModified)

        // Overwrite 'foo', category and character should be copied from the old entry
        assertTrue(iconSet.fromSVG("foo", svg))
        val exportedFoo = jsObject("{}")
        exportedFoo.body = trimSVG(svgBody2)
        exportedFoo.width = 100
        exportedFoo.height = 100
        exported.icons.foo = exportedFoo
        assertTrue(iconSet.lastModified != lastModified)
        iconSet.updateLastModified(lastModified)

        assertEquals(listOf("foo", "bar"), iconSet.list().toList())
        assertJsDeepEquals(exported, iconSet.export())
    }
}
