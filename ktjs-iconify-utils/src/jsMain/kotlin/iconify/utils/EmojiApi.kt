@file:JsModule("@iconify/utils")

package iconify.utils

import iconify.IconifyJSON

/**
 * Parse an emoji sequence string into code points.
 *
 * Accepts space or dash separated hexadecimal numbers, in UTF-16 or UTF-32 form. For example,
 * `"1F441 FE0F 200D 1F5E8 FE0F"` and `"1f441-fe0f-200d-1f5e8-fe0f"` produce the same result.
 */
external fun getEmojiSequenceFromString(value: String): Array<Int>

/**
 * Remove emoji variation selectors, U+FE0F, from a sequence.
 */
external fun getUnqualifiedEmojiSequence(sequence: Array<Int>): Array<Int>

/**
 * Convert a hexadecimal code string to a code point.
 */
external fun getEmojiCodePoint(code: String): Int

/**
 * Split a code point above U+FFFF into a UTF-16 surrogate pair.
 *
 * @return `[first, second]`, or `null` if the code point fits in UTF-16.
 */
external fun splitUTF32Number(code: Int): Array<Int>?

/**
 * Check whether a UTF-16 code unit is part of a surrogate pair.
 *
 * @return `1` for the first unit, `2` for the second, `false` otherwise.
 */
external fun isUTF32SplitNumber(value: Int): dynamic

/**
 * Merge a UTF-16 surrogate pair into one code point.
 *
 * @return the code point, or `null` if the values are not a valid pair.
 */
external fun mergeUTF32Numbers(part1: Int, part2: Int): Int?

/**
 * Convert a code point, or a hexadecimal code string, to a Unicode string.
 */
external fun getEmojiUnicode(code: dynamic): String

/**
 * Convert an emoji sequence to UTF-16 code units.
 */
external fun convertEmojiSequenceToUTF16(numbers: Array<Int>): Array<Int>

/**
 * Convert an emoji sequence to UTF-32 code points.
 *
 * @param throwOnError throw when the sequence contains an invalid UTF-16 surrogate pair; defaults
 * to `true`.
 */
external fun convertEmojiSequenceToUTF32(
    numbers: Array<Int>,
    throwOnError: Boolean = definedExternally,
): Array<Int>

/**
 * Convert a code point to a hexadecimal string.
 *
 * Defaults to lowercase hexadecimal without a prefix, such as `1f600`.
 */
external fun getEmojiUnicodeString(
    code: Int,
    options: dynamic = definedExternally,
): String

/**
 * Convert an emoji sequence to a hexadecimal string.
 *
 * Defaults to lowercase UTF-32 hexadecimal separated by dashes, such as `1f441-fe0f`.
 */
external fun getEmojiSequenceString(
    sequence: Array<Int>,
    options: dynamic = definedExternally,
): String

/**
 * Convert an emoji sequence to a keyword-style string, without options.
 *
 * Produces lowercase hexadecimal separated by dashes, such as `1f441-fe0f`.
 */
external fun getEmojiSequenceKeyword(sequence: Array<Int>): String

/**
 * Prepare an emoji to icon name map for matching emojis in text.
 *
 * @param icons emoji sequences, keyed by emoji, with the icon name as value.
 * @param rawTestData contents of `emoji-test.txt` from unicode.org, used to find missing emojis and
 * optimise the regular expression.
 */
external fun prepareEmojiForIconsList(
    icons: dynamic,
    rawTestData: String = definedExternally,
): PreparedEmojiResult

/**
 * Prepare the `chars` of an icon set for matching emojis in text.
 *
 * @param rawTestData contents of `emoji-test.txt` from unicode.org, used to find missing emojis and
 * optimise the regular expression.
 */
external fun prepareEmojiForIconSet(
    iconSet: IconifyJSON,
    rawTestData: String = definedExternally,
): PreparedEmojiResult

/**
 * Create an optimised regular expression source for emoji sequences.
 *
 * The sequences are converted to UTF-32 first.
 */
external fun createOptimisedRegexForEmojiSequences(sequences: Array<Array<Int>>): String

/**
 * Create an optimised regular expression source for emojis.
 *
 * Each emoji can be a sequence string or an array of code points. Qualified variations are added.
 */
external fun createOptimisedRegex(emojis: Array<dynamic>): String

/**
 * Replace emojis in text.
 *
 * @param callback receives the match and the text before it. Return the replacement text, or
 * `undefined` to keep the match.
 * @return the text with replacements, or `null` if nothing was replaced.
 */
external fun findAndReplaceEmojisInText(
    regexp: dynamic,
    content: String,
    callback: (EmojiRegexMatch, String) -> dynamic,
): String?

/**
 * Parse the contents of `emoji-test.txt` from unicode.org.
 *
 * Duplicate variants of the same emoji are merged into one item, keeping the fully-qualified
 * sequence.
 */
external fun parseEmojiTestFile(data: String): dynamic

/**
 * Convert emoji sequences to their qualified forms, removing duplicates.
 */
external fun <T : EmojiSequenceItem> getQualifiedEmojiVariations(items: Array<T>): Array<T>

/**
 * Find emojis missing from [sequences] by looking them up in emoji test data.
 *
 * Missing items are based on the items they should be copied from; the source items are expected to
 * reference an icon name.
 */
external fun <T : EmojiSequenceItem> findMissingEmojis(
    sequences: Array<T>,
    testDataTree: dynamic,
): Array<T>
