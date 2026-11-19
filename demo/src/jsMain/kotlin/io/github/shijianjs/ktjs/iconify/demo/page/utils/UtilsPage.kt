package io.github.shijianjs.ktjs.iconify.demo.page.utils

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
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.iconify.addIcon
import io.github.shijianjs.ktjs.iconify.createIconifyIcon
import io.github.shijianjs.ktjs.iconify.IconifyIcon
import io.github.shijianjs.ktjs.iconify.getIcon
import io.github.shijianjs.ktjs.iconify.utils.IconifyIconCustomisations
import io.github.shijianjs.ktjs.iconify.utils.StringOrDouble
import io.github.shijianjs.ktjs.iconify.utils.colorToString
import io.github.shijianjs.ktjs.iconify.utils.compareColors
import io.github.shijianjs.ktjs.iconify.utils.getEmojiSequenceFromString
import io.github.shijianjs.ktjs.iconify.utils.getEmojiSequenceKeyword
import io.github.shijianjs.ktjs.iconify.utils.getIconCSS
import io.github.shijianjs.ktjs.iconify.utils.iconToHTML
import io.github.shijianjs.ktjs.iconify.utils.iconToSVG
import io.github.shijianjs.ktjs.iconify.utils.stringToColor
import io.github.shijianjs.ktjs.iconify.utils.stringToIcon
import js.objects.Record
import kotlin.js.js
import kotlin.js.unsafeCast
import web.html.HTMLElement

/** An icon registered from data embedded in this page — no request, no API dependency. */
private val starIconData = js(
    """{
        body: '<path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01z"/>',
        width: 24,
        height: 24
    }""",
)

private class UtilsTexts(
    val title: String,
    val summary: String,
    val registrationTitle: String,
    val registrationHint: String,
    val registrationNote: String,
    val svgTitle: String,
    val svgHint: String,
    val colourLine: String,
    val emojiLine: String,
    val nameLine: String,
    val cssTitle: String,
    val cssHint: String,
)

private val english: UtilsTexts = UtilsTexts(
    title = "SVG, CSS and registration",
    summary = "`ktjs-iconify-utils` turns icon data into markup and rules, and the element accepts data " +
        "registered locally — the build-time path.",
    registrationTitle = "Registered from data",
    registrationHint = "`addIcon(\"demo:star\", data)` puts the icon in storage; `getIcon` reads it back.",
    registrationNote = "Nothing here touched the network: the data object above is the whole icon.",
    svgTitle = "Generated markup",
    svgHint = "`iconToSVG` merges the customisations, `iconToHTML` serialises the result.",
    colourLine = "stringToColor(\"#ff8800\") vs rgb(255, 136, 0)",
    emojiLine = "getEmojiSequenceFromString(\"1f600\")",
    nameLine = "stringToIcon(\"mdi:home\")",
    cssTitle = "Generated CSS",
    cssHint = "`getIconCSS` writes one rule for mask or background mode — one sheet instead of one " +
        "element per icon.",
)

private val chinese: UtilsTexts = UtilsTexts(
    title = "SVG、CSS 与本地注册",
    summary = "`ktjs-iconify-utils` 把图标数据变成标记和规则；元素也接受本地注册的数据，这就是构建期那条路。",
    registrationTitle = "用数据注册",
    registrationHint = "`addIcon(\"demo:star\", data)` 把图标写进存储，`getIcon` 再读回来。",
    registrationNote = "这里没有访问网络：上面那个数据对象就是整个图标。",
    svgTitle = "生成的标记",
    svgHint = "`iconToSVG` 合并定制项，`iconToHTML` 把结果序列化。",
    colourLine = "stringToColor(\"#ff8800\") 与 rgb(255, 136, 0)",
    emojiLine = "getEmojiSequenceFromString(\"1f600\")",
    nameLine = "stringToIcon(\"mdi:home\")",
    cssTitle = "生成的 CSS",
    cssHint = "`getIconCSS` 为 mask 或 background 模式写一条规则——一张表，而不是每个图标一个元素。",
)

private val texts: Translations<UtilsTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** Three sections: local registration, SVG generation and CSS generation. */
internal class UtilsPage : DemoPage() {
    private val binder = Binder(texts)


    /** Computed once: the values are deterministic, so a language switch must not re-run them. */
    private val storedBodyLength: Int
    private val storedWidth: Int
    private val storedHeight: Int

