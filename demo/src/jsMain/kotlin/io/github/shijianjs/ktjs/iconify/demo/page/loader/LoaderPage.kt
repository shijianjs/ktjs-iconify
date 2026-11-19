package io.github.shijianjs.ktjs.iconify.demo.page.loader

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.newPage
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.iconify.createIconifyIcon
import io.github.shijianjs.ktjs.iconify.loadIcon
import io.github.shijianjs.ktjs.vaadin.button.ButtonVariants
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.dom.document
import web.events.addEventHandler
import web.html.HTMLElement
import web.html.HtmlTagName
import web.pointer.CLICK
import web.pointer.PointerEvent

/** What the form is showing right now; the states that name an icon carry that name. */
private sealed interface LoaderState {
    data object Idle : LoaderState

    data object MissingName : LoaderState

    data class Loading(val name: String) : LoaderState

    data class Loaded(val name: String, val width: Int, val height: Int) : LoaderState

    data class Failed(val name: String, val error: String?) : LoaderState
}

private class LoaderTexts(
    val title: String,
    val summary: String,
    val sectionTitle: String,
    val sectionHint: String,
    val fieldLabel: String,
    val buttonLabel: String,
    val idle: String,
    val missingName: String,
    val unknownError: String,
    val note: String,
) {
    fun loading(name: String): String = "Loading $name …"

    fun loaded(name: String, width: Int, height: Int): String = "$name → ${width}×${height}"

    fun failed(name: String, error: String): String = "$name failed: $error"
}

private val english: LoaderTexts = LoaderTexts(
    title = "Load an icon by name",
    summary = "`loadIcon` asks the Iconify API for one name and resolves with the icon data. The element " +
        "that renders it is created from the same name.",
    sectionTitle = "Any name, on demand",
    sectionHint = "Try `mdi:home`, `lucide:rocket` or a set you have registered locally.",
    fieldLabel = "Icon name",
    buttonLabel = "Load",
    idle = "Nothing loaded yet.",
    missingName = "Type a name first.",
    unknownError = "unknown error",
    note = "The promise rejects when the name does not exist, which is the difference between " +
        "`loadIcon` (throws) and `getIcon` (answers null).",
)

private val chinese: LoaderTexts = LoaderTexts(
    title = "按名字加载图标",
    summary = "`loadIcon` 向 Iconify API 索要一个名字并解析出图标数据，渲染它的元素由同一个名字创建。",
    sectionTitle = "任意名字，按需加载",
    sectionHint = "可以试 `mdi:home`、`lucide:rocket`，或者你在本地注册过的图标集。",
    fieldLabel = "图标名",
    buttonLabel = "加载",
    idle = "还没有加载任何图标。",
    missingName = "先输入一个名字。",
    unknownError = "未知错误",
    note = "名字不存在时 promise 会失败——这正是 `loadIcon`（抛错）与 `getIcon`（给 null）的区别。",
)

private val texts: Translations<LoaderTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** One form: a field, a button, a status line and the rendered result. */
internal class LoaderPage : Page {
    private val page = newPage()
    private val binder = Binder(texts)

    override val node: HTMLElement get() = page

    private val field = createTextField {
        placeholder = "mdi:home"
        value = "ph:rocket-launch"
        required = true
    }

    private val button = createButton {
        addTheme(ButtonVariants.primary)
        textContent = english.buttonLabel
    }

    private val status = Readout()

    /** Holds the rendered `<iconify-icon>`; empty until a load succeeds. */
    private val output: HTMLElement = document.createElement(HtmlTagName.div)

    /** Last state; kept so a language switch can re-localize the line instead of losing it. */
    private var state: LoaderState = LoaderState.Idle

    init {
        val section = sectionBlock(english.sectionTitle, english.sectionHint)
        binder.bind(section.title) { it.sectionTitle }
        binder.bind(section.hint) { it.sectionHint }

        // The field's label is a property, not text content: a text node on the host would land in the
        // light DOM slot and be ignored by the component, so it is written in `applyTranslation`.
        field.label = english.fieldLabel
        binder.bind(button) { it.buttonLabel }

        button.addEventHandler(PointerEvent.CLICK) { submit() }

        showState()
        section.body.append(
            exampleBox(null, row(field, button), status.root, output),
        )

        val note = paragraph(english.note)
        binder.bind(note) { it.note }
        section.body.append(note)

        page.body.append(section.root)
    }

    private fun submit() {
        if (state is LoaderState.Loading) return
        val name = field.value.trim()
        if (name.isEmpty()) {
            state = LoaderState.MissingName
            showState()
            return
        }
        state = LoaderState.Loading(name)
        showState()
        loadIcon(name).then(
            { icon ->
                val width = (icon.width ?: 0.0).toInt()
                val height = (icon.height ?: 0.0).toInt()
                output.replaceChildren(createIconifyIcon(name) { this.width = "3rem"; this.height = "3rem" })
                state = LoaderState.Loaded(name, width, height)
                showState()
            },
            { error: Throwable ->
                output.replaceChildren()
                state = LoaderState.Failed(name, error.message)
                showState()
            },
        )
    }

    private fun showState() {
        val current = texts[lang]
        status.set(
            when (val value = state) {
                is LoaderState.Idle -> current.idle
                is LoaderState.MissingName -> current.missingName
                is LoaderState.Loading -> current.loading(value.name)
                is LoaderState.Loaded -> current.loaded(value.name, value.width, value.height)
                is LoaderState.Failed -> current.failed(value.name, value.error ?: current.unknownError)
            },
        )
        button.disabled = state is LoaderState.Loading
        field.disabled = state is LoaderState.Loading
    }

    /** The language the page is currently shown in; the layout calls [applyTranslation] first. */
    private var lang: Lang = Langs.EN

    override fun applyTranslation(lang: Lang) {
        this.lang = lang
        val current = texts[lang]
        page.setTitle(current.title)
        page.setSummary(current.summary)
        field.label = current.fieldLabel
        binder.apply(lang)
        showState()
    }
}
