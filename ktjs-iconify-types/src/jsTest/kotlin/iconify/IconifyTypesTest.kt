package iconify

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals

class IconifyTypesTest {
    private fun keys(value: dynamic): Array<String> =
        js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

    @Test
    fun readsIconSetJson() {
        val iconSet = js(
            """({
                prefix: 'mdi',
                width: 24,
                height: 24,
                icons: { home: { body: '<path d="M0 0h24v24H0z"/>' } },
                aliases: { house: { parent: 'home' } },
                info: {
                    name: 'Material Design Icons',
                    author: { name: 'Pictogrammers' },
                    license: { title: 'Apache 2.0', spdx: 'Apache-2.0' }
                }
            })""",
        ).unsafeCast<IconifyJSON>()

        assertEquals("mdi", iconSet.prefix)
        assertEquals(24.0, iconSet.width)
        assertEquals("Material Design Icons", iconSet.info?.name)
        assertEquals("Apache-2.0", iconSet.info?.license?.spdx)
        assertEquals(1, keys(iconSet.icons).size)
        assertEquals(1, keys(iconSet.aliases).size)
    }

    @Test
    fun readsIconAndTransformations() {
        val icon = js("""({ body: '<path/>', rotate: 1, hFlip: true, width: 32 })""")
            .unsafeCast<ExtendedIconifyIcon>()

        assertEquals("<path/>", icon.body)
        assertEquals(1.0, icon.rotate)
        assertEquals(true, icon.hFlip)
        assertEquals(32.0, icon.width)
    }
}
