package iconify.utils

import kotlin.js.js
import kotlin.js.unsafeCast

/** Evaluate a JavaScript object literal used as test data. */
internal fun jsObject(source: String): dynamic =
    js("(source) => eval('(' + source + ')')")(source)

/** Read a property with an arbitrary name from a dynamic object. */
internal fun prop(value: dynamic, name: String): dynamic = js("value[name]")

/** Check whether a dynamic object has its own or inherited property [name]. */
internal fun hasProperty(value: dynamic, name: String): Boolean = js("name in value")

/** Create a JavaScript regular expression. */
internal fun jsRegexp(source: String, flags: String = ""): dynamic =
    js("(source, flags) => new RegExp(source, flags)")(source, flags)

/** Run [regexp] against [text] with the global flag, returning all matches. */
internal fun globalMatches(text: String, regexp: dynamic): Array<String>? =
    js("(text, regexp) => text.match(regexp)")(text, regexp)

/** Create a string from a Unicode code point. */
internal fun fromCodePoint(code: Int): String =
    js("String.fromCodePoint")(code).unsafeCast<String>()
