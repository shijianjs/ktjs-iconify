@file:JsModule("@iconify/utils/lib/emoji/test/variations")

package iconify.utils

/**
 * Convert an emoji sequence to its qualified form, adding variation selectors where required.
 *
 * Bound to the `@iconify/utils/lib/emoji/test/variations` submodule, which is the only place this
 * function is exported from.
 */
external fun <T : EmojiSequenceItem> getQualifiedEmojiVariation(item: T): T
