package io.github.shijianjs.ktjs.iconify.demo.page.loader

import io.github.shijianjs.ktjs.dom.element.invoke

/** What the form is showing right now; the states that name an icon carry that name. */
internal sealed interface LoaderState {
    data object Idle : LoaderState

    data object MissingName : LoaderState

    data class Loading(val name: String) : LoaderState

    data class Loaded(val name: String, val width: Int, val height: Int) : LoaderState

    data class Failed(val name: String, val error: String?) : LoaderState
}
