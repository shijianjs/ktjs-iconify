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
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import web.dom.TagName
import web.dom.document
import web.html.HTMLAnchorElement
import web.html.HTMLButtonElement
import web.html.HTMLInputElement
import web.html.HTMLElement
import web.html.HtmlTagName
import web.html.InputType
import web.html.text
import web.keyboard.KEY_DOWN
import web.keyboard.KeyboardEvent
import web.events.addEventHandler
import web.pointer.CLICK
import web.pointer.PointerEvent

private const val GITHUB_URL = "https://github.com/shijianjs/ktjs-iconify"

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

private class DemoPage {
    private val root: HTMLElement = document.body

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
    }

    private fun buildHeader(): HTMLElement {
        val header = element(HtmlTagName.header, "topbar")
        val inner = element(HtmlTagName.div, "topbar-inner")
        val brand = element(HtmlTagName.a, "brand").apply {
            textContent = "ktjs-iconify"
            setAttribute("href", "./")
        }
        val nav = element(HtmlTagName.nav, "nav").apply {
            append(
                navLink("Demo", "./"),
                navLink("API docs", "api/"),
                navLink("GitHub", GITHUB_URL, external = true),
                navLink("README", "$GITHUB_URL#readme", external = true),
            )
        }
        inner.append(brand, nav)
        header.append(inner)
        return header
    }

    private fun buildHero(): HTMLElement {
        val hero = element(HtmlTagName.section, "hero")
        val actions = element(HtmlTagName.div, "actions").apply {
            append(
                actionLink("Browse the API reference", "api/", primary = true),
                actionLink("View on GitHub", GITHUB_URL, external = true),
            )
        }
        hero.append(
            textElement(HtmlTagName.span, "ICONIFY × KOTLIN/JS", "kicker"),
            textElement(HtmlTagName.h1, "Typed Kotlin/JS wrappers for Iconify"),
            textElement(
                HtmlTagName.p,
                "Render icons with the official web component, parse icon sets, and build SVG, " +
                    "CSS and emoji helpers from Kotlin/JS — without hand-written externals.",
                "lead",
            ),
            actions,
        )
        return hero
    }

    private fun buildFeaturedSection(): HTMLElement {
        val grid = element(HtmlTagName.div, "grid")
        featuredIcons.forEach { grid.appendChild(iconCard(it)) }
        return section(
            "Icons from the Iconify API",
            "Each tile is a real <iconify-icon> element created through ktjs-iconify-icon.",
            grid,
        )
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

        val inline = element(HtmlTagName.p, "inline-demo").apply {
            append(document.createTextNode("Pass "))
            append(createIconifyIcon("mdi:heart") { inline = true })
            append(document.createTextNode(" inline = true "))
            append(document.createTextNode("to keep the icon aligned with the surrounding text."))
        }

        val content = element(HtmlTagName.div, "stack").apply {
            append(row, inline)
        }
        return section(
            "Transformations and inline icons",
            "rotate, flip, width and height map directly to the web component attributes.",
            content,
        )
    }

    private fun buildModesSection(): HTMLElement {
        val row = element(HtmlTagName.div, "row")
        row.append(
            modeCard("mdi:star", "svg", "Default <svg> rendering"),
            modeCard("twemoji:star", "bg", "Background image"),
            modeCard("mdi:star", "mask", "CSS mask, follows currentColor"),
        )
        return section("Rendering modes", "Pick the element output that fits your layout.", row)
    }

    private fun buildStorageSection(): HTMLElement {
        addIcon("demo:star", starIconData)

        val icon = createIconifyIcon("demo:star") {
            width = "2.5rem"
            height = "2.5rem"
        }
        val info = element(HtmlTagName.p, "muted")
        val data = getIcon("demo:star")
        info.textContent = if (data == null) {
            "getIcon() returned null"
        } else {
            "getIcon(\"demo:star\") -> body ${data.body.length} chars, ${data.width} × ${data.height}"
        }

        val output = element(HtmlTagName.pre, "output")
        output.textContent = buildUtilsReport()

        val content = element(HtmlTagName.div, "stack").apply {
            append(
                element(HtmlTagName.div, "row").apply {
                    append(icon, info)
                },
                output,
            )
        }
        return section(
            "Local icons and @iconify/utils",
            "addIcon() stores data without the network; the report below comes from iconToSVG(), " +
                "iconToHTML(), stringToIcon(), colors and emoji helpers.",
            content,
        )
    }

    private fun buildUtilsReport(): String {
        val icon = getIcon("demo:star") ?: return "Icon data is not available."
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
        val input = element(HtmlTagName.input, "loader-input").apply {
            type = InputType.text
            placeholder = "mdi:home"
            value = "ph:rocket-launch"
            setAttribute("aria-label", "Icon name")
        }
        val button = element(HtmlTagName.button, "button primary").apply {
            textContent = "Load icon"
        }
        val status = textElement(
            HtmlTagName.p,
            "Loading uses loadIcon() from the Iconify API.",
            "loader-status muted",
        ).apply {
            setAttribute("aria-live", "polite")
        }
        val output = element(HtmlTagName.div, "loader-output")
        val form = element(HtmlTagName.div, "loader").apply {
            append(input, button)
        }

        fun submit() {
            loadInto(input.value, button, status, output)
        }

        button.addEventHandler(PointerEvent.CLICK, { _: PointerEvent -> submit() })
        input.addEventHandler(KeyboardEvent.KEY_DOWN, { event: KeyboardEvent ->
            if (event.key == "Enter") submit()
        })

        val content = element(HtmlTagName.div, "stack").apply {
            append(form, status, output)
        }
        return section(
            "Load any icon",
            "Type a name such as mdi:home or @provider:prefix:name; the icon is fetched on demand.",
            content,
        )
    }

    private fun buildFooter(): HTMLElement {
        val footer = element(HtmlTagName.footer, "footer")
        val inner = element(HtmlTagName.div, "footer-inner").apply {
            append(
                textElement(HtmlTagName.span, "ktjs-iconify · MIT-licensed wrappers for Iconify", "muted"),
                element(HtmlTagName.nav, "nav").apply {
                    append(
                        navLink("API docs", "api/"),
                        navLink("Maven Central", "https://central.sonatype.com/namespace/io.github.shijianjs", external = true),
                        navLink("GitHub", GITHUB_URL, external = true),
                    )
                },
            )
        }
        footer.append(inner)
        return footer
    }

    private fun loadInto(
        name: String,
        button: HTMLButtonElement,
        status: HTMLElement,
        output: HTMLElement,
    ) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            status.textContent = "Enter an icon name first."
            return
        }
        button.disabled = true
        status.textContent = "Loading $trimmed…"
        val completion: Promise<Unit> = loadIcon(trimmed).then(
            { icon ->
                val iconWidth = icon.width ?: 0.0
                val iconHeight = icon.height ?: 0.0
                status.textContent = "Loaded $trimmed (${iconWidth.toInt()} × ${iconHeight.toInt()})"
                output.replaceChildren(
                    createIconifyIcon(trimmed) {
                        width = "3rem"
                        height = "3rem"
                    },
                )
            },
            { error: Throwable ->
                status.textContent = "Could not load $trimmed: ${error.message ?: "unknown error"}"
                output.replaceChildren()
            },
        ).then { button.disabled = false }
    }

    private fun section(title: String, description: String, content: HTMLElement): HTMLElement {
        val element = element(HtmlTagName.section, "section")
        val head = element(HtmlTagName.div, "section-head").apply {
            append(
                textElement(HtmlTagName.h2, title),
                textElement(HtmlTagName.p, description, "muted"),
            )
        }
        element.append(head, content)
        return element
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

    private fun modeCard(name: String, mode: String, caption: String): HTMLElement {
        val card = element(HtmlTagName.div, "card")
        val icon = createIconifyIcon(name) {
            width = "2rem"
            height = "2rem"
            this.mode = mode
        }
        if (mode == "mask") {
            icon.style.color = "#8b5cf6"
        }
        card.append(icon, textElement(HtmlTagName.span, caption, "card-name"))
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
