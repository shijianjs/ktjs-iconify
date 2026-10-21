package demo

import iconify.IconifyIconElement
import iconify.addIcon
import iconify.createIconifyIcon
import iconify.getIcon
import iconify.loadIcon
import iconify.utils.IconifyIconCustomisations
import iconify.utils.colorToString
import iconify.utils.compareColors
import iconify.utils.getEmojiSequenceFromString
import iconify.utils.getEmojiSequenceKeyword
import iconify.utils.iconToHTML
import iconify.utils.iconToSVG
import iconify.utils.stringToColor
import iconify.utils.stringToIcon
import kotlin.js.js
import kotlin.js.unsafeCast
import web.dom.TagName
import web.dom.Text
import web.dom.document
import web.events.addEventHandler
import web.html.ButtonType
import web.html.HTMLAnchorElement
import web.html.HTMLButtonElement
import web.html.HTMLInputElement
import web.html.HTMLElement
import web.html.HtmlTagName
import web.html.InputType
import web.html.button
import web.html.text
import web.keyboard.KEY_DOWN
import web.keyboard.KeyboardEvent
import web.navigator.navigator
import web.pointer.CLICK
import web.pointer.PointerEvent
import web.storage.localStorage

private const val GITHUB_URL = "https://github.com/shijianjs/ktjs-iconify"
private const val LANGUAGE_STORAGE_KEY = "ktjs-iconify-demo-language"

private val featuredIcons = listOf(
    "mdi:home",
    "mdi:rocket-launch",
    "lucide:sparkles",
    "devicon:kotlin",
    "twemoji:unicorn",
    "mdi:github",
    "ph:palette",
    "simple-icons:iconify",
)

fun main() {
    DemoPage().mount()
}

private data class StorageInfo(val bodyLength: Int, val width: Int, val height: Int)

private sealed interface LoaderState {
    data object Idle : LoaderState
    data object MissingName : LoaderState
    data class Loading(val name: String) : LoaderState
    data class Loaded(val name: String, val width: Int, val height: Int) : LoaderState
    data class Failed(val name: String, val error: String?) : LoaderState
}

/** Section heading plus its muted description. */
private class SectionBlock {
    val root: HTMLElement = element(HtmlTagName.section, "section")

    private val titleNode: HTMLElement = element(HtmlTagName.h2)
    private val descriptionNode: HTMLElement = element(HtmlTagName.p, "muted")

    init {
        root.append(
            element(HtmlTagName.div, "section-head").apply {
                append(titleNode, descriptionNode)
            },
        )
    }

    fun setCopy(title: String, description: String) {
        titleNode.textContent = title
        descriptionNode.textContent = description
    }
}

/** Icon loader form; keeps its last state so switching language can re-localize it. */
private class LoaderPanel(initialName: String) {
    val root: HTMLElement = element(HtmlTagName.div, "stack")

    private val input: HTMLInputElement = element(HtmlTagName.input, "loader-input").apply {
        type = InputType.text
        placeholder = "mdi:home"
        value = initialName
    }
    private val button: HTMLButtonElement = element(HtmlTagName.button, "button primary").apply {
        type = ButtonType.button
    }
    private val status: HTMLElement = element(HtmlTagName.p, "loader-status muted").apply {
        setAttribute("aria-live", "polite")
    }
    private val output: HTMLElement = element(HtmlTagName.div, "loader-output")

    private var state: LoaderState = LoaderState.Idle
    private var strings: Strings = stringsFor(Language.English)

    init {
        root.append(element(HtmlTagName.div, "loader").apply { append(input, button) }, status, output)
        button.addEventHandler(PointerEvent.CLICK, { _: PointerEvent -> submit() })
        input.addEventHandler(KeyboardEvent.KEY_DOWN, { event: KeyboardEvent ->
            if (event.key == "Enter") submit()
        })
        showState()
    }

    fun setCopy(strings: Strings) {
        this.strings = strings
        button.textContent = strings.loaderButton
        input.setAttribute("aria-label", strings.loaderInputLabel)
        showState()
    }

    private fun setState(state: LoaderState) {
        this.state = state
        showState()
    }

    private fun showState() {
        status.textContent = when (val current = state) {
            LoaderState.Idle -> strings.loaderIdle
            LoaderState.MissingName -> strings.loaderMissingName()
            is LoaderState.Loading -> strings.loaderLoading(current.name)
            is LoaderState.Loaded -> strings.loaderLoaded(current.name, current.width, current.height)
            is LoaderState.Failed -> strings.loaderFailed(current.name, current.error ?: strings.unknownError)
        }
        button.disabled = state is LoaderState.Loading
    }

