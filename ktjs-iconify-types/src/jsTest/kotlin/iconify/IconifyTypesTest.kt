package iconify

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IconifyTypesTest {
    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    @Test
    fun readsIconSetJson() {
        val iconSet = js(
            """({
                prefix: 'mdi',
                provider: 'iconify',
                left: -2,
                top: 1,
                width: 24,
                height: 24,
                icons: {
                    home: { body: '<path d="M0 0h24v24H0z"/>' },
                    hidden: { body: '<path/>', hidden: true }
                },
                aliases: {
                    house: { parent: 'home' },
                    hiddenAlias: { parent: 'home', hidden: true }
                },
                info: {
                    name: 'Material Design Icons',
                    total: 7000,
                    version: '7.0.0',
                    author: { name: 'Pictogrammers', url: 'https://pictogrammers.com' },
                    license: {
                        title: 'Apache 2.0',
                        spdx: 'Apache-2.0',
                        url: 'https://www.apache.org/licenses/LICENSE-2.0'
                    },
                    samples: ['home', 'account'],
                    height: 24,
                    displayHeight: 20,
                    category: 'General',
                    tags: ['Material', 'UI'],
                    palette: false,
                    hidden: false
                },
                chars: { e900: 'home' },
                categories: { General: ['home'] },
                themes: { primary: { title: 'Primary' } },
                prefixes: { 'mdi-': 'Material Design Icons' },
                suffixes: { '-outline': 'Outline' },
                lastModified: 1700000000,
                not_found: ['missing']
            })""",
        ).unsafeCast<IconifyJSON>()

        assertEquals("mdi", iconSet.prefix)
        assertEquals("iconify", iconSet.provider)
        assertEquals(-2.0, iconSet.left)
        assertEquals(1.0, iconSet.top)
        assertEquals(24.0, iconSet.width)
        assertEquals(24.0, iconSet.height)
        assertEquals(2, keys(iconSet.icons).size)
        assertEquals(2, keys(iconSet.aliases).size)

        val info = assertNotNull(iconSet.info)
        assertEquals("Material Design Icons", info.name)
        assertEquals(7000.0, info.total)
        assertEquals("7.0.0", info.version)
        assertEquals("Pictogrammers", info.author.name)
        assertEquals("https://pictogrammers.com", info.author.url)
        assertEquals("Apache 2.0", info.license.title)
        assertEquals("Apache-2.0", info.license.spdx)
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0", info.license.url)
        assertContentEquals(arrayOf("home", "account"), info.samples)
        assertEquals(24.0, info.height.unsafeCast<Double>())
        assertEquals(20.0, info.displayHeight)
        assertEquals("General", info.category)
        assertContentEquals(arrayOf("Material", "UI"), info.tags)
        assertEquals(false, info.palette)
        assertEquals(false, info.hidden)

        assertEquals("home", iconSet.chars.unsafeCast<dynamic>().e900)
        assertContentEquals(
            arrayOf("home"),
            iconSet.categories.unsafeCast<dynamic>().General.unsafeCast<Array<String>>(),
        )
        assertEquals("Primary", iconSet.themes.unsafeCast<dynamic>().primary.title)
        assertEquals(
            "Material Design Icons",
            iconSet.prefixes.unsafeCast<dynamic>()["mdi-"] as String,
        )
        assertEquals("Outline", iconSet.suffixes.unsafeCast<dynamic>()["-outline"] as String)
        assertEquals(1700000000.0, iconSet.lastModified)
        assertContentEquals(arrayOf("missing"), iconSet.not_found)
    }

    @Test
    fun readsIconAndTransformations() {
        val icon = js(
            """({
                body: '<path/>',
                left: 1,
                top: 2,
                width: 32,
                height: 40,
                rotate: 1,
                hFlip: true,
                vFlip: false,
                hidden: true
            })""",
        ).unsafeCast<ExtendedIconifyIcon>()

        assertEquals("<path/>", icon.body)
        assertEquals(1.0, icon.left)
        assertEquals(2.0, icon.top)
        assertEquals(32.0, icon.width)
        assertEquals(40.0, icon.height)
        assertEquals(1.0, icon.rotate)
        assertEquals(true, icon.hFlip)
        assertEquals(false, icon.vFlip)
        assertEquals(true, icon.hidden)

        val alias = js(
            """({ parent: 'home', rotate: 2, hFlip: true, hidden: true })""",
        ).unsafeCast<ExtendedIconifyAlias>()

        assertEquals("home", alias.parent)
        assertEquals(2.0, alias.rotate)
        assertEquals(true, alias.hFlip)
        assertEquals(true, alias.hidden)

        // IconifyOptional only carries dimensions and transformations.
        val optional = icon.unsafeCast<IconifyOptional>()
        assertEquals(32.0, optional.width)
        assertEquals(1.0, optional.rotate)
    }

    @Test
    fun readsInfoWithArrayHeight() {
        val info = js(
            """({
                name: 'Test',
                author: { name: 'Author' },
                license: { title: 'MIT' },
                height: [24, 28]
            })""",
        ).unsafeCast<IconifyInfo>()

        assertTrue(js("Array.isArray").unsafeCast<(dynamic) -> Boolean>()(info.height))
        assertContentEquals(arrayOf(24.0, 28.0), info.height.unsafeCast<Array<Double>>())
        assertNull(info.author.url)
        assertNull(info.license.spdx)
        assertNull(info.total)
        assertNull(info.samples)
    }

    @Test
    fun readsPackageExports() {
        val exports = js(
            """({
                info: {
                    name: 'Material Design Icons',
                    author: { name: 'Pictogrammers' },
                    license: { title: 'Apache 2.0', spdx: 'Apache-2.0' },
                    height: 24
                },
                icons: {
                    prefix: 'mdi',
                    icons: { home: { body: '<path/>' } },
                    aliases: { house: { parent: 'home' } }
                },
                metadata: {
                    info: { name: 'Material Design Icons', author: { name: 'Pictogrammers' }, license: { title: 'Apache 2.0' }, height: 24 },
                    chars: { e900: 'home' },
                    categories: { General: ['home'] },
                    themes: {},
                    prefixes: {},
                    suffixes: {}
                },
                chars: { e900: 'home' }
            })""",
        ).unsafeCast<IconifyJSONPackageExports>()

        assertEquals("Material Design Icons", exports.info.name)
        assertEquals("mdi", exports.icons.prefix)
        assertEquals(1, keys(exports.icons.icons).size)
        assertEquals(1, keys(exports.icons.aliases).size)
        assertEquals("Material Design Icons", exports.metadata.info?.name)
        assertEquals("home", exports.metadata.chars.unsafeCast<dynamic>().e900)
        assertEquals("home", exports.chars.unsafeCast<dynamic>().e900)
    }

    @Test
    fun readsProviderData() {
        val provider = js(
            """({
                provider: 'iconify',
                title: 'Iconify',
                api: 'https://api.iconify.design',
                links: {
                    home: 'https://iconify.design',
                    collection: 'https://iconify.design/icon-sets/{prefix}/',
                    icon: 'https://iconify.design/icon-sets/{prefix}/{name}.html'
                },
                npm: {
                    package: '@iconify-json/{prefix}',
                    icon: '{prefix}:{name}'
                },
                svg: 'https://api.iconify.design/{prefix}/{name}.svg'
            })""",
        ).unsafeCast<APIProviderRawData>()

        assertEquals("iconify", provider.provider)
        assertEquals("Iconify", provider.title)
        assertEquals("https://api.iconify.design", provider.api.unsafeCast<String>())

        val links = assertNotNull(provider.links)
        assertEquals("https://iconify.design", links.home)
        assertEquals("https://iconify.design/icon-sets/{prefix}/", links.collection)
        assertEquals("https://iconify.design/icon-sets/{prefix}/{name}.html", links.icon)

        val npm = assertNotNull(provider.npm)
        assertEquals("@iconify-json/{prefix}", npm.`package`)
        assertEquals("{prefix}:{name}", npm.icon)

        assertEquals("https://api.iconify.design/{prefix}/{name}.svg", provider.svg)
    }

    @Test
    fun readsProviderDataWithArrayAPI() {
        val provider = js(
            """({ provider: 'custom', api: ['https://api.example.com', 'https://backup.example.com'] })""",
        ).unsafeCast<APIProviderRawData>()

        assertEquals("custom", provider.provider)
        assertContentEquals(
            arrayOf("https://api.example.com", "https://backup.example.com"),
            provider.api.unsafeCast<Array<String>>(),
        )
        assertNull(provider.title)
        assertNull(provider.links)
        assertNull(provider.npm)
        assertNull(provider.svg)
    }

    @Test
    fun allowsOmittingOptionalFields() {
        val icon = js("""({ body: '<path/>' })""").unsafeCast<ExtendedIconifyIcon>()
        assertEquals("<path/>", icon.body)
        assertNull(icon.hidden)
        assertNull(icon.left)
        assertNull(icon.rotate)

        val json = js("""({ prefix: 'test', icons: {} })""").unsafeCast<IconifyJSON>()
        assertEquals("test", json.prefix)
        assertNull(json.provider)
        assertNull(json.aliases)
        assertNull(json.info)
        assertNull(json.lastModified)
        assertNull(json.not_found)

        val info = js(
            """({ name: 'Test', author: { name: 'Author' }, license: { title: 'MIT' }, height: 16 })""",
        ).unsafeCast<IconifyInfo>()
        assertNull(info.total)
        assertNull(info.version)
        assertNull(info.displayHeight)
        assertNull(info.category)
        assertNull(info.tags)
        assertNull(info.palette)
        assertNull(info.hidden)
    }

    @Test
    fun modifiesWritableProperties() {
        val icon = js("""({ body: '<path/>', rotate: 1 })""").unsafeCast<IconifyIcon>()
        icon.rotate = 3.0
        icon.hFlip = true
        icon.width = 24.0
        assertEquals(3.0, icon.rotate)
        assertEquals(true, icon.hFlip)
        assertEquals(24.0, icon.width)

        val info = js(
            """({ name: 'Test', author: { name: 'Author' }, license: { title: 'MIT' }, height: 16 })""",
        ).unsafeCast<IconifyInfo>()
        info.total = 10.0
        info.hidden = true
        assertEquals(10.0, info.total)
        assertEquals(true, info.hidden)
    }

    @Test
    fun readsMissingRequiredPropertyAsUndefined() {
        // The wrappers are type declarations: a missing required property is still undefined at
        // runtime, it is not enforced by the module.
        val icon = js("({})").unsafeCast<IconifyIcon>()
        assertEquals("undefined", js("typeof icon.body"))
        assertFailsWith<Throwable> {
            icon.body.length
        }
    }
}
