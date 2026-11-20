package io.github.shijianjs.ktjs.iconify.demo.page.loader

import io.github.shijianjs.ktjs.dom.element.invoke

internal class LoaderTexts(
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