    init {
        addIcon("demo:star", starIconData.unsafeCast<IconifyIcon>())
        val stored = getIcon("demo:star")
        storedBodyLength = stored?.body?.length ?: 0
        storedWidth = (stored?.width ?: 0.0).toInt()
        storedHeight = (stored?.height ?: 0.0).toInt()

        val registrationSection = sectionBlock(english.registrationTitle, english.registrationHint)
        binder.bind(registrationSection.title) { it.registrationTitle }
        binder.bind(registrationSection.hint) { it.registrationHint }

        val readout = Readout()
        readout.set("demo:star · body ${storedBodyLength} chars · ${storedWidth}×${storedHeight}")
        registrationSection.body.append(
            row(
                createIconifyIcon("demo:star") { width = "2.5rem"; height = "2.5rem" },
                readout.root,
            ),
            codeText("addIcon(\"demo:star\", js(\"{ body, width, height }\"))"),
        )

        val registrationNote = paragraph(english.registrationNote)
        binder.bind(registrationNote) { it.registrationNote }
        registrationSection.body.append(registrationNote)

        val svgSection = sectionBlock(english.svgTitle, english.svgHint)
        binder.bind(svgSection.title) { it.svgTitle }
        binder.bind(svgSection.hint) { it.svgHint }

        // Computed once. The three lines below are bound rather than written, so a language switch
        // rewrites their label and keeps the measured value — it never re-runs the conversion.
        val built = buildSvg()
        val colourLine = paragraph()
        binder.bind(colourLine) { it.colourLine + ": " + built.colour }
        val emojiLine = paragraph()
        binder.bind(emojiLine) { it.emojiLine + " → " + built.emoji }
        val nameLine = paragraph()
        binder.bind(nameLine) { it.nameLine + " → " + built.name }

        svgSection.body.append(exampleBox(null, codeText(built.svg)), colourLine, emojiLine, nameLine)

        val cssSection = sectionBlock(english.cssTitle, english.cssHint)
        binder.bind(cssSection.title) { it.cssTitle }
        binder.bind(cssSection.hint) { it.cssHint }
        cssSection.body.append(codeText(buildCss()))

        body.append(registrationSection.root, svgSection.root, cssSection.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = texts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
    }

    companion object : CustomElementReference<UtilsPage>(UtilsPage::class.js) {
        val NAME: TagName<UtilsPage> = TagName("iconify-utils-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** The values one run of the utils examples produced. */
private class SvgReport(val svg: String, val colour: String, val emoji: String, val name: String)

private fun buildSvg(): SvgReport {
    val icon = getIcon("demo:star") ?: return SvgReport("(icon data unavailable)", "—", "—", "—")
    val customisations = js("({})").unsafeCast<IconifyIconCustomisations>().apply {
        width = StringOrDouble("2em")
        height = StringOrDouble("2em")
        hFlip = true
        rotate = 1.0
    }
    val built = iconToSVG(icon, customisations)

    val orange = stringToColor("#ff8800")
    val rgb = stringToColor("rgb(255, 136, 0)")
    val same = orange != null && rgb != null && compareColors(orange, rgb)
    val hex = orange?.let { colorToString(it) } ?: "null"

    val sequence = getEmojiSequenceFromString("1f600")
    val glyph = js("(codes) => String.fromCodePoint.apply(null, codes)")(sequence) as String
    val keyword = getEmojiSequenceKeyword(sequence)
    val parsed = stringToIcon("mdi:home")

    return SvgReport(
        svg = iconToHTML(built.body, built.attributes.unsafeCast<Record<String, String>>()),
        colour = "$hex · equal to rgb(255, 136, 0): $same",
        emoji = "$glyph ($keyword)",
        name = "provider=\"${parsed?.provider ?: ""}\", prefix=\"${parsed?.prefix ?: ""}\", " +
            "name=\"${parsed?.name ?: ""}\"",
    )
}

private fun buildCss(): String {
    val icon = getIcon("demo:star") ?: return "(icon data unavailable)"
    return getIconCSS(icon, js("({ iconSelector: '.demo-star' })"))
}

/** Creates a defined `<iconify-utils-page>`. */
internal fun newUtilsPage(): UtilsPage {
    UtilsPage.define()
    return UtilsPage.NAME()
}
