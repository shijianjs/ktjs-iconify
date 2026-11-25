package io.github.shijianjs.ktjs.iconify

import io.github.shijianjs.ktjs.dom.attribute.Attribute
import io.github.shijianjs.ktjs.dom.attribute.booleanAttribute

/**
 * `noobserver`: keep the icon rendered even when it is outside the viewport.
 *
 * `iconify-icon` is an attribute-driven element, and this flag is one of the two it does **not**
 * expose as a property: upstream installs generic attribute reflection for it, which writes
 * `noobserver="false"` for `false` — an attribute that is still *present*, so the flag stays on.
 * The attribute layer gets the boolean right: `value = true` writes the bare attribute, `false`
 * removes it, and `valueOrNull` tells "absent" from "empty".
 *
 * ```kotlin
 * icon.noObserverAttr.value = true       // noobserver
 * icon.noObserverAttr.value = false      // attribute removed
 * ```
 */
val IconifyIconElement.noObserverAttr: Attribute<Boolean>
    get() = booleanAttribute("noobserver")
