package iconify.utils

external interface PreparedEmojiIcon {
    var icon: String
    var sequence: String
}

external interface PreparedEmojiResult {
    var icons: Array<PreparedEmojiIcon>
    var regex: String
}

/** Item that has an emoji sequence and optionally a keyword. */
external interface EmojiSequenceItem {
    var sequence: Array<Int>
    var sequenceKey: String?
}

external interface EmojiRegexMatch {
    var match: String
    var sequence: Array<Int>
    var keyword: String
    var regexp: Int
}

external interface EmojiTestDataItem {
    var group: String
    var subgroup: String
    var version: String
    var sequence: Array<Int>
    var emoji: String
    var status: String
    var name: String
}

external interface UnicodeFormattingOptions {
    var prefix: String
    var separator: String

    /** `"upper"` or `"lower"`. */
    var `case`: String

    /** `"utf-32"` or `"utf-16"`. */
    var format: String
    var add0: Boolean
    var throwOnError: Boolean
}
