package io.github.shijianjs.ktjs.iconify.demo

import io.github.shijianjs.ktjs.demo.i18n.LanguageState
import io.github.shijianjs.ktjs.demo.layout.DemoLayout
import io.github.shijianjs.ktjs.demo.layout.LayoutConfig
import io.github.shijianjs.ktjs.demo.style.Theme
import web.dom.ElementId
import web.dom.document
import io.github.shijianjs.ktjs.dom.element.invoke

/**
 * Entry point of the demo page.
 *
 * The <ktjs-demo-layout> comes from index.html: the markup names it, this file defines the tag and
 * mounts the menu into the already-connected element.
 */
fun main() {
    val language = LanguageState(storageKey = "ktjs-iconify-language")
    language.init()

    val theme = Theme()
    theme.applyStored()

    // The companion registers the tag in its `init`; reading `NAME` upgrades the element the page already spells.
    DemoLayout.NAME
    val layout = document.getElementById(ElementId("demo")) as? DemoLayout
        ?: error("index.html has no <ktjs-demo-layout id=\"demo\">")

    layout.mount(
        LayoutConfig(
            menu = siteMenu(),
            language = language,
            theme = theme,
            texts = siteTexts,
            siteVersion = SITE_VERSION,
            docsUrl = DOCS_URL,
            apiUrl = API_URL,
            sourceUrl = SOURCE_URL,
            logoSrc = "img/ktjs-iconify-logo.png",
        ),
    )
}
