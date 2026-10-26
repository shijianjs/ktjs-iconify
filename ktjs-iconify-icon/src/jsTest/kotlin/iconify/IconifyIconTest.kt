package iconify

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import web.dom.document
import web.html.HTMLUnknownElement

class IconifyIconTest {
    private val homeIcon: IconifyIcon = js(
        """{ body: '<path d="M0 0h24v24H0z" fill="currentColor"/>', width: 24, height: 24 }"""
    )

    private fun shadowHtml(element: IconifyIconElement): String =
        js("(element) => element.shadowRoot ? element.shadowRoot.innerHTML : ''")(element)

    private fun attach(element: IconifyIconElement) {
        js("(element) => document.body.appendChild(element)")(element)
    }

    private fun detach(element: IconifyIconElement) {
        element.remove()
    }

    private fun awaitUntil(condition: () -> Boolean): Promise<Unit> =
        js(
            """(condition) => new Promise((resolve, reject) => {
                let attempts = 0;
                const check = () => {
                    if (condition()) return resolve();
                    if (++attempts > 200) {
                        return reject(new Error('Timed out waiting for condition'));
                    }
                    setTimeout(check, 10);
                };
                check();
            })"""
        )(condition).unsafeCast<Promise<Unit>>()

    private fun awaitStatus(element: IconifyIconElement, status: String): Promise<Unit> =
        awaitUntil { element.status == status }

    private fun jsObject(source: String): dynamic =
        js("(source) => eval('(' + source + ')')")(source)

    @Test
    fun registersCustomElement() {
        registerIconifyIcon()
        assertFalse(document.createElement("iconify-icon") is HTMLUnknownElement)
    }

    @Test
    fun createsElementFromTypedTagName() {
        registerIconifyIcon()
        val element = document.createElement(IconifyIconTagName.iconifyIcon)

        assertEquals("iconify-icon", element.tagName.lowercase())
    }

    @Test
    fun createsElement() {
        val element = createIconifyIcon("mdi:home") {
            width = "1em"
            height = "2em"
            mode = "mask"
            rotate = "90deg"
            flip = "horizontal"
            inline = true
            noobserver = true
        }

        assertEquals("iconify-icon", element.tagName.lowercase())
        assertEquals("mdi:home", element.icon.unsafeCast<String>())
        assertEquals("1em", element.width)
        assertEquals("2em", element.height)
        assertEquals("mask", element.mode)
        assertEquals("90deg", element.rotate)
        assertEquals("horizontal", element.flip)
        assertTrue(element.hasAttribute("inline"))
        assertTrue(element.hasAttribute("noobserver"))
    }

    @Test
    fun storesInlineIconData() {
        assertTrue(addIcon("test:home", homeIcon))

        assertTrue(iconLoaded("test:home"))
        val icon = getIcon("test:home")
        assertNotNull(icon)
        assertEquals("""<path d="M0 0h24v24H0z" fill="currentColor"/>""", icon.body)
        assertEquals(24.0, icon.width)
        assertEquals(24.0, icon.height)
        assertTrue(listIcons().contains("test:home"))
    }

    @Test
    fun fillsIconDefaults() {
        assertTrue(addIcon("test:plain", js("{ body: '<g/>' }")))

        val icon = getIcon("test:plain")
        assertNotNull(icon)
        assertEquals("<g/>", icon.body)
        assertEquals(16.0, icon.width)
        assertEquals(16.0, icon.height)
        assertEquals(0.0, icon.left)
        assertEquals(0.0, icon.top)
    }

    @Test
    fun handlesMissingIcons() {
        assertTrue(addIcon("test:gone", null))

        assertFalse(iconLoaded("test:gone"))
        assertNull(getIcon("test:gone"))
        assertFalse(listIcons().contains("test:gone"))
    }

    @Test
    fun rejectsInvalidData() {
        assertFalse(addIcon("", homeIcon))
        assertFalse(addIcon("::", homeIcon))
        assertFalse(addCollection(js("{ icons: {} }")))
    }

    @Test
    fun addsCollections() {
        val collection = js(
            """{
                prefix: 'testcollection',
                icons: {
                    arrow: { body: '<g id="arrow"/>' },
                    circle: { body: '<circle r="4"/>' }
                },
                width: 20,
                height: 20
            }"""
        )

        assertTrue(addCollection(collection))
        assertTrue(iconLoaded("testcollection:arrow"))
        assertTrue(iconLoaded("testcollection:circle"))
        assertTrue(listIcons().contains("testcollection:arrow"))

        val icons = listIcons("", "testcollection")
        assertEquals(listOf("testcollection:arrow", "testcollection:circle"), icons.sorted())

        // Missing icon in a collection
        val withMissing = js(
            """{
                prefix: 'testcollectionmissing',
                icons: {},
                not_found: ['gone']
            }"""
        )
        assertTrue(addCollection(withMissing))
        assertFalse(iconLoaded("testcollectionmissing:gone"))
    }

