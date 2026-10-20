@file:JsModule("@iconify/utils")

package iconify.utils

external fun camelize(str: String): String

external fun pascalize(str: String): String

external fun camelToKebab(key: String): String

external fun snakelize(str: String): String

external fun compareObjects(obj1: dynamic, obj2: dynamic, ref: dynamic = definedExternally): Boolean

/**
 * Remove properties that are identical in both objects.
 */
external fun unmergeObjects(obj1: dynamic, obj2: dynamic): dynamic

external fun commonObjectProps(item: dynamic, reference: dynamic): dynamic

/**
 * Remove characters that would break an XML attribute.
 */
external fun sanitiseTitleAttribute(content: String): String
