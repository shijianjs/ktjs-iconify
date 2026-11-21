package io.github.shijianjs.ktjs.iconify.demo.page.element

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.codeText
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.iconify.createIconifyIcon
import web.cssom.Color

/** One section of mode cards; the mask example is tinted to show that it takes the text colour. */
internal class ModesPage : DemoPage() {
    private val binder = Binder(texts)

    init {
        val section = SectionBlock.NAME { show(english.sectionTitle, english.sectionHint) }
        binder.bind(section.heading) { it.sectionTitle }
        binder.bind(section.hint) { it.sectionHint }

        val cards = modes.map { example ->
            val element = createIconifyIcon(example.icon) {
                mode = example.mode
                width = "2.5rem"
                height = "2.5rem"
                if (example.mode == "mask") inlineStyle.apply { color = Color("var(--lumo-primary-color)") }
            }
            exampleBox(example.name, element)
        }
        section.body.append(Grid.NAME { show(180); add(*cards.toTypedArray()) })

        val note = paragraph(english.maskNote)
        binder.bind(note) { it.maskNote }
        section.body.append(note, codeText("createIconifyIcon(\"mdi:star\") { mode = \"mask\" }"))

        body.append(section.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = texts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
    }

    companion object : ModesPageDefaultReference() {
        private val modes: List<ModeExample> = listOf(
            ModeExample("svg", "svg", "mdi:star"),
            ModeExample("bg", "bg", "twemoji:star"),
            ModeExample("mask", "mask", "mdi:star"),
        )
        private val english: ModesTexts = ModesTexts(
            title = "Rendering modes",
            summary = "The same catalogue can be painted four ways; the mode decides what the browser receives.",
            sectionTitle = "Three modes side by side",
            sectionHint = "`svg` builds an element, `bg` paints a background image, `mask` uses the glyph as a " +
                "CSS mask so it takes `color`.",
            maskNote = "The mask card is tinted through `color` alone — the icon data carries no fill of its own.",
        )
        private val chinese: ModesTexts = ModesTexts(
            title = "渲染模式",
            summary = "同一套图标可以用四种方式画出来，模式决定浏览器最终拿到什么。",
            sectionTitle = "三种模式并排",
            sectionHint = "`svg` 生成元素，`bg` 画成背景图，`mask` 把字形当 CSS 遮罩用，因此吃 `color`。",
            maskNote = "遮罩那张卡片只靠 `color` 上色——图标数据本身不带填充色。",
        )
        private val texts: Translations<ModesTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
