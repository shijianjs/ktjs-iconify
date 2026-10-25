package demo.e2e

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/demo-page.test.mjs`, the standalone Node/Playwright script the demo used to
 * carry. The script ran every check against one page in sequence; here each concern is its own
 * test that opens the page from scratch, which also lets a failure retry on its own.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    private val enTitle = "Kotlin/JS wrappers for Iconify"
    private val enHeading = "Typed Kotlin/JS wrappers for Iconify"
    private val zhTitle = "ktjs-iconify · Iconify 的 Kotlin/JS 封装"
    private val zhHeading = "为 Iconify 打造的 Kotlin/JS 类型化封装"

    @RetryingTest(maxAttempts = 2)
    fun `english page renders localized copy and every icon`(page: Page) {
        openPage(page)
        waitForIcons(page)

        assertEquals("en", documentLanguage(page))
        assertThat(page).hasTitle(Pattern.compile(enTitle))
        assertThat(page.locator(".hero h1")).hasText(enHeading)
        assertEquals("中文", page.locator(".lang-toggle").textContent())
        assertEquals("Switch to Chinese", page.locator(".lang-toggle").getAttribute("aria-label"))
        assertEquals("Loading uses loadIcon() from the Iconify API.", page.locator(".loader-status").textContent())
        assertEquals("Load icon", page.locator(".loader .button").textContent())
        assertEveryIconRendered(page)
    }

    @RetryingTest(maxAttempts = 2)
    fun `language switch localizes the page and persists across reloads`(page: Page) {
        openPage(page)
        switchLanguage(page, "zh")

        assertEquals("zh", documentLanguage(page))
        assertEquals(zhTitle, page.title())
        assertThat(page.locator(".hero h1")).hasText(zhHeading)
        assertEquals("English", page.locator(".lang-toggle").textContent())
        assertEquals("切换到英文", page.locator(".lang-toggle").getAttribute("aria-label"))

        val sectionTitles = page.locator(".section-head h2").allTextContents()
        assertEquals(5, sectionTitles.size)
        assertEquals("来自 Iconify API 的图标", sectionTitles.first())

        val inline = page.locator(".inline-demo").textContent()
        assertTrue(inline.startsWith("传入 "), inline)
        assertTrue(inline.endsWith("，让图标与周围文字对齐。"), inline)

        // The first five cards are the icon catalog, the rest are the three render modes.
        val modes = page.locator(".row .card-name").allTextContents().drop(5)
        assertEquals(listOf("默认 <svg> 渲染", "背景图", "CSS mask，跟随 currentColor"), modes)

        val storageInfo = page.locator(".row .muted").first().textContent()
        assertTrue(storageInfo.contains("字符"), storageInfo)

        assertEquals("加载使用 Iconify API 的 loadIcon()。", page.locator(".loader-status").textContent())
        assertEquals("加载图标", page.locator(".loader .button").textContent())
        assertEquals("图标名称", page.locator(".loader-input").getAttribute("aria-label"))
        assertEquals("zh", storedLanguage(page))
        assertEveryIconRendered(page)

        page.reload(Page.ReloadOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
        waitForIcons(page)
        assertEquals("zh", documentLanguage(page))
        assertThat(page.locator(".hero h1")).hasText(zhHeading)

        switchLanguage(page, "en")
        assertThat(page.locator(".hero h1")).hasText(enHeading)
        assertEquals("Loading uses loadIcon() from the Iconify API.", page.locator(".loader-status").textContent())
        assertEquals("en", storedLanguage(page))
    }

    @RetryingTest(maxAttempts = 2)
    fun `loader fetches an icon from the Iconify API`(page: Page) {
        openPage(page)
        switchLanguage(page, "zh")

        page.locator(".loader-input").fill("mdi:home")
        page.locator(".loader .button").click()
        page.waitForFunction(
            "() => (document.querySelector('.loader-status')?.textContent || '').startsWith('已加载 mdi:home')",
            null,
            Page.WaitForFunctionOptions().setTimeout(15_000.0),
        )

        assertThat(page.locator(".loader-output iconify-icon")).hasCount(1)
    }

    @RetryingTest(maxAttempts = 2)
    fun `a chinese browser defaults to chinese`(browser: Browser) {
        browser.newContext(
            Browser.NewContextOptions()
                .setBaseURL(DemoServer.baseUrl)
                .setLocale("zh-CN")
                .setViewportSize(1280, 1000),
        ).use { context ->
            val page = context.newPage()
            openPage(page)
            waitForIcons(page)

            assertEquals("zh", documentLanguage(page))
            assertThat(page.locator(".hero h1")).hasText(zhHeading)
        }
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

    /** Iconify renders icons asynchronously, so wait until every `iconify-icon` reports `rendered`. */
    private fun waitForIcons(page: Page) {
        page.waitForFunction(
            "() => [...document.querySelectorAll('iconify-icon')].every((node) => node.status === 'rendered')",
        )
    }

    private fun assertEveryIconRendered(page: Page) {
        val icons = page.locator("iconify-icon")
        val total = icons.count()
        val rendered = icons.evaluateAll(
            "(nodes) => nodes.filter((node) => node.status === 'rendered').length",
        ) as Number
        assertTrue(total > 0, "the page renders icons")
        assertEquals(total, rendered.toInt(), "rendered icons")
    }

    private fun switchLanguage(page: Page, lang: String) {
        page.locator(".lang-toggle").click()
        page.waitForFunction("(expected) => document.documentElement.lang === expected", lang)
    }

    private fun documentLanguage(page: Page): String? = page.locator("html").getAttribute("lang")

    private fun storedLanguage(page: Page): Any? =
        page.evaluate("() => localStorage.getItem('ktjs-iconify-demo-language')")
}
