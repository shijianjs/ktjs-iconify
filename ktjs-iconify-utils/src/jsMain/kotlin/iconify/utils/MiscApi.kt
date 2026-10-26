@file:JsModule("@iconify/utils")

package iconify.utils

import js.objects.Record
import kotlin.js.JsAny

/**
 * Convert a kebab-case string to camelCase: `foo-bar` becomes `fooBar`.
 */
external fun camelize(str: String): String

/**
 * Convert a kebab-case string to PascalCase: `foo-bar` becomes `FooBar`.
 */
external fun pascalize(str: String): String

/**
 * Convert a camelCase string to kebab-case: `fooBar` becomes `foo-bar`.
 *
 * Colons are also converted to dashes.
 */
external fun camelToKebab(key: String): String

/**
 * Convert a camelCase string to snake_case: `fooBar` becomes `foo_bar`.
 */
external fun snakelize(str: String): String

/**
 * Compare two objects.
 *
 * @param ref keys to compare; defaults to the keys of [obj1].
 * @return `true` if all keys in [ref] have strictly equal values and both objects have the same
 * number of keys.
 */
external fun compareObjects(
    obj1: Record<String, JsAny?>,
    obj2: Record<String, JsAny?>,
    ref: Record<String, JsAny?> = definedExternally,
): Boolean

/**
 * Remove properties that are identical in both objects.
 *
 * @return a copy of [obj1] without the properties whose values equal those in [obj2].
 */
external fun unmergeObjects(
    obj1: Record<String, JsAny?>,
    obj2: Record<String, JsAny?>,
): Record<String, JsAny?>

/**
 * Get properties of [item] whose keys exist in [reference].
 */
external fun commonObjectProps(
    item: JsAny,
    reference: Record<String, JsAny?>,
): Record<String, JsAny?>

/**
 * Remove characters that would break an XML attribute: `<`, `>` and `&`.
 *
 * This is a basic filter, not a full XML parser.
 */
external fun sanitiseTitleAttribute(content: String): String
