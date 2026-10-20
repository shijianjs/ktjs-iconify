@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyJSON

external fun getEmojiSequenceFromString(value: String): Array<Int>

external fun getSequenceFromEmojiStringOrKeyword(value: String): Array<Int>

external fun splitEmojiSequences(
    sequence: Array<Int>,
    separator: Int = definedExternally,
): Array<Array<Int>>

external fun joinEmojiSequences(
    sequences: Array<Array<Int>>,
    separator: Int = definedExternally,
): Array<Int>

external fun getUnqualifiedEmojiSequence(sequence: Array<Int>): Array<Int>

external fun getEmojiCodePoint(code: String): Int

/** `[first, second]` when the code is a UTF-32 number split into a UTF-16 pair. */
external fun splitUTF32Number(code: Int): Array<Int>?

/** `1`, `2` or `false`. */
external fun isUTF32SplitNumber(value: Int): dynamic

external fun mergeUTF32Numbers(part1: Int, part2: Int): Int?

external fun getEmojiUnicode(code: dynamic): String

external fun convertEmojiSequenceToUTF16(numbers: Array<Int>): Array<Int>

external fun convertEmojiSequenceToUTF32(
    numbers: Array<Int>,
    throwOnError: Boolean = definedExternally,
): Array<Int>

external fun getEmojiUnicodeString(
    code: Int,
    options: dynamic = definedExternally,
): String

external fun getEmojiSequenceString(
    sequence: Array<Int>,
    options: dynamic = definedExternally,
): String

external fun getEmojiSequenceKeyword(sequence: Array<Int>): String

external fun prepareEmojiForIconsList(
    icons: dynamic,
    rawTestData: String = definedExternally,
): PreparedEmojiResult

external fun prepareEmojiForIconSet(
    iconSet: IconifyJSON,
    rawTestData: String = definedExternally,
): PreparedEmojiResult

external fun createOptimisedRegexForEmojiSequences(sequences: Array<Array<Int>>): String

external fun createOptimisedRegex(emojis: Array<dynamic>): String

/**
 * Replace emojis in text. Return `undefined` from the callback to keep the match.
 */
external fun findAndReplaceEmojisInText(
    regexp: dynamic,
    content: String,
    callback: (EmojiRegexMatch, String) -> dynamic,
): String?

external fun parseEmojiTestFile(data: String): dynamic

external fun <T : EmojiSequenceItem> getQualifiedEmojiVariation(item: T): T

external fun <T : EmojiSequenceItem> getQualifiedEmojiVariations(items: Array<T>): Array<T>

external fun <T : EmojiSequenceItem> findMissingEmojis(
    sequences: Array<T>,
    testDataTree: dynamic,
): Array<T>
