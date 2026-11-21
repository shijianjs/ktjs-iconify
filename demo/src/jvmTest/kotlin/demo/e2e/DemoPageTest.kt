package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo: the shared shell runs, every route renders, the icon element actually
 * paints, the loader talks to the Iconify API, and a language switch rewrites the page in place.
 *
 * The shell lives in a shadow tree, so the demo's own nodes are selected through `#demo` and the page
 * element the shell mounts — never through the document root.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders the shell and the icon gallery`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-iconify"), "title: ${page.title()}")
        assertTrue(page.locator("#demo img.layout__logo").isVisible(), "the shell serves the project logo")
        assertEquals("Overview", page.locator("#demo iconify-gallery-page h1").first().textContent())
        assertEquals(5, page.locator("#demo vaadin-side-nav-item[data-route]").count())

        // The gallery is the landing page: eight cards, one icon each, all of them painted.
        assertEquals(8, page.locator("#demo iconify-gallery-page vaadin-card").count())
        waitForIcons(page)
        assertEveryIconRendered(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `transformations page applies the attributes`(page: Page) {
        openPage(page)
        openRoute(page, "transformations")
        assertEquals("Rotate and flip", page.locator("#demo iconify-transformations-page h1").first().textContent())

        val icons = page.locator("#demo iconify-transformations-page vaadin-card iconify-icon")
        assertEquals(6, icons.count(), "six orientations of the same icon")
        // Attributes, not CSS: the rotation is readable on the element itself.
        assertEquals("90deg", icons.nth(1).getAttribute("rotate"))
        assertEquals("horizontal", icons.nth(4).getAttribute("flip"))
        waitForIcons(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `modes page paints the three rendering modes`(page: Page) {
        openPage(page)
        openRoute(page, "modes")
        assertEquals("Rendering modes", page.locator("#demo iconify-modes-page h1").first().textContent())

        val icons = page.locator("#demo iconify-modes-page vaadin-card iconify-icon")
        assertEquals(3, icons.count())
        assertEquals("svg", icons.nth(0).getAttribute("mode"))
        assertEquals("bg", icons.nth(1).getAttribute("mode"))
        assertEquals("mask", icons.nth(2).getAttribute("mode"))
        waitForIcons(page)
        assertEveryIconRendered(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `utils page registers an icon locally and generates markup`(page: Page) {
        openPage(page)
        openRoute(page, "utils")
        assertEquals("SVG, CSS and registration", page.locator("#demo iconify-utils-page h1").first().textContent())

        // Registered from embedded data: no request is involved, so the readout is filled on first paint.
        val readout = page.locator("#demo iconify-utils-page .readout").first().textContent()!!
        assertTrue(readout.contains("demo:star"), "readout: $readout")
        assertTrue(readout.contains("24×24"), "readout: $readout")

        val svg = page.locator("#demo iconify-utils-page code").nth(1).textContent()!!
        assertTrue(svg.contains("<svg"), "generated markup: ${svg.take(80)}")
        assertTrue(svg.contains("transform"), "the customisation reached the markup: ${svg.take(120)}")
        waitForIcons(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `loader fetches an icon from the Iconify API`(page: Page) {
        openPage(page)
        openRoute(page, "loader")
        assertEquals("Load an icon by name", page.locator("#demo iconify-loader-page h1").first().textContent())

        page.locator("#demo iconify-loader-page vaadin-text-field").first().evaluate(
            "(el, value) => { el.value = value }",
            "mdi:home",
        )
        page.locator("#demo iconify-loader-page vaadin-button").first().click()
        page.waitForSelector("#demo iconify-loader-page .readout:has-text(\"mdi:home →\")")

        assertEquals(1, page.locator("#demo iconify-loader-page iconify-icon").count())
        waitForIcons(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch rewrites the page in place`(page: Page) {
        openPage(page)
        openRoute(page, "utils")
        val svgBefore = page.locator("#demo iconify-utils-page code").first().textContent()

        switchLanguage(page, "zh-Hans")
        assertEquals("SVG、CSS 与本地注册", page.locator("#demo iconify-utils-page h1").first().textContent())

        // The generated markup is data: switching the language must not re-run the conversion.
        assertEquals(svgBefore, page.locator("#demo iconify-utils-page code").first().textContent())
    }

    @RetryingTest(maxAttempts = 2)
    fun `a chinese browser starts in chinese`(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
        // The suite's context is English; the stored value is what carries the choice, so this test
        // starts from a clean one.
        page.evaluate("() => localStorage.removeItem('ktjs-iconify-language')")
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
        switchLanguage(page, "zh-Hans")
        assertEquals("概览", page.locator("#demo iconify-gallery-page h1").first().textContent())
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message ->
            if (message.type() == "error") consoleErrors += message.text()
        }
        page.onRequestFailed { request ->
            failedRequests += "${request.url()} ${request.failure()}"
        }

        openPage(page)
        waitForIcons(page)

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }

    /** Clicks the drawer item for [route]; the router is also what the menu drives. */
    private fun openRoute(page: Page, route: String) {
        page.locator("#demo vaadin-side-nav-item[data-route='$route']").first().click()
        page.waitForSelector("#demo iconify-$route-page h1")
    }

    /** Opens the layout's language menu and picks [code]. */
    private fun switchLanguage(page: Page, code: String) {
        page.locator("#demo [data-menu='lang']").first().click()
        page.locator("#demo [data-action='lang:$code']").first().click()
        page.waitForFunction("(expected) => document.documentElement.lang === expected", code)
    }

    /**
     * Iconify renders icons asynchronously, so wait until every `iconify-icon` reports `rendered`.
     *
     * Every page is its own element with its own shadow tree, so neither `document.querySelectorAll`
     * nor a query on the layout's shadow root reaches the icons — they would come back empty and the
     * wait would pass vacuously. The walk collects `iconify-icon` through every shadow root it meets.
     */
    private fun waitForIcons(page: Page) {
        page.waitForFunction(
            """
            () => {
              const icons = [];
              const walk = (root) => {
                for (const node of root.querySelectorAll('*')) {
                  if (node.tagName.toLowerCase() === 'iconify-icon') icons.push(node);
                  if (node.shadowRoot) walk(node.shadowRoot);
                }
              };
              walk(document);
              return icons.length > 0 && icons.every((node) => node.status === 'rendered');
            }
            """,
        )
    }

    private fun assertEveryIconRendered(page: Page) {
        val icons = page.locator("#demo iconify-icon")
        val total = icons.count()
        val rendered = icons.evaluateAll(
            "(nodes) => nodes.filter((node) => node.status === 'rendered').length",
        ) as Number
        assertTrue(total > 0, "the page renders icons")
        assertEquals(total, rendered.toInt(), "rendered icons")
    }
}
