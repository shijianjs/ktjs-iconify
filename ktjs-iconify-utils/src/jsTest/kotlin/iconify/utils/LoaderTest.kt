package iconify.utils

import iconify.IconifyJSON
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoaderTest {
    private fun options(json: String): IconifyLoaderOptions =
        jsObject(json).unsafeCast<IconifyLoaderOptions>()

    private fun inlineCollections(name: String, icon: String, svg: String): dynamic =
        js("(name, icon, svg) => { const collections = {}; collections[name] = {}; collections[name][icon] = svg; return collections; }")(name, icon, svg)

    private fun loaderCollections(name: String, svg: String): dynamic =
        js("(name, svg) => { const collections = {}; collections[name] = () => svg; return collections; }")(name, svg)

    private fun loader(svg: String): dynamic = js("(svg) => () => svg")(svg)

    private fun assertContains(expected: String, actual: String?) {
        assertNotNull(actual)
        assertTrue(actual.contains(expected), "Expected '$actual' to contain '$expected'")
    }

    @Test
    fun loadsIconFromInlineCollection(): Promise<Unit> {
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"
        val loaderOptions = options("{}")
        loaderOptions.customCollections = inlineCollections("a", "circle", svg)

        return loadIcon("a", "circle", loaderOptions).then { result ->
            assertEquals(svg, result)
            Unit
        }
    }

    @Test
    fun loadsIconFromLoaderFunction(): Promise<Unit> {
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"
        val loaderOptions = options("{}")
        loaderOptions.customCollections = loaderCollections("a", svg)

        return loadIcon("a", "circle", loaderOptions).then { result ->
            assertEquals(svg, result)
            Unit
        }
    }

    @Test
    fun returnsNullForUnknownCollection(): Promise<Unit> {
        return loadIcon("missing", "circle", options("{}")).then { result ->
            assertNull(result)
            Unit
        }
    }

    @Test
    fun appliesCustomisationsWhenLoadingIcon(): Promise<Unit> {
        val svg =
            "<svg viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"
        val customizations = jsObject("{}")
        val transform: (String) -> String = { value ->
            value.replace("<svg ", "<svg width=\"1em\" height=\"1em\" ")
        }
        customizations.transform = transform

        val loaderOptions = options("{}")
        loaderOptions.customCollections = inlineCollections("a", "circle", svg)
        loaderOptions.customizations = customizations

        return loadIcon("a", "circle", loaderOptions).then { result ->
            assertContains("width=\"1em\"", result)
            assertContains("height=\"1em\"", result)
            Unit
        }
    }

    @Test
    fun loadsCustomIconDirectly(): Promise<Unit> {
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"

        return getCustomIcon(loader(svg), "a", "b").then { result ->
            assertEquals(svg, result)
            Unit
        }
    }

    @Test
    fun addsNamespaces(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"

        return getCustomIcon(loader(svg), "a", "b", options("{ addXmlNs: true }")).then { result ->
            assertEquals(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
                result,
            )
            Unit
        }
    }

    @Test
    fun trimsCustomSVG(): Promise<Unit> {
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\">\n" +
            "<circle cx=\"60\" cy=\"60\" r=\"50\"/>\n</svg>\n"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options("{ customizations: { trimCustomSvg: true } }"),
        ).then { result ->
            assertEquals(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
                result,
            )
            Unit
        }
    }

    @Test
    fun cleansUpSVGPreface(): Promise<Unit> {
        val svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
            "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\">\n" +
            "<circle cx=\"60\" cy=\"60\" r=\"50\"/>\n</svg>\n"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options("{ customizations: { trimCustomSvg: true } }"),
        ).then { result ->
            assertEquals(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>",
                result,
            )
            Unit
        }
    }

    @Test
    fun scalesCustomIcon(): Promise<Unit> {
        val svg =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\"><path d=\"M0 0h512v512H0z\"/></svg>"

        return getCustomIcon(loader(svg), "a", "b", options("{ scale: 2 }")).then { result ->
            assertContains(" width=\"2em\"", result)
            assertContains(" height=\"2em\"", result)
            Unit
        }
    }

    @Test
    fun disablesScaleForCustomIcon(): Promise<Unit> {
        val svg =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\"><path d=\"M0 0h512v512H0z\"/></svg>"

        return getCustomIcon(loader(svg), "a", "b", options("{ scale: 0 }")).then { result ->
            assertNotNull(result)
            assertTrue(!result.contains(" width=\""))
            assertTrue(!result.contains(" height=\""))
            Unit
        }
    }

    @Test
    fun doesNotScaleNonSquareIcon(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 90 120\"><circle cx=\"45\" cy=\"60\" r=\"30\"/></svg>"

        return getCustomIcon(loader(svg), "a", "b", options("{ addXmlNs: true }")).then { result ->
            assertEquals(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 90 120\"><circle cx=\"45\" cy=\"60\" r=\"30\"/></svg>",
                result,
            )
            Unit
        }
    }

    @Test
    fun recalculatesWidthFromExplicitHeight(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 640 512\"><path d=\"M0 0h640v512H0z\"/></svg>"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options("{ scale: 1.2, customizations: { additionalProps: { height: '2em' } } }"),
        ).then { result ->
            assertContains("width=\"2.5em\"", result)
            assertContains("height=\"2em\"", result)
            Unit
        }
    }

    @Test
    fun recalculatesHeightFromExplicitWidth(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 640 512\"><path d=\"M0 0h640v512H0z\"/></svg>"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options("{ scale: 1.2, customizations: { additionalProps: { width: '2em' } } }"),
        ).then { result ->
            assertContains("width=\"2em\"", result)
            assertContains("height=\"1.6em\"", result)
            Unit
        }
    }

    @Test
    fun omitsSuppressedDimensions(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 640 512\"><path d=\"M0 0h640v512H0z\"/></svg>"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options(
                "{ scale: 1.2, customizations: { additionalProps: { width: 'unset', height: 'none' } } }",
            ),
        ).then { result ->
            assertNotNull(result)
            assertTrue(!result.contains("width=\""))
            assertTrue(!result.contains("height=\""))
            Unit
        }
    }

    @Test
    fun keepsExplicitDimensionWhileOmittingSuppressedSide(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 640 512\"><path d=\"M0 0h640v512H0z\"/></svg>"

        return getCustomIcon(
            loader(svg),
            "a",
            "b",
            options(
                "{ scale: 1.2, customizations: { additionalProps: { width: 'unset', height: '2em' } } }",
            ),
        ).then { result ->
            assertNotNull(result)
            assertTrue(!result.contains("width=\""))
            assertContains("height=\"2em\"", result)
            Unit
        }
    }

    @Test
    fun collectsUsedProps(): Promise<Unit> {
        val svg = "<svg viewBox=\"0 0 512 512\"><path d=\"M0 0h512v512H0z\"/></svg>"
        val usedProps = jsObject("{}")
        val customizations = jsObject("{ additionalProps: { width: '4em', height: '4em' } }")
        val loaderOptions = options("{ scale: 2 }")
        loaderOptions.customizations = customizations
        loaderOptions.usedProps = usedProps

        return getCustomIcon(loader(svg), "a", "b", loaderOptions).then { result ->
            assertContains("width=\"4em\"", result)
            assertContains("height=\"4em\"", result)
            assertEquals("4em", prop(usedProps, "width").unsafeCast<String>())
            assertEquals("4em", prop(usedProps, "height").unsafeCast<String>())
            Unit
        }
    }

    @Test
    fun searchesForIconByName(): Promise<Unit> {
        val iconSet = jsObject(
            "{ prefix: 'test', icons: { 'home': { body: '<path d=\"M0 0h16v16z\" fill=\"currentColor\"/>' } } }",
        ).unsafeCast<IconifyJSON>()

        return searchForIcon(iconSet, "test", arrayOf("missing", "home")).then { result ->
            assertContains("<path d=\"M0 0h16v16z\" fill=\"currentColor\"/>", result)
            Unit
        }
    }

    @Test
    fun returnsNullWhenSearchFindsNothing(): Promise<Unit> {
        val iconSet = jsObject(
            "{ prefix: 'test', icons: { 'home': { body: '<path d=\"M0 0h16v16z\"/>' } } }",
        ).unsafeCast<IconifyJSON>()

        return searchForIcon(iconSet, "test", arrayOf("missing")).then { result ->
            assertNull(result)
            Unit
        }
    }

    @Test
    fun mergesIconProps(): Promise<Unit> {
        val svg =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 120\"><circle cx=\"60\" cy=\"60\" r=\"50\"/></svg>"
        val loaderOptions = options("{ scale: 2, defaultClass: 'icon' }")

        val promise: Promise<String> = mergeIconProps(svg, "a", "b", loaderOptions)
        return promise.then { result ->
            assertContains(" width=\"2em\"", result)
            assertContains(" height=\"2em\"", result)
            assertContains("class=\"icon\"", result)
            Unit
        }
    }
}
