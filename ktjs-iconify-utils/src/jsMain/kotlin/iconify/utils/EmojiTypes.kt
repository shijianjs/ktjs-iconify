package iconify.utils

import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.JsAny

/**
 * 上游 `number | string`。
 */
external interface DoubleOrString

fun DoubleOrString(value: Double): DoubleOrString = unsafeCast(value)

fun DoubleOrString(value: String): DoubleOrString = unsafeCast(value)

/**
 * 上游 `1 | 2 | false`：UTF-16 代理对的第一个或第二个码元，不匹配时为 `false`。
 */
external interface DoubleOrBoolean

fun DoubleOrBoolean(value: Double): DoubleOrBoolean = unsafeCast(value)

fun DoubleOrBoolean(value: Boolean): DoubleOrBoolean = unsafeCast(value)

/**
 * 上游 `string | number[]`。
 */
external interface StringOrDoubleArray

fun StringOrDoubleArray(value: String): StringOrDoubleArray = unsafeCast(value)

fun StringOrDoubleArray(value: Array<Double>): StringOrDoubleArray = unsafeCast(value)

/**
 * 上游 `string | RegExp`。
 */
external interface StringOrRegExp

fun StringOrRegExp(value: String): StringOrRegExp = unsafeCast(value)

fun StringOrRegExp(value: kotlin.js.RegExp): StringOrRegExp = unsafeCast(value)

/**
 * 上游 `string | RegExp | (string | RegExp)[]`：匹配 emoji 用的正则。
 */
external interface EmojiRegexp

fun EmojiRegexp(value: String): EmojiRegexp = unsafeCast(value)

fun EmojiRegexp(value: kotlin.js.RegExp): EmojiRegexp = unsafeCast(value)

fun EmojiRegexp(value: Array<StringOrRegExp>): EmojiRegexp = unsafeCast(value)

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
 * 上游 `EmojiTestData`：不合格序列（不含 `FE0F`）到条目的映射。
 */
typealias EmojiTestData = Record<String, EmojiTestDataItem>

/**
 * Emoji components in a sequence: `"skin-tone"` or `"hair-style"`.
 */
sealed external interface EmojiComponentType

/**
 * 包装来自 JavaScript 的值。优先用 [EmojiComponentTypes] 里的常量。
 */
fun EmojiComponentType(value: String): EmojiComponentType = unsafeCast(value)

/**
 * [EmojiComponentType] 的全部字面量。
 */
object EmojiComponentTypes {
    inline val skinTone: EmojiComponentType get() = EmojiComponentType("skin-tone")
    inline val hairStyle: EmojiComponentType get() = EmojiComponentType("hair-style")
}

/**
 * Component reference inside an emoji name or sequence.
 */
external interface EmojiSequenceComponentEntry {
    var index: Double
    var type: EmojiComponentType
}

/**
 * 上游 `SplitEmojiName`：emoji 名拆分出的基础名与变体信息。
 */
external interface SplitEmojiName {
    var base: String
    var key: String
    var variations: Array<StringOrEmojiSequenceComponentEntry>?
    var components: Double?
}

/**
 * 上游 `string | EmojiSequenceComponentEntry`。
 */
external interface StringOrEmojiSequenceComponentEntry

fun StringOrEmojiSequenceComponentEntry(value: String): StringOrEmojiSequenceComponentEntry =
    unsafeCast(value)

fun StringOrEmojiSequenceComponentEntry(
    value: EmojiSequenceComponentEntry,
): StringOrEmojiSequenceComponentEntry = unsafeCast(value)

/**
 * 上游 `TreeSplitEmojiTestDataItem`：测试数据条目加上组件统计。
 */
external interface EmojiTestItemWithComponents {
    var group: String
    var subgroup: String
    var version: String
    var name: SplitEmojiName
    var sequenceKey: String
    var sequence: Array<StringOrDouble>
    var components: Record<String, Double>
    var componentsKey: String
}

/**
 * 上游 `EmojiComponentsTreeItem`：测试数据的组件依赖树节点。
 */
external interface EmojiComponentsTreeItem {
    var item: EmojiTestItemWithComponents
    var children: Record<String, EmojiComponentsTreeItem>?
}

/**
 * 上游 `EmojiComponentsTree`。
 */
typealias EmojiComponentsTree = Record<String, EmojiComponentsTreeItem>

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

/**
 * 上游 `Partial<UnicodeFormattingOptions>`：所有字段都可省略。
 */
external interface PartialUnicodeFormattingOptions {
    var prefix: String?
    var separator: String?
    var `case`: String?
    var format: String?
    var add0: Boolean?
    var throwOnError: Boolean?
}
