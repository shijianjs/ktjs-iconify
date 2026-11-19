package io.github.shijianjs.ktjs.iconify.demo

import io.github.shijianjs.ktjs.demo.nav.DemoMenu
import io.github.shijianjs.ktjs.demo.nav.Entry
import io.github.shijianjs.ktjs.demo.nav.Group
import io.github.shijianjs.ktjs.iconify.demo.page.element.newModesPage
import io.github.shijianjs.ktjs.iconify.demo.page.element.newTransformationsPage
import io.github.shijianjs.ktjs.iconify.demo.page.gallery.newGalleryPage
import io.github.shijianjs.ktjs.iconify.demo.page.loader.newLoaderPage
import io.github.shijianjs.ktjs.iconify.demo.page.utils.newUtilsPage

/** The upstream npm releases the wrappers follow; the navbar shows the Kotlin version instead. */
const val SITE_VERSION: String = "Kotlin/JS 2.4.21"

/** The documentation site. */
const val DOCS_URL: String = "https://shijianjs.github.io/ktjs-iconify/"

/** Dokka reference assembled by pagesSite next to the demo. */
const val API_URL: String = "https://shijianjs.github.io/ktjs-iconify/api/"

/** Source repository, linked from the navbar's link menu. */
const val SOURCE_URL: String = "https://github.com/shijianjs/ktjs-iconify"

internal val START: Group = Group("start", "lucide:sparkles")

internal val ELEMENT: Group = Group("element", "lucide:component")

internal val UTILS: Group = Group("utils", "lucide:braces")

/**
 * The site: one entry per page, and one page per capability the wrappers cover.
 *
 * Adding a page means adding one entry here and its titles in [siteTexts].
 */
fun siteMenu(): DemoMenu = DemoMenu(
    listOf(
        Entry("overview", START) { newGalleryPage() },
        Entry("transformations", ELEMENT) { newTransformationsPage() },
        Entry("modes", ELEMENT) { newModesPage() },
        Entry("utils", UTILS) { newUtilsPage() },
        Entry("loader", UTILS) { newLoaderPage() },
    ),
)
