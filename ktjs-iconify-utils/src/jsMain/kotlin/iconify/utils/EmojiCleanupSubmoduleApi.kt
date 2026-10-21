@file:JsModule("@iconify/utils/lib/emoji/cleanup")

package iconify.utils

/**
 * Convert an emoji sequence or keyword to code points.
 *
 * Values made only of hex digits, dashes and whitespace are parsed as a sequence; anything else,
 * such as a keyword, is converted character by character.
 *
 * Bound to the `@iconify/utils/lib/emoji/cleanup` submodule, which is the only place this function
 * is exported from.
 */
external fun getSequenceFromEmojiStringOrKeyword(value: String): Array<Int>

/**
 * Split an emoji sequence by the joiner into smaller sequences.
 *
 * Each part represents one component of a combined emoji.
 *
 * @param separator code point to split on; defaults to the zero width joiner, U+200D.
 */
external fun splitEmojiSequences(
    sequence: Array<Int>,
    separator: Int = definedExternally,
): Array<Array<Int>>

/**
 * Join emoji sequences, inserting the separator between them.
 *
 * @param separator code point to insert; defaults to the zero width joiner, U+200D.
 */
external fun joinEmojiSequences(
    sequences: Array<Array<Int>>,
    separator: Int = definedExternally,
): Array<Int>
