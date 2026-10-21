package iconify.tools

import kotlin.js.unsafeCast
import kotlin.test.assertEquals

/**
 * Create a JavaScript object from a JavaScript expression.
 *
 * `js()` only accepts constant strings, so object literals are evaluated through `eval`.
 * The parentheses work around the parser treating a leading `{` as a block.
 */
internal fun jsObject(source: String): dynamic =
    js("(source) => eval('(' + source + ')')")(source)

/**
 * Create a JavaScript object from properties.
 *
 * Unlike [jsObject], values are assigned directly, so paths with backslashes are not
 * interpreted as string escapes.
 */
internal fun jsObjectWith(vararg pairs: Pair<String, Any?>): dynamic {
    val obj = jsObject("{}")
    val set = js("(object, key, value) => { object[key] = value; }")
    pairs.forEach { (key, value) -> set(obj, key, value) }
    return obj
}

internal fun jsKeys(value: dynamic): Array<String> =
    js("Object.keys").unsafeCast<(dynamic) -> Array<String>>()(value)

internal fun jsStringify(value: dynamic): String =
    js("JSON.stringify").unsafeCast<(dynamic) -> String>()(value)

internal fun jsArray(value: dynamic): Array<dynamic> =
    js("(value) => Array.from(value)").unsafeCast<(dynamic) -> Array<dynamic>>()(value)

internal fun jsError(value: dynamic): String = js("(value) => value.message")(value)

/**
 * Collect `console.warn` messages produced while running [block].
 */
internal fun captureWarnings(block: () -> Unit): Array<String> =
    js(
        """(block) => {
            const original = console.warn;
            const messages = [];
            console.warn = (...args) => { messages.push(args.map(String).join(' ')); };
            try { block(); } finally { console.warn = original; }
            return messages;
        }"""
    )(block).unsafeCast<Array<String>>()

/**
 * Assert that [block] throws, returning the error message.
 */
internal fun errorMessage(block: () -> Unit): String? =
    try {
        block()
        null
    } catch (err: dynamic) {
        jsError(err)
    }

/**
 * Deep equality for JSON-like values, ignoring property order.
 */
internal fun jsDeepEqual(expected: dynamic, actual: dynamic): Boolean =
    js(
        """(a, b) => {
            const eq = (x, y) => {
                if (x === y) return true;
                if (x === null || y === null || typeof x !== 'object' || typeof y !== 'object') return false;
                if (Array.isArray(x) !== Array.isArray(y)) return false;
                const kx = Object.keys(x);
                const ky = Object.keys(y);
                if (kx.length !== ky.length) return false;
                return kx.every((key) => Object.prototype.hasOwnProperty.call(y, key) && eq(x[key], y[key]));
            };
            return eq(a, b);
        }"""
    )(expected, actual)

internal fun assertJsDeepEquals(expected: dynamic, actual: dynamic) {
    if (!jsDeepEqual(expected, actual)) {
        assertEquals(jsStringify(expected), jsStringify(actual))
    }
}

/**
 * Assert that [actual] deep-equals an object literal evaluated with [jsObject].
 */
internal fun assertJsObject(expectedSource: String, actual: dynamic) {
    assertJsDeepEquals(jsObject(expectedSource), actual)
}

/**
 * SVG with a global `<style>` block, shared by style-related tests.
 */
internal const val ARROW_SVG =
    """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><defs><style>.cls-1{fill:#fff;opacity:0;}.cls-2{fill:#231f20;}</style></defs><title>arrow-circle-right</title><g id="Layer_2" data-name="Layer 2"><g id="arrow-circle-right"><g id="arrow-circle-right-2" data-name="arrow-circle-right"><rect class="cls-1" width="24" height="24" transform="translate(0 24) rotate(-90)"/><path class="cls-2" d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z"/></g></g></g></svg>"""

internal const val ARROW_PATH =
    """<path d="M2,12A10,10,0,1,0,12,2,10,10,0,0,0,2,12ZM13.86,8.31l2.86,3a.49.49,0,0,1,.1.15.54.54,0,0,1,.1.16.94.94,0,0,1,0,.76,1,1,0,0,1-.21.33l-3,3a1,1,0,0,1-1.42-1.42L13.59,13H8a1,1,0,0,1,0-2h5.66L12.41,9.69a1,1,0,0,1,1.45-1.38Z"/>"""
