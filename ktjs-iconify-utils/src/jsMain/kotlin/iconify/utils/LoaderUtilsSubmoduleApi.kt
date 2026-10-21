@file:JsModule("@iconify/utils/lib/loader/utils")

package iconify.utils

/**
 * Possible icon names for a name that may use a different naming convention, from the most specific
 * to the most generic.
 *
 * For example, `fooBar` also produces `foo-bar`, and `foo2` also produces `foo-2`.
 *
 * Bound to the `@iconify/utils/lib/loader/utils` submodule, which is the only place this function
 * is exported from.
 */
external fun getPossibleIconNames(icon: String): Array<String>
