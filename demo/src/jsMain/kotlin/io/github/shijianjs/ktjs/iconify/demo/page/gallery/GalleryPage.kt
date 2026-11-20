package io.github.shijianjs.ktjs.iconify.demo.page.gallery

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.iconify.createIconifyIcon

/** The icon names this page shows; a name is data, so it is never translated. */
private val featuredIcons: List<String> = listOf(
    "mdi:home",
    "mdi:rocket-launch",
    "lucide:sparkles",
    "devicon:kotlin",
    "twemoji:unicorn",
    "mdi:github",
    "ph:palette",
    "simple-icons:iconify",
)

private val english: GalleryTexts = GalleryTexts(
    title = "Overview",
    summary = "A gallery of icons the wrapper renders with nothing but a name: no SVG markup, no " +
        "icon font, no CSS sprite.",
    sectionTitle = "Featured icons",
    sectionHint = "Each card is one <iconify-icon> element. The caption is the icon name that produced it.",
    note = "Names come from any registered Iconify set. An unknown name loads from the Iconify API on " +
        "first render, and the wrapper reports the failure through `loadIcon` when you ask for it " +
        "explicitly.",
    docsLink = "How the element works",
)

private val chinese: GalleryTexts = GalleryTexts(
    title = "概览",
    summary = "这里每张卡片只有一个图标名：没有 SVG 标记、没有图标字体，也没有 CSS 雪碧图。",
    sectionTitle = "精选图标",
    sectionHint = "每张卡片就是一个 <iconify-icon> 元素，标题是生成它的图标名。",
    note = "名字可以来自任何已注册的 Iconify 图标集。没注册过的名字会在首次渲染时向 Iconify API 拉取；" +
        "显式调用 `loadIcon` 时，失败会以异常形式返回。",
    docsLink = "图标元素详解",
)

private val texts: Translations<GalleryTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** The landing page: one section of cards, one card per icon. */
internal class GalleryPage : DemoPage() {
    private val binder = Binder(texts)

    init {
        binder.bind(anchor(english.docsLink, "docs/guide/icon-element")) { it.docsLink }
            .also { addLink(it) }

        val section = SectionBlock.NAME { show(english.sectionTitle, english.sectionHint) }
        binder.bind(section.heading) { it.sectionTitle }
        binder.bind(section.hint) { it.sectionHint }

        val cards = featuredIcons.map { name ->
            exampleBox(name, createIconifyIcon(name) { width = "2rem"; height = "2rem" })
        }
        section.body.append(Grid.NAME { show(200); add(*cards.toTypedArray()) })

        val note = paragraph(english.note)
        binder.bind(note) { it.note }
        section.body.append(note)

        body.append(section.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = texts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
    }

    companion object : CustomElementReferenceAdapter<GalleryPage>(GalleryPage::class.js, "iconify-gallery-page")
}
