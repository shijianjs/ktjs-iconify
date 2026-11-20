package io.github.shijianjs.ktjs.iconify.demo.page.element

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.iconify.IconifyIconElement
import io.github.shijianjs.ktjs.iconify.createIconifyIcon
import web.dom.document
import web.html.HTMLElement

/** One transformation example: the caption is data (an attribute value), not copy. */
private class Transform(val caption: String, val configure: IconifyIconElement.() -> Unit)

private val transforms: List<Transform> = listOf(
    Transform("rotate 0°") { },
    Transform("rotate 90°") { rotate = "90deg" },
    Transform("rotate 180°") { rotate = "180deg" },
    Transform("rotate 270°") { rotate = "270deg" },
    Transform("flip horizontal") { flip = "horizontal" },
    Transform("flip vertical") { flip = "vertical" },
)

private class TransformationsTexts(
    val title: String,
    val summary: String,
    val transformTitle: String,
    val transformHint: String,
    val inlineTitle: String,
    val inlineHint: String,
)

private val english: TransformationsTexts = TransformationsTexts(
    title = "Rotate and flip",
    summary = "`rotate` and `flip` are element attributes rather than CSS transforms, so they also work " +
        "in the modes where a CSS transform would not.",
    transformTitle = "Six orientations of the same icon",
    transformHint = "All six cards render `mdi:arrow-right`; only the attributes differ.",
    inlineTitle = "Inline rendering",
    inlineHint = "`inline = true` drops the shadow DOM, so the glyph behaves like a character in the text run.",
)

private val chinese: TransformationsTexts = TransformationsTexts(
    title = "旋转与镜像",
    summary = "`rotate` 与 `flip` 是元素属性而不是 CSS transform，所以在 CSS transform 不起作用的模式下同样有效。",
    transformTitle = "同一个图标的六种朝向",
    transformHint = "六张卡片渲染的都是 `mdi:arrow-right`，只有属性不同。",
    inlineTitle = "内联渲染",
    inlineHint = "`inline = true` 不使用 shadow DOM，字形就像文本里的一个字符。",
)

private val texts: Translations<TransformationsTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** Two sections: the attribute matrix, and what `inline` changes. */
internal class TransformationsPage : DemoPage() {
    private val binder = Binder(texts)


    init {
        val transformSection = sectionBlock(english.transformTitle, english.transformHint)
        binder.bind(transformSection.heading) { it.transformTitle }
        binder.bind(transformSection.hint) { it.transformHint }

        val cards = transforms.map { transform ->
            exampleBox(
                transform.caption,
                createIconifyIcon("mdi:arrow-right") {
                    width = "2.5rem"
                    height = "2.5rem"
                    transform.configure(this)
                },
            )
        }
        transformSection.body.append(grid(minColumnWidth = 160, *cards.toTypedArray()))

        val inlineSection = sectionBlock(english.inlineTitle, english.inlineHint)
        binder.bind(inlineSection.heading) { it.inlineTitle }
        binder.bind(inlineSection.hint) { it.inlineHint }

        val sentence = paragraph()
        sentence.append(
            document.createTextNode("Inline: "),
            createIconifyIcon("mdi:heart") { inline = true },
            document.createTextNode(" → one character, one baseline, one line height."),
        )
        inlineSection.body.append(sentence, codeText("createIconifyIcon(\"mdi:heart\") { inline = true }"))

        body.append(transformSection.root, inlineSection.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = texts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
    }

    companion object : CustomElementReference<TransformationsPage>(TransformationsPage::class.js) {
        val NAME: TagName<TransformationsPage> = TagName("iconify-transformations-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<iconify-transformations-page>`. */
internal fun newTransformationsPage(): TransformationsPage {
    TransformationsPage.define()
    return TransformationsPage.NAME()
}
