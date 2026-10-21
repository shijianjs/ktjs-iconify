package iconify.utils

/**
 * Emoji mapped to an icon.
 */
external interface PreparedEmojiIcon {
    /**
     * Icon name.
     */
    var icon: String

    /**
     * Unqualified emoji sequence as a keyword, such as `1f441-200d-1f5e8`.
     */
    var sequence: String
}

/**
 * Result of preparing emojis for matching in text.
 */
external interface PreparedEmojiResult {
    /**
     * Emojis mapped to icons.
     */
    var icons: Array<PreparedEmojiIcon>

    /**
     * Optimised regular expression source for all emoji sequences.
     */
    var regex: String
}

/**
 * Item that has an emoji sequence and optionally a keyword.
 */
external interface EmojiSequenceItem {
    /**
     * Emoji sequence as code points.
     */
    var sequence: Array<Int>

    /**
     * Unqualified sequence as a keyword, such as `1f441-200d-1f5e8`.
     */
    var sequenceKey: String?
}

/**
 * Emoji match in text.
 */
external interface EmojiRegexMatch {
    /**
     * Matched text.
     */
    var match: String

    /**
     * Emoji sequence as code points.
     */
    var sequence: Array<Int>

    /**
     * Unqualified sequence as a keyword.
     */
    var keyword: String

    /**
     * Index of the regular expression that matched, when several were provided.
     */
    var regexp: Int
}

/**
 * Entry from `emoji-test.txt` from unicode.org.
 */
external interface EmojiTestDataItem {
    /**
     * Group, such as `Smileys & Emotion`.
     */
    var group: String

    /**
     * Subgroup, such as `face-smiling`.
     */
    var subgroup: String

    /**
     * Emoji version, such as `E1.0`.
     */
    var version: String

    /**
     * Emoji sequence as code points.
     */
    var sequence: Array<Int>

    /**
     * Emoji character.
     */
    var emoji: String

    /**
     * Qualification status: `"fully-qualified"`, `"minimally-qualified"`, `"unqualified"` or
     * `"component"`.
     */
    var status: String

    /**
     * Emoji name.
     */
    var name: String
}

/**
 * Options for formatting emoji code points as a string.
 */
external interface UnicodeFormattingOptions {
    /**
     * Prefix before each code, such as `\\u`.
     */
    var prefix: String

    /**
     * Separator between codes.
     */
    var separator: String

    /**
     * Case conversion: `"upper"` or `"lower"`.
     */
    var `case`: String

    /**
     * Output format: `"utf-32"` or `"utf-16"`.
     */
    var format: String

    /**
     * Pad codes shorter than four digits with leading zeros.
     */
    var add0: Boolean

    /**
     * Throw when the sequence contains an invalid UTF-16 surrogate pair.
     */
    var throwOnError: Boolean
}