    private fun submit() {
        if (state is LoaderState.Loading) return

        val name = input.value.trim()
        if (name.isEmpty()) {
            setState(LoaderState.MissingName)
            return
        }
        setState(LoaderState.Loading(name))
        loadIcon(name).then(
            { icon ->
                val iconWidth = icon.width ?: 0.0
                val iconHeight = icon.height ?: 0.0
                output.replaceChildren(
                    createIconifyIcon(name) {
                        width = "3rem"
                        height = "3rem"
                    },
                )
                setState(LoaderState.Loaded(name, iconWidth.toInt(), iconHeight.toInt()))
            },
            { error: Throwable ->
                output.replaceChildren()
                setState(LoaderState.Failed(name, error.message))
            },
        )
    }
}

private class DemoPage {
    private val root: HTMLElement = document.body

    private var language: Language = Language.English

    private lateinit var languageButton: HTMLButtonElement
    private lateinit var navDemo: HTMLAnchorElement
    private lateinit var navApi: HTMLAnchorElement
    private lateinit var heroTitle: HTMLElement
    private lateinit var heroLead: HTMLElement
    private lateinit var heroApiAction: HTMLAnchorElement
    private lateinit var heroGithubAction: HTMLAnchorElement
    private lateinit var featuredSection: SectionBlock
    private lateinit var transformationsSection: SectionBlock
    private lateinit var modesSection: SectionBlock
    private lateinit var storageSection: SectionBlock
    private lateinit var loaderSection: SectionBlock
    private lateinit var inlinePrefix: Text
    private lateinit var inlineSuffix: Text
    private lateinit var modeSvg: HTMLElement
    private lateinit var modeBackground: HTMLElement
    private lateinit var modeMask: HTMLElement
    private lateinit var storageInfoNode: HTMLElement
    private lateinit var utilsReportNode: HTMLElement
    private lateinit var loaderPanel: LoaderPanel
    private lateinit var footerLicense: HTMLElement
    private lateinit var footerApi: HTMLAnchorElement

    private var storageInfo: StorageInfo? = null
    private var utilsReport: String? = null

    fun mount() {
        val header = buildHeader()
        val main = element(HtmlTagName.main, "page")
        main.append(
            buildHero(),
            buildFeaturedSection(),
            buildTransformationsSection(),
            buildModesSection(),
            buildStorageSection(),
            buildLoaderSection(),
        )
        root.replaceChildren(header, main, buildFooter())

        languageButton.addEventHandler(PointerEvent.CLICK, { _: PointerEvent ->
            val next = language.other()
            localStorage.setItem(LANGUAGE_STORAGE_KEY, next.code)
            setLanguage(next)
        })
        setLanguage(initialLanguage())
    }

    private fun buildHeader(): HTMLElement {
        val header = element(HtmlTagName.header, "topbar")
        val inner = element(HtmlTagName.div, "topbar-inner")
        val brand = element(HtmlTagName.a, "brand").apply {
            textContent = "ktjs-iconify"
            setAttribute("href", "./")
        }
        navDemo = navLink("", "./")
        navApi = navLink("", "api/")
        languageButton = element(HtmlTagName.button, "lang-toggle").apply {
            type = ButtonType.button
        }
        val nav = element(HtmlTagName.nav, "nav").apply {
            append(
                navDemo,
                navApi,
                navLink("GitHub", GITHUB_URL, external = true),
                navLink("README", "$GITHUB_URL#readme", external = true),
                languageButton,
            )
        }
        inner.append(brand, nav)
        header.append(inner)
        return header
    }

    private fun buildHero(): HTMLElement {
        val hero = element(HtmlTagName.section, "hero")
        heroTitle = element(HtmlTagName.h1)
        heroLead = element(HtmlTagName.p, "lead")
        heroApiAction = actionLink("", "api/", primary = true)
        heroGithubAction = actionLink("", GITHUB_URL, external = true)
        val actions = element(HtmlTagName.div, "actions").apply {
            append(heroApiAction, heroGithubAction)
        }
        hero.append(
            textElement(HtmlTagName.span, "ICONIFY × KOTLIN/JS", "kicker"),
            heroTitle,
            heroLead,
            actions,
        )
        return hero
    }

