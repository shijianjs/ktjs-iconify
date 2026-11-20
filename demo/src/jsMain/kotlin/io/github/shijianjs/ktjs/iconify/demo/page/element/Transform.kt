package io.github.shijianjs.ktjs.iconify.demo.page.element

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.iconify.IconifyIconElement

/** One transformation example: the caption is data (an attribute value), not copy. */
internal class Transform(val caption: String, val configure: IconifyIconElement.() -> Unit)