    @Test
    fun configuresAPIProviders() {
        assertTrue(
            addAPIProvider(
                "test-provider",
                jsObject("{resources: ['https://api.example.com']}")
                    .unsafeCast<IconifyAPIProviderConfig>()
            )
        )
        assertFalse(
            addAPIProvider(
                "empty-provider",
                jsObject("{resources: []}").unsafeCast<IconifyAPIProviderConfig>()
            )
        )
        assertFalse(
            addAPIProvider(
                "no-resources",
                jsObject("{}").unsafeCast<IconifyAPIProviderConfig>()
            )
        )
    }

    @Test
    fun rendersIconFromStorage(): Promise<Unit> {
        assertTrue(addIcon("render:home", homeIcon))
        val element = createIconifyIcon("render:home") {
            noobserver = true
            width = "2em"
            height = "2em"
        }
        attach(element)

        return awaitStatus(element, "rendered").then {
            val html = shadowHtml(element)
            assertTrue(html.contains("<svg"), html)
            assertTrue(html.contains("""width="2em""""), html)
            assertTrue(html.contains("""viewBox="0 0 24 24""""), html)

            detach(element)
        }
    }

    @Test
    fun rendersIconInBackgroundMode(): Promise<Unit> {
        assertTrue(addIcon("render:bg", homeIcon))
        val element = createIconifyIcon("render:bg") {
            noobserver = true
            mode = "bg"
        }
        attach(element)

        return awaitStatus(element, "rendered").then {
            val html = shadowHtml(element)
            assertTrue(html.contains("background-image"), html)

            detach(element)
        }
    }

    @Test
    fun rendersIconInMaskMode(): Promise<Unit> {
        assertTrue(addIcon("render:mask", homeIcon))
        val element = createIconifyIcon("render:mask") {
            noobserver = true
            mode = "mask"
        }
        attach(element)

        return awaitStatus(element, "rendered").then {
            val html = shadowHtml(element)
            assertTrue(html.contains("mask-image"), html)

            detach(element)
        }
    }

    @Test
    fun rendersIconObjects(): Promise<Unit> {
        // Object assigned directly to the property
        val element = document.createElement(IconifyIconTagName.iconifyIcon)
        registerIconifyIcon()
        element.icon = StringOrIconifyIcon(homeIcon)
        element.noobserver = true
        attach(element)

        return awaitStatus(element, "rendered").then {
            assertEquals("""<path d="M0 0h24v24H0z" fill="currentColor"/>""", element.icon.unsafeCast<IconifyIcon>().body)
            assertTrue(shadowHtml(element).contains("<svg"))
            detach(element)
        }
    }

    @Test
    fun rendersSerializedIcons(): Promise<Unit> {
        val serialized =
            """{"body":"<path d=\"M0 0h24v24H0z\"/>","width":24,"height":24}"""
        val element = createIconifyIcon(serialized) {
            noobserver = true
        }
        attach(element)

        return awaitStatus(element, "rendered").then {
            // Getter parses serialised data
            assertEquals("""<path d="M0 0h24v24H0z"/>""", element.icon.unsafeCast<IconifyIcon>().body)
            assertTrue(shadowHtml(element).contains("<svg"))
            detach(element)
        }
    }

    @Test
    fun reportsFailedStatusForMissingIcon(): Promise<Unit> {
        assertTrue(addIcon("render:missing", null))
        val element = createIconifyIcon("render:missing") {
            noobserver = true
        }
        attach(element)

        return awaitStatus(element, "failed").then {
            assertFalse(shadowHtml(element).contains("<svg"))
            detach(element)
        }
    }

    @Test
    fun restartsAnimations(): Promise<Unit> {
        assertTrue(addIcon("render:animated", homeIcon))
        val element = createIconifyIcon("render:animated") {
            noobserver = true
        }
        attach(element)

        return awaitStatus(element, "rendered").then {
            element.restartAnimation()
            assertEquals("rendered", element.status)
            assertTrue(shadowHtml(element).contains("<svg"))
            detach(element)
        }
    }

    @Test
    fun loadsStoredIcons(): Promise<Unit> {
        assertTrue(addIcon("loader:home", homeIcon))

        return Promise { resolve, reject ->
            loadIcons(arrayOf("loader:home")) { loaded, missing, pending, _ ->
                try {
                    assertEquals(1, loaded.size)
                    assertEquals("", loaded[0].provider)
                    assertEquals("loader", loaded[0].prefix)
                    assertEquals("home", loaded[0].name)
                    assertEquals(0, missing.size)
                    assertEquals(0, pending.size)
                    resolve(Unit)
                } catch (err: Throwable) {
                    reject(err)
                }
            }
        }
    }

    @Test
    fun loadsSingleStoredIcon(): Promise<Unit> {
        assertTrue(addIcon("loader:single", homeIcon))

        return loadIcon("loader:single").then { icon ->
            assertEquals("""<path d="M0 0h24v24H0z" fill="currentColor"/>""", icon.body)
            assertEquals(24.0, icon.width)
            assertEquals(24.0, icon.height)
        }
    }

    @Test
    fun rejectsInvalidIconLoad(): Promise<Unit> =
        loadIcon("::").then(
            { error("Expected loadIcon() to fail") },
            { _: Throwable -> }
        )
}