    private fun buildFeaturedSection(): HTMLElement {
        val grid = element(HtmlTagName.div, "grid")
        featuredIcons.forEach { grid.appendChild(iconCard(it)) }
        featuredSection = SectionBlock().apply { root.append(grid) }
        return featuredSection.root
    }

    private fun buildTransformationsSection(): HTMLElement {
        val row = element(HtmlTagName.div, "row")
        row.append(
            iconCard("mdi:arrow-right", "rotate 0"),
            iconCard("mdi:arrow-right", "rotate 90", configure = { rotate = "90deg" }),
            iconCard("mdi:arrow-right", "rotate 180", configure = { rotate = "180deg" }),
            iconCard("mdi:arrow-right", "flip H", configure = { flip = "horizontal" }),
            iconCard("mdi:arrow-right", "flip V", configure = { flip = "vertical" }),
        )

        inlinePrefix = document.createTextNode("")
        inlineSuffix = document.createTextNode("")
        val inline = element(HtmlTagName.p, "inline-demo").apply {
            append(inlinePrefix)
            append(createIconifyIcon("mdi:heart") { inline = true })
            append(document.createTextNode(" inline = true"))
            append(inlineSuffix)
        }

        val content = element(HtmlTagName.div, "stack").apply {
            append(row, inline)
        }
        transformationsSection = SectionBlock().apply { root.append(content) }
        return transformationsSection.root
    }

    private fun buildModesSection(): HTMLElement {
        modeSvg = element(HtmlTagName.span, "card-name")
        modeBackground = element(HtmlTagName.span, "card-name")
        modeMask = element(HtmlTagName.span, "card-name")

        val row = element(HtmlTagName.div, "row").apply {
            append(
                modeCard("mdi:star", "svg", modeSvg),
                modeCard("twemoji:star", "bg", modeBackground),
                modeCard("mdi:star", "mask", modeMask),
            )
        }
        modesSection = SectionBlock().apply { root.append(row) }
        return modesSection.root
    }

    private fun buildStorageSection(): HTMLElement {
        addIcon("demo:star", starIconData)

        val icon = createIconifyIcon("demo:star") {
            width = "2.5rem"
            height = "2.5rem"
        }
        storageInfoNode = element(HtmlTagName.p, "muted")
        utilsReportNode = element(HtmlTagName.pre, "output")

        val data = getIcon("demo:star")
        storageInfo = data?.let {
            StorageInfo(
                bodyLength = it.body.length,
                width = (it.width ?: 0.0).toInt(),
                height = (it.height ?: 0.0).toInt(),
            )
        }
        utilsReport = buildUtilsReport()

        val content = element(HtmlTagName.div, "stack").apply {
            append(
                element(HtmlTagName.div, "row").apply {
                    append(icon, storageInfoNode)
                },
                utilsReportNode,
            )
        }
        storageSection = SectionBlock().apply { root.append(content) }
        return storageSection.root
    }

    private fun buildUtilsReport(): String? {
        val icon = getIcon("demo:star") ?: return null
        val customisations = js("({})").unsafeCast<IconifyIconCustomisations>().apply {
            width = "2em"
            height = "2em"
            hFlip = true
            rotate = 1.0
        }
        val built = iconToSVG(icon, customisations)
        val svg = iconToHTML(built.body, built.attributes)

        val orange = stringToColor("#ff8800")
        val rgb = stringToColor("rgb(255, 136, 0)")
        val same = orange != null && rgb != null && compareColors(orange, rgb)
        val hex = orange?.let { colorToString(it) } ?: "null"

        val sequence = getEmojiSequenceFromString("1f600")
        val glyph = js("(codes) => String.fromCodePoint.apply(null, codes)")(sequence) as String
        val keyword = getEmojiSequenceKeyword(sequence)
        val name = stringToIcon("mdi:home")

        return buildString {
            appendLine("iconToSVG() + iconToHTML():")
            appendLine(svg)
            appendLine()
            appendLine(
                "stringToIcon(\"mdi:home\") -> provider=\"${name?.provider ?: ""}\", " +
                    "prefix=\"${name?.prefix ?: ""}\", name=\"${name?.name ?: ""}\"",
            )
            appendLine("stringToColor(\"#ff8800\") -> $hex; equals rgb(255, 136, 0): $same")
            appendLine("emoji 1f600 -> $glyph ($keyword)")
        }
    }

    private fun buildLoaderSection(): HTMLElement {
        loaderPanel = LoaderPanel("ph:rocket-launch")
        loaderSection = SectionBlock().apply { root.append(loaderPanel.root) }
        return loaderSection.root
    }

