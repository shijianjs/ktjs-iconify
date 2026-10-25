package demo.e2e

import com.microsoft.playwright.Browser
import com.microsoft.playwright.assertions.PlaywrightAssertions
import com.microsoft.playwright.junit.Options
import com.microsoft.playwright.junit.OptionsFactory
import java.nio.file.Path

/**
 * Mirrors the browser settings the old Node script used: an English browser at 1280×1000, with
 * traces kept only when a test fails.
 */
class DemoOptions : OptionsFactory {
    override fun getOptions(): Options {
        PlaywrightAssertions.setDefaultAssertionTimeout(7_000.0)
        return Options()
            .setBaseUrl(DemoServer.baseUrl)
            .setTrace(Options.Trace.RETAIN_ON_FAILURE)
            .setOutputDir(Path.of(System.getProperty("e2e.output", "build/e2e-results")))
            .setContextOptions(
                Browser.NewContextOptions()
                    .setLocale("en-US")
                    .setViewportSize(1280, 1000),
            )
    }
}
