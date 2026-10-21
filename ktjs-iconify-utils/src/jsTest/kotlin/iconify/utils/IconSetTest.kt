package iconify.utils

import iconify.ExtendedIconifyIcon
import iconify.IconifyInfo
import iconify.IconifyJSON
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class IconSetTest {
    private fun iconSet(source: String): IconifyJSON = jsObject(source).unsafeCast<IconifyJSON>()

    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    private fun iconNames(value: IconifyJSON): Array<String> = keys(value.icons).sortedArray()

    private fun aliasNames(value: IconifyJSON): Array<String> =
        keys(value.aliases).sortedArray()

    @Test
    fun extractsIcons() {
        val data = iconSet(
            """({
                prefix: 'foo',
                icons: {
                    bar: { body: '<g id="bar" />' },
                    baz: { body: '<g id="baz" />' },
                    foo: { body: '<g id="foo" />' }
                }
            })""",
        )

        assertNull(getIcons(data, arrayOf("missing-icon")))

        val missing = assertNotNull(getIcons(data, arrayOf("missing-icon"), notFound = true))
        assertEquals("foo", missing.prefix)
        assertEquals(0, keys(missing.icons).size)
        assertContentEquals(arrayOf("missing-icon"), missing.not_found)

        val single = assertNotNull(getIcons(data, arrayOf("bar")))
        assertEquals("foo", single.prefix)
        assertContentEquals(arrayOf("bar"), iconNames(single))
        assertEquals("<g id=\"bar\" />", prop(single.icons, "bar").body.unsafeCast<String>())
        assertNull(single.aliases)

        val duplicates = assertNotNull(getIcons(data, arrayOf("bar", "bar", "bar")))
        assertContentEquals(arrayOf("bar"), iconNames(duplicates))

        val multiple = assertNotNull(getIcons(data, arrayOf("foo", "bar")))
        assertContentEquals(arrayOf("bar", "foo"), iconNames(multiple))
        assertEquals("<g id=\"bar\" />", prop(multiple.icons, "bar").body.unsafeCast<String>())
        assertEquals("<g id=\"foo\" />", prop(multiple.icons, "foo").body.unsafeCast<String>())
    }

    @Test
    fun copiesCommonProperties() {
        val data = iconSet(
            """({
                prefix: 'foo',
                provider: 'iconify',
                left: 1,
                top: 2,
                width: 24,
                height: 32,
                icons: { bar: { body: '<g />' } }
            })""",
        )

        val subset = assertNotNull(getIcons(data, arrayOf("bar")))
        assertEquals("iconify", subset.provider)
        assertEquals(1.0, subset.left)
        assertEquals(2.0, subset.top)
        assertEquals(24.0, subset.width)
        assertEquals(32.0, subset.height)
    }

    @Test
    fun extractsAliasesAndCharacters() {
        val data = iconSet(
            """({
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    bar: { body: '<g />' },
                    bar_2: { body: '<g />' }
                },
                aliases: {
                    'foo': { parent: 'bar', hFlip: true },
                    'foo_2': { parent: 'foo' },
                    'missing-alias': { parent: 'missing-icon' }
                },
                chars: {
                    f00: 'bar_2',
                    f01: 'bar',
                    f02: 'foo',
                    f03: 'foo_2',
                    f04: 'missing-icon'
                }
            })""",
        )

        val alias = assertNotNull(getIcons(data, arrayOf("foo")))
        assertEquals(12345.0, alias.lastModified)
        assertContentEquals(arrayOf("bar"), iconNames(alias))
        assertContentEquals(arrayOf("foo"), aliasNames(alias))
        assertEquals("bar", prop(alias.aliases, "foo").parent.unsafeCast<String>())
        assertEquals(true, prop(alias.aliases, "foo").hFlip.unsafeCast<Boolean>())

        val nested = assertNotNull(getIcons(data, arrayOf("foo_2")))
        assertContentEquals(arrayOf("bar"), iconNames(nested))
        assertContentEquals(arrayOf("foo", "foo_2"), aliasNames(nested))

        assertNull(getIcons(data, arrayOf("missing-alias")))
        val badAlias = assertNotNull(getIcons(data, arrayOf("missing-alias"), notFound = true))
        assertContentEquals(arrayOf("missing-alias"), badAlias.not_found)
        assertEquals(0, keys(badAlias.icons).size)

        // Characters are not icon names
        assertNull(getIcons(data, arrayOf("f00")))
        assertNull(getIcons(data, arrayOf("f02")))

        assertNull(getIcons(data, arrayOf("f04")))
        val badChar = assertNotNull(getIcons(data, arrayOf("f04"), notFound = true))
        assertContentEquals(arrayOf("f04"), badChar.not_found)
    }

    @Test
    fun buildsIconsTree() {
        val tree = getIconsTree(
            iconSet(
                """({
                    prefix: 'foo',
                    icons: {
                        foo: { body: '<g />' },
                        bar: { body: '<g />' }
                    },
                    aliases: {
                        baz: { parent: 'bar' }
                    }
                })""",
            ),
        )

        assertContentEquals(arrayOf<String>(), prop(tree, "foo").unsafeCast<Array<String>>())
        assertContentEquals(arrayOf<String>(), prop(tree, "bar").unsafeCast<Array<String>>())
        assertContentEquals(arrayOf("bar"), prop(tree, "baz").unsafeCast<Array<String>>())
    }

    @Test
    fun buildsAliasChainsAndMarksBadOnes() {
        val tree = getIconsTree(
            iconSet(
                """({
                    prefix: 'foo',
                    icons: {
                        foo: { body: '<g />' },
                        bar: { body: '<g />' }
                    },
                    aliases: {
                        baz: { parent: 'bar' },
                        baz2: { parent: 'baz3' },
                        baz3: { parent: 'baz' },
                        baz4: { parent: 'baz3' },
                        baz5: { parent: 'baz4' },
                        baz6: { parent: 'baz5' },
                        bazz5: { parent: 'baz4', hFlip: true },
                        bad: { parent: 'good' },
                        loop1: { parent: 'loop3' },
                        loop2: { parent: 'loop1' },
                        loop3: { parent: 'loop1' }
                    }
                })""",
            ),
        )

        assertContentEquals(arrayOf<String>(), prop(tree, "foo").unsafeCast<Array<String>>())
        assertContentEquals(arrayOf("bar"), prop(tree, "baz").unsafeCast<Array<String>>())
        assertContentEquals(
            arrayOf("baz3", "baz", "bar"),
            prop(tree, "baz2").unsafeCast<Array<String>>(),
        )
        assertContentEquals(
            arrayOf("baz", "bar"),
            prop(tree, "baz3").unsafeCast<Array<String>>(),
        )
        assertContentEquals(
            arrayOf("baz3", "baz", "bar"),
            prop(tree, "baz4").unsafeCast<Array<String>>(),
        )
        assertContentEquals(
            arrayOf("baz4", "baz3", "baz", "bar"),
            prop(tree, "baz5").unsafeCast<Array<String>>(),
        )
        assertContentEquals(
            arrayOf("baz5", "baz4", "baz3", "baz", "bar"),
            prop(tree, "baz6").unsafeCast<Array<String>>(),
        )
        assertContentEquals(
            arrayOf("baz4", "baz3", "baz", "bar"),
            prop(tree, "bazz5").unsafeCast<Array<String>>(),
        )

        assertJsNull(prop(tree, "good"))
        assertJsNull(prop(tree, "bad"))
        assertJsNull(prop(tree, "loop1"))
        assertJsNull(prop(tree, "loop2"))
        assertJsNull(prop(tree, "loop3"))
    }

    private fun assertJsNull(value: dynamic) {
        assertTrue(js("value == null").unsafeCast<Boolean>())
    }

    @Test
    fun resolvesTreeForSelectedNamesOnly() {
        val data = iconSet(
            """({
                prefix: 'foo',
                icons: { foo: { body: '<g />' }, bar: { body: '<g />' } },
                aliases: { baz: { parent: 'bar' } }
            })""",
        )

        val tree = getIconsTree(data, arrayOf("baz", "missing"))
        assertContentEquals(arrayOf("bar", "baz", "missing"), keys(tree).sortedArray())
        assertContentEquals(arrayOf("bar"), prop(tree, "baz").unsafeCast<Array<String>>())
        assertJsNull(prop(tree, "missing"))
    }

    @Test
    fun parsesIconSet() {
        val parsed = mutableMapOf<String, ExtendedIconifyIcon?>()
        val names = mutableListOf<String>()

        val result = parseIconSet(
            iconSet(
                """({
                    prefix: 'foo',
                    not_found: ['missing'],
                    icons: {
                        icon_1: { body: '<path d="icon1" />', width: 20 },
                        icon_2: { body: '<path d="icon2" />', width: 24 }
                    },
                    height: 24
                })""",
            ),
        ) { name, data ->
            names.add(name)
            parsed[name] = data
        }

        assertContentEquals(arrayOf("missing", "icon_1", "icon_2"), result)
        assertContentEquals(arrayOf("missing", "icon_1", "icon_2"), names.toTypedArray())

        assertNull(parsed["missing"])
        assertEquals("<path d=\"icon1\" />", parsed.getValue("icon_1")?.body)
        assertEquals(20.0, parsed.getValue("icon_1")?.width)
        assertEquals(24.0, parsed.getValue("icon_1")?.height)
        assertEquals("<path d=\"icon2\" />", parsed.getValue("icon_2")?.body)
        assertEquals(24.0, parsed.getValue("icon_2")?.width)
        assertEquals(24.0, parsed.getValue("icon_2")?.height)
    }

    @Test
    fun parsesAliases() {
        val parsed = mutableMapOf<String, ExtendedIconifyIcon?>()

        val result = parseIconSet(
            iconSet(
                """({
                    prefix: 'foo',
                    icons: {
                        icon1: { body: '<path d="icon1" />', width: 20 },
                        icon2: { body: '<path d="icon2" />', width: 24, vFlip: true, rotate: 3 }
                    },
                    aliases: {
                        alias1: { parent: 'icon1' },
                        alias2: { parent: 'icon2', hFlip: true, width: 20 },
                        icon2: { parent: 'icon1' }
                    },
                    height: 24
                })""",
            ),
        ) { name, data ->
            parsed[name] = data
        }

        assertContentEquals(
            arrayOf("icon1", "icon2", "alias1", "alias2").sortedArray(),
            result.sortedArray(),
        )

        val alias1 = assertNotNull(parsed["alias1"])
        assertEquals("<path d=\"icon1\" />", alias1.body)
        assertEquals(20.0, alias1.width)
        assertEquals(24.0, alias1.height)

        val alias2 = assertNotNull(parsed["alias2"])
        assertEquals("<path d=\"icon2\" />", alias2.body)
        assertEquals(20.0, alias2.width)
        assertEquals(24.0, alias2.height)
        assertEquals(true, alias2.hFlip)
        assertEquals(true, alias2.vFlip)
        assertEquals(3.0, alias2.rotate)
    }

    @Test
    fun parsesNestedAliasesAsynchronously(): Promise<Unit> {
        val parsed = mutableMapOf<String, ExtendedIconifyIcon?>()

        return parseIconSetAsync(
            iconSet(
                """({
                    prefix: 'foo',
                    icons: {
                        icon2: { body: '<path d="icon2" />', width: 24, rotate: 1, hFlip: true }
                    },
                    aliases: {
                        alias2f: { parent: 'icon2', width: 22, rotate: 1, hFlip: true, vFlip: true },
                        alias2z: { parent: 'alias2f', width: 21, rotate: 3 },
                        alias2z3: { parent: 'alias2z' },
                        alias3: { parent: 'icon3' }
                    },
                    height: 24
                })""",
            ),
        ) { name, data ->
            parsed[name] = data
            Promise.resolve(Unit)
        }.then { names ->
            assertContentEquals(
                arrayOf("icon2", "alias2f", "alias2z", "alias2z3").sortedArray(),
                names.sortedArray(),
            )

            val icon2 = assertNotNull(parsed["icon2"])
            assertEquals(24.0, icon2.width)
            assertEquals(24.0, icon2.height)
            assertEquals(true, icon2.hFlip)
            assertEquals(1.0, icon2.rotate)

            val alias2f = assertNotNull(parsed["alias2f"])
            assertEquals(22.0, alias2f.width)
            assertEquals(24.0, alias2f.height)
            assertEquals(true, alias2f.vFlip)
            assertEquals(2.0, alias2f.rotate)

            val alias2z = assertNotNull(parsed["alias2z"])
            assertEquals(21.0, alias2z.width)
            assertEquals(24.0, alias2z.height)
            assertEquals(true, alias2z.vFlip)
            assertEquals(1.0, alias2z.rotate)

            val alias2z3 = assertNotNull(parsed["alias2z3"])
            assertEquals(21.0, alias2z3.width)
            assertEquals(24.0, alias2z3.height)
            assertEquals(true, alias2z3.vFlip)
            assertEquals(1.0, alias2z3.rotate)
        }
    }

    @Test
    fun parsesIconNamesWithAnyCharacters() {
        val names = mutableListOf<String>()
        val result = parseIconSet(
            iconSet(
                """({
                    prefix: 'api-mock-02',
                    icons: {
                        test20: { body: '<g />' },
                        test21: { body: '<g />' },
                        BadName: { body: '<g />' }
                    }
                })""",
            ),
        ) { name, _ ->
            names.add(name)
        }

        assertContentEquals(arrayOf("test20", "test21", "BadName"), result)
        assertContentEquals(arrayOf("test20", "test21", "BadName"), names.toTypedArray())
    }

    private fun assertIcon(
        iconSet: IconifyJSON,
        name: String,
        body: String = "<g />",
        width: Double? = null,
        height: Double? = null,
        hidden: Boolean? = null,
    ) {
        val icon = prop(iconSet.icons, name)
        assertEquals(body, icon.body.unsafeCast<String>())
        assertEquals(width, if (hasProperty(icon, "width")) icon.width.unsafeCast<Double>() else null)
        assertEquals(height, if (hasProperty(icon, "height")) icon.height.unsafeCast<Double>() else null)
        assertEquals(hidden, if (hasProperty(icon, "hidden")) icon.hidden.unsafeCast<Boolean>() else null)
    }

    @Test
    fun minifiesNothing() {
        val item = iconSet(
            """({
                prefix: 'foo',
                icons: { foo: { body: '<g />', width: 24, height: 24 } }
            })""",
        )
        minifyIconSet(item)

        assertNull(item.width)
        assertNull(item.height)
        assertIcon(item, "foo", width = 24.0, height = 24.0)
    }

    @Test
    fun keepsIconSetsWithoutCommonValues() {
        val item = iconSet(
            """({
                prefix: 'foo',
                icons: {
                    'foo-24': { body: '<g />', width: 24, height: 24 },
                    'foo-16': { body: '<g />', width: 12, height: 12 }
                }
            })""",
        )
        minifyIconSet(item)

        assertNull(item.width)
        assertNull(item.height)
        assertIcon(item, "foo-24", width = 24.0, height = 24.0)
        assertIcon(item, "foo-16", width = 12.0, height = 12.0)
    }

    @Test
    fun removesDefaultValues() {
        val item = iconSet(
            """({
                prefix: 'foo',
                icons: {
                    foo: { body: '<g />', width: 24, height: 16 },
                    bar: { body: '<g />', width: 16, height: 12 }
                }
            })""",
        )
        minifyIconSet(item)

        assertNull(item.width)
        assertNull(item.height)
        assertIcon(item, "foo", width = 24.0)
        assertIcon(item, "bar", height = 12.0)
    }

    @Test
    fun movesCommonDefaultsToRoot() {
        val item = iconSet(
            """({
                prefix: 'bar',
                icons: {
                    'chrome-maximize': { body: '<g />', width: 24, height: 24, hidden: true },
                    'chrome-minimize': { body: '<g />', width: 24, height: 24, hidden: true },
                    'remove': { body: '<g />' }
                }
            })""",
        )
        minifyIconSet(item)

        assertEquals(24.0, item.width)
        assertEquals(24.0, item.height)
        assertIcon(item, "chrome-maximize", hidden = true)
        assertIcon(item, "chrome-minimize", hidden = true)
        assertIcon(item, "remove", width = 16.0, height = 16.0)
    }

    @Test
    fun movesCommonValuesToRoot() {
        val item = iconSet(
            """({
                prefix: 'foo',
                icons: {
                    'foo-wide': { body: '<g />', width: 32, height: 24 },
                    'foo-square': { body: '<g />', width: 24, height: 24 },
                    'foo-tiny-wide': { body: '<g />', width: 24, height: 16 },
                    'foo-tiny-square': { body: '<g />', width: 16, height: 16 }
                }
            })""",
        )
        minifyIconSet(item)

        assertEquals(24.0, item.width)
        assertEquals(24.0, item.height)
        assertIcon(item, "foo-wide", width = 32.0)
        assertIcon(item, "foo-square")
        assertIcon(item, "foo-tiny-wide", height = 16.0)
        assertIcon(item, "foo-tiny-square", width = 16.0, height = 16.0)
    }

    @Test
    fun removesCommonValueMatchingDefault() {
        val item = iconSet(
            """({
                prefix: 'foo',
                icons: {
                    'foo-wide': { body: '<g />', width: 24, height: 16 },
                    'foo-square': { body: '<g />', width: 16, height: 16 },
                    'foo-tiny': { body: '<g />', width: 12, height: 16 }
                }
            })""",
        )
        minifyIconSet(item)

        assertNull(item.width)
        assertNull(item.height)
        assertIcon(item, "foo-wide", width = 24.0)
        assertIcon(item, "foo-square")
        assertIcon(item, "foo-tiny", width = 12.0)
    }

    @Test
    fun expandsMinifiedIconSet() {
        val item = iconSet(
            """({
                prefix: 'foo',
                width: 24,
                height: 32,
                left: 1,
                top: 2,
                icons: {
                    foo: { body: '<g />' },
                    bar: { body: '<g />', width: 16 }
                },
                aliases: { baz: { parent: 'foo' } }
            })""",
        )
        expandIconSet(item)

        assertNull(item.width)
        assertNull(item.height)
        assertNull(item.left)
        assertNull(item.top)
        assertIcon(item, "foo", width = 24.0, height = 32.0)
        assertIcon(item, "bar", width = 16.0, height = 32.0)
        assertEquals(1.0, prop(item.icons, "foo").left.unsafeCast<Double>())
        assertEquals(2.0, prop(item.icons, "foo").top.unsafeCast<Double>())
        // Aliases are not expanded
        assertEquals("foo", prop(item.aliases, "baz").parent.unsafeCast<String>())
    }

    @Test
    fun convertsInfo() {
        assertNull(convertIconSetInfo(jsObject("({})")))

        val named = assertNotNull(convertIconSetInfo(jsObject("({ name: 'Foo' })")))
        assertEquals("Foo", named.name)
        assertEquals("", named.author.name)
        assertEquals("", named.license.title)
        assertNull(named.author.url)
        assertNull(named.license.spdx)

        val titled = assertNotNull(convertIconSetInfo(jsObject("({ prefix: 'foo', title: 'Foo' })")))
        assertEquals("Foo", titled.name)

        assertNull(convertIconSetInfo(jsObject("({ name: 'Foo', prefix: 'bar' })"), "foo"))
    }

    @Test
    fun convertsLegacyInfo() {
        val info = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        prefix: 'foo',
                        title: 'Foo',
                        author: 'Author',
                        url: 'https://localhost/',
                        license: 'MIT',
                        licenseID: 'MIT',
                        licenseURL: 'https://license.local/',
                        height: '24',
                        samples: ['arrow-left', 'arrow-right', 'arrow-up', 'arrow-down', 'arrow-up'],
                        palette: true
                    })""",
                ),
            ),
        )

        assertEquals("Foo", info.name)
        assertEquals("Author", info.author.name)
        assertEquals("https://localhost/", info.author.url)
        assertEquals("MIT", info.license.title)
        assertEquals("MIT", info.license.spdx)
        assertEquals("https://license.local/", info.license.url)
        assertEquals(24.0, info.height.unsafeCast<Double>())
        assertContentEquals(
            arrayOf("arrow-left", "arrow-right", "arrow-up", "arrow-down"),
            info.samples,
        )
        assertEquals(true, info.palette)
        assertNull(info.displayHeight)
    }

    @Test
    fun convertsNewInfo() {
        val info = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        prefix: 'foo',
                        title: 'Foo',
                        total: '100',
                        author: { name: 'Author' },
                        license: { title: 'BSD' },
                        height: [16, '18'],
                        palette: 'Colorful'
                    })""",
                ),
            ),
        )

        assertEquals("Foo", info.name)
        assertEquals(100.0, info.total)
        assertEquals("Author", info.author.name)
        assertNull(info.author.url)
        assertEquals("BSD", info.license.title)
        assertNull(info.license.spdx)
        assertTrue(js("Array.isArray").unsafeCast<(dynamic) -> Boolean>()(info.height))
        assertContentEquals(arrayOf(16.0, 18.0), info.height.unsafeCast<Array<Double>>())
        assertEquals(true, info.palette)

        val full = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        name: 'Foo',
                        version: '1.0.0',
                        total: 100,
                        author: { name: 'Author', url: 'https://author.local/' },
                        license: { title: 'BSD', spdx: 'BSD', url: 'https://license.local/' },
                        height: 32,
                        displayHeight: 24,
                        samples: ['home', 'pin', 'alert'],
                        palette: false,
                        category: 'Thematic'
                    })""",
                ),
            ),
        )

        assertEquals("1.0.0", full.version)
        assertEquals(100.0, full.total)
        assertEquals("https://author.local/", full.author.url)
        assertEquals("BSD", full.license.spdx)
        assertEquals("https://license.local/", full.license.url)
        assertEquals(32.0, full.height.unsafeCast<Double>())
        assertEquals(24.0, full.displayHeight)
        assertContentEquals(arrayOf("home", "pin", "alert"), full.samples)
        assertEquals(false, full.palette)
        assertEquals("Thematic", full.category)
    }

    @Test
    fun convertsKnownLegacyFormats() {
        val antDesign = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        name: 'Ant Design Icons',
                        total: 728,
                        author: 'HeskeyBaozi',
                        url: 'https://github.com/ant-design/ant-design-icons',
                        license: 'MIT',
                        height: 16,
                        samples: ['pushpin', 'pie-chart-outline', 'user-add-outline'],
                        palette: 'Colorless',
                        category: 'General'
                    })""",
                ),
            ),
        )
        assertEquals("HeskeyBaozi", antDesign.author.name)
        assertEquals("https://github.com/ant-design/ant-design-icons", antDesign.author.url)
        assertEquals("MIT", antDesign.license.title)
        assertEquals(false, antDesign.palette)

        val fontAwesome = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        name: 'Font Awesome 4',
                        total: 678,
                        author: 'Dave Gandy',
                        url: 'http://fontawesome.io/',
                        license: 'Open Font License',
                        licenseURL: 'http://scripts.sil.org/OFL',
                        samples: ['wrench', 'bell-o', 'user-o'],
                        version: '4.7.0',
                        palette: 'Colorless',
                        category: 'General'
                    })""",
                ),
            ),
        )
        assertEquals("http://scripts.sil.org/OFL", fontAwesome.license.url)
        assertEquals("4.7.0", fontAwesome.version)
        assertEquals(false, fontAwesome.palette)

        val codicons = assertNotNull(
            convertIconSetInfo(
                jsObject(
                    """({
                        name: 'Codicons',
                        total: 383,
                        author: {
                            name: 'Microsoft Corporation',
                            url: 'https://github.com/microsoft/vscode-codicons'
                        },
                        license: {
                            title: 'CC BY 4.0',
                            spdx: 'CC-BY-4.0',
                            url: 'https://raw.githubusercontent.com/microsoft/vscode-codicons/master/LICENSE'
                        },
                        version: '0.0.23',
                        samples: ['account', 'bell-dot', 'new-file'],
                        samplesHeight: 16,
                        height: [16, 24],
                        category: 'General',
                        palette: false,
                        hidden: true
                    })""",
                ),
            ),
        )
        assertEquals(16.0, codicons.displayHeight)
        assertEquals(true, codicons.hidden)
        assertEquals("CC-BY-4.0", codicons.license.spdx)
    }

    @Test
    fun keepsAlreadyConvertedInfo() {
        val source = jsObject(
            """({
                name: 'Font Awesome 4',
                total: 678,
                author: { name: 'Dave Gandy', url: 'http://fontawesome.io/' },
                license: {
                    title: 'Open Font License',
                    spdx: '',
                    url: 'http://scripts.sil.org/OFL'
                },
                samples: ['wrench', 'bell-o', 'user-o'],
                version: '4.7.0',
                height: 16,
                palette: false,
                category: 'General',
                tags: ['Foo', 'Bar']
            })""",
        ).unsafeCast<IconifyInfo>()

        val result = assertNotNull(convertIconSetInfo(source))
        assertEquals("Font Awesome 4", result.name)
        assertEquals(678.0, result.total)
        assertEquals("Dave Gandy", result.author.name)
        assertEquals("", result.license.spdx)
        assertContentEquals(arrayOf("wrench", "bell-o", "user-o"), result.samples)
        assertContentEquals(arrayOf("Foo", "Bar"), result.tags)
        assertEquals(false, result.palette)
    }

    @Test
    fun validatesIconSets() {
        assertFailsWith<Throwable> { validateIconSet(js("undefined")) }
        assertFailsWith<Throwable> { validateIconSet(jsObject("({})")) }
        assertFailsWith<Throwable> { validateIconSet(null) }
        assertFailsWith<Throwable> { validateIconSet(js("([])")) }

        val valid = validateIconSet(
            jsObject(
                """({
                    prefix: 'fòó_bār',
                    icons: {
                        bar: { body: '<g />' },
                        'fòó_': { body: '<g />' }
                    }
                })""",
            ),
        )
        assertEquals("fòó_bār", valid.prefix)
        assertContentEquals(arrayOf("bar", "fòó_"), iconNames(valid))

        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject("({ prefix: 'foo', icons: { '': { body: '<g />' } } })"),
            )
        }

        val fixed = validateIconSet(
            jsObject(
                """({
                    prefix: 'foo',
                    icons: { '': { body: '<g />' }, 'fòó_': { body: '<g />' } }
                })""",
            ),
            jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
        )
        assertContentEquals(arrayOf("fòó_"), iconNames(fixed))
    }

    @Test
    fun validatesMissingAndEmptyIcons() {
        assertFailsWith<Throwable> { validateIconSet(jsObject("({ prefix: 'foo' })")) }
        assertFailsWith<Throwable> { validateIconSet(jsObject("({ prefix: 'foo', icons: {} })")) }
    }

    @Test
    fun validatesCharacters() {
        val valid = validateIconSet(
            jsObject(
                """({
                    prefix: 'foo',
                    icons: { bar: { body: '<g />' } },
                    aliases: { baz: { parent: 'bar', hFlip: true } },
                    chars: { e00: 'bar', e01: 'baz' }
                })""",
            ),
        )
        assertEquals("bar", prop(valid.chars, "e00").unsafeCast<String>())
        assertEquals("baz", prop(valid.chars, "e01").unsafeCast<String>())

        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject(
                    """({
                        prefix: 'foo',
                        icons: { bar: { body: '<g />' } },
                        chars: { e01: 'baz' }
                    })""",
                ),
            )
        }

        val fixed = validateIconSet(
            jsObject(
                """({
                    prefix: 'foo',
                    icons: { bar: { body: '<g />' } },
                    chars: { e01: 'baz' }
                })""",
            ),
            jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
        )
        assertNull(fixed.chars)

        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject(
                    """({
                        prefix: 'foo',
                        icons: { bar: { body: '<g />' } },
                        chars: { test: 'bar' }
                    })""",
                ),
            )
        }

        val fixedChars = validateIconSet(
            jsObject(
                """({
                    prefix: 'foo',
                    icons: { bar: { body: '<g />' } },
                    chars: { 'e000-f123': 'bar', test: 'bar', E0: 'bar' }
                })""",
            ),
            jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
        )
        assertContentEquals(arrayOf("e000-f123"), keys(fixedChars.chars))
    }

    @Test
    fun rejectsInvalidDefaultValues() {
        val iconSetBase = """{
            prefix: 'foo',
            icons: { icon1: { body: '<path d="icon1" />' } },
            height: 24,
            %s
        }"""

        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject(iconSetBase.replace("%s", "width: { foo: 1 }")),
                jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
            )
        }
        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject(iconSetBase.replace("%s", "left: null")),
                jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
            )
        }
        assertFailsWith<Throwable> {
            validateIconSet(
                jsObject(iconSetBase.replace("%s", "width: '32'")),
                jsObject("({ fix: true })").unsafeCast<IconSetValidationOptions>(),
            )
        }
    }

    @Test
    fun quicklyValidatesIconSets() {
        val valid = jsObject(
            """({
                prefix: 'foo',
                icons: { bar: { body: '<g />' } },
                aliases: { baz: { parent: 'bar' } },
                chars: { e00: 'bar' }
            })""",
        )
        assertSame(valid, quicklyValidateIconSet(valid))

        assertNull(quicklyValidateIconSet(js("undefined")))
        assertNull(quicklyValidateIconSet(null))
        assertNull(quicklyValidateIconSet(jsObject("({})")))
        assertNull(quicklyValidateIconSet(js("([])")))
        assertNull(quicklyValidateIconSet(jsObject("({ prefix: 'foo' })")))
        assertNull(quicklyValidateIconSet(jsObject("({ prefix: 'foo', icons: 'bar' })")))
        // Optional property of the wrong type
        assertNull(
            quicklyValidateIconSet(jsObject("({ prefix: 'foo', icons: {}, width: '24' })")),
        )
        // Icon without body
        assertNull(
            quicklyValidateIconSet(jsObject("({ prefix: 'foo', icons: { bar: {} } })")),
        )
        // Empty icon name
        assertNull(
            quicklyValidateIconSet(jsObject("({ prefix: 'foo', icons: { '': { body: '<g />' } } })")),
        )
        // Icon with invalid optional value
        assertNull(
            quicklyValidateIconSet(
                jsObject("({ prefix: 'foo', icons: { bar: { body: '<g />', rotate: '1' } } })"),
            ),
        )
        // Bad alias
        assertNull(
            quicklyValidateIconSet(
                jsObject("({ prefix: 'foo', icons: {}, aliases: { baz: { parent: 'missing' } } })"),
            ),
        )
        // Valid alias to alias
        assertNotNull(
            quicklyValidateIconSet(
                jsObject(
                    """({
                        prefix: 'foo',
                        icons: { bar: { body: '<g />' } },
                        aliases: { baz: { parent: 'bar' }, qux: { parent: 'baz' } }
                    })""",
                ),
            ),
        )
    }

    @Test
    fun exposesCopiedProperties() {
        assertContentEquals(arrayOf("left", "top", "width", "height", "provider"), propsToCopy)
    }
}