    private fun buildFooter(): HTMLElement {
        val footer = element(HtmlTagName.footer, "footer")
        footerLicense = element(HtmlTagName.span, "muted")
        footerApi = navLink("", "api/")
        val inner = element(HtmlTagName.div, "footer-inner").apply {
            append(
                footerLicense,
                element(HtmlTagName.nav, "nav").apply {
                    append(
                        footerApi,
                        navLink("Maven Central", "https://central.sonatype.com/namespace/io.github.shijianjs", external = true),
                        navLink("GitHub", GITHUB_URL, external = true),
                    )
                },
            )
        }
        footer.append(inner)
        return footer
    }

    private fun setLanguage(language: Language) {
        this.language = language
        val strings = stringsFor(language)

        document.documentElement.setAttribute("lang", language.code)
        document.title = strings.documentTitle
        languageButton.textContent = language.switchLabel
        languageButton.setAttribute("aria-label", strings.switchLanguageAria)

        navDemo.textContent = strings.navDemo
        navApi.textContent = strings.navApi
        heroTitle.textContent = strings.heroTitle
        heroLead.textContent = strings.heroLead
        heroApiAction.textContent = strings.heroApiAction
        heroGithubAction.textContent = strings.heroGithubAction

        featuredSection.setCopy(strings.featuredTitle, strings.featuredDescription)
        transformationsSection.setCopy(strings.transformationsTitle, strings.transformationsDescription)
        modesSection.setCopy(strings.modesTitle, strings.modesDescription)
        storageSection.setCopy(strings.storageTitle, strings.storageDescription)
        loaderSection.setCopy(strings.loaderTitle, strings.loaderDescription)

        inlinePrefix.data = strings.inlinePrefix
        inlineSuffix.data = strings.inlineSuffix
        modeSvg.textContent = strings.modeSvg
        modeBackground.textContent = strings.modeBackground
        modeMask.textContent = strings.modeMask

        storageInfoNode.textContent = storageInfo?.let {
            strings.storageInfo(it.bodyLength, it.width, it.height)
        } ?: strings.storageInfoUnavailable
        utilsReportNode.textContent = utilsReport ?: strings.storageInfoUnavailable

        loaderPanel.setCopy(strings)
        footerLicense.textContent = strings.footerLicense
        footerApi.textContent = strings.footerApi
    }

    private fun iconCard(
        name: String,
        caption: String = name,
        configure: IconifyIconElement.() -> Unit = {},
    ): HTMLElement {
        val card = element(HtmlTagName.div, "card")
        val icon = createIconifyIcon(name) {
            width = "2rem"
            height = "2rem"
            configure()
        }
        card.append(icon, textElement(HtmlTagName.code, caption, "card-name"))
        return card
    }

    private fun modeCard(name: String, mode: String, caption: HTMLElement): HTMLElement {
        val card = element(HtmlTagName.div, "card")
        val icon = createIconifyIcon(name) {
            width = "2rem"
            height = "2rem"
            this.mode = mode
        }
        if (mode == "mask") {
            icon.style.color = "#8b5cf6"
        }
        card.append(icon, caption)
        return card
    }

    private fun navLink(label: String, href: String, external: Boolean = false): HTMLAnchorElement =
        element(HtmlTagName.a, "nav-link").apply {
            textContent = label
            setAttribute("href", href)
            if (external) {
                setAttribute("target", "_blank")
                setAttribute("rel", "noreferrer")
            }
        }

    private fun actionLink(label: String, href: String, primary: Boolean = false, external: Boolean = false): HTMLAnchorElement =
        element(HtmlTagName.a, if (primary) "button primary" else "button").apply {
            textContent = label
            setAttribute("href", href)
            if (external) {
                setAttribute("target", "_blank")
                setAttribute("rel", "noreferrer")
            }
        }
}

private fun initialLanguage(): Language =
    Language.fromCode(localStorage.getItem(LANGUAGE_STORAGE_KEY))
        ?: if (navigator.language.lowercase().startsWith("zh")) Language.Chinese else Language.English

private fun <T : HTMLElement> element(tag: TagName<T>, classNames: String = ""): T =
    document.createElement(tag).apply {
        if (classNames.isNotEmpty()) setAttribute("class", classNames)
    }

private fun <T : HTMLElement> textElement(tag: TagName<T>, value: String, className: String = ""): T =
    element(tag, className).apply {
        textContent = value
    }

private val starIconData = js(
    """{
        body: '<path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01z"/>',
        width: 24,
        height: 24
    }""",
)
