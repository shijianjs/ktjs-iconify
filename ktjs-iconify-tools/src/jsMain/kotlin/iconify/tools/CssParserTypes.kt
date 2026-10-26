package iconify.tools

import js.objects.Record
import js.reflect.unsafeCast

/**
 * CSS token type: `"chunk"`, `"url"`, `"quoted-string"`, `"rule"`, `"selector"`, `"at-rule"`
 * or `"close"`.
 */
sealed external interface CSSTokenType

/**
 * 包装来自 JavaScript 的值。优先用 [CSSTokenTypes] 里的常量。
 */
fun CSSTokenType(value: String): CSSTokenType = unsafeCast(value)

/**
 * [CSSTokenType] 的全部字面量。
 */
object CSSTokenTypes {
    inline val chunk: CSSTokenType get() = CSSTokenType("chunk")
    inline val url: CSSTokenType get() = CSSTokenType("url")
    inline val quotedString: CSSTokenType get() = CSSTokenType("quoted-string")
    inline val rule: CSSTokenType get() = CSSTokenType("rule")
    inline val selector: CSSTokenType get() = CSSTokenType("selector")
    inline val atRule: CSSTokenType get() = CSSTokenType("at-rule")
    inline val close: CSSTokenType get() = CSSTokenType("close")
}

/**
 * CSS token.
 */
external interface CSSToken {
    var type: CSSTokenType

    /** Position in the source. */
    var index: Double
}

/**
 * Raw text token before it is split into CSS tokens.
 */
external interface TextToken {
    var type: CSSTokenType

    /** Position in the source. */
    var index: Double

    /** Token text. */
    var text: String
}

/**
 * Declaration inside a rule.
 */
external interface CSSRuleToken : CSSToken {
    /** Property name. */
    var prop: String

    /** Property value. */
    var value: String

    /** `true` when the declaration is marked as important. */
    var important: Boolean?
}

/**
 * Selector that starts a rule block.
 */
external interface CSSSelectorToken : CSSToken {
    /** Selector as written. */
    var code: String

    /** Individual selectors. */
    var selectors: Array<String>
}

/**
 * At-rule.
 */
external interface CSSAtRuleToken : CSSToken {
    /** Rule name, such as `media` or `keyframes`. */
    var rule: String

    /** Rule parameters. */
    var value: String
}

/**
 * Closing brace of a rule block.
 */
external interface CSSCloseToken : CSSToken

/**
 * Callback item type for [parseSVGStyle]: `"inline"`, `"global"`, `"at-rule"` or
 * `"keyframes"`.
 */
sealed external interface ParseSVGStyleCallbackItemType

/**
 * 包装来自 JavaScript 的值。优先用 [ParseSVGStyleCallbackItemTypes] 里的常量。
 */
fun ParseSVGStyleCallbackItemType(value: String): ParseSVGStyleCallbackItemType =
    unsafeCast(value)

/**
 * [ParseSVGStyleCallbackItemType] 的全部字面量。
 */
object ParseSVGStyleCallbackItemTypes {
    inline val inline: ParseSVGStyleCallbackItemType get() = ParseSVGStyleCallbackItemType("inline")
    inline val global: ParseSVGStyleCallbackItemType get() = ParseSVGStyleCallbackItemType("global")
    inline val atRule: ParseSVGStyleCallbackItemType get() = ParseSVGStyleCallbackItemType("at-rule")
    inline val keyframes: ParseSVGStyleCallbackItemType
        get() = ParseSVGStyleCallbackItemType("keyframes")
}

/**
 * Item passed to the [parseSVGStyle] callback.
 *
 * The [type] property identifies the variant and selects which fields are available.
 */
external interface ParseSVGStyleCallbackItem {
    var type: ParseSVGStyleCallbackItemType

    /** Property name. */
    var prop: String

    /** Property value. */
    var value: String
}

/**
 * Rule from an inline `style` attribute.
 */
external interface ParseSVGStyleCallbackItemInline : ParseSVGStyleCallbackItem {
    /** Element that owns the style attribute. */
    var item: ParseSVGCallbackItem
}

/**
 * Rule in a `<style>` element.
 */
external interface ParseSVGStyleCallbackItemGlobal : ParseSVGStyleCallbackItem {
    /** Parsed declaration. */
    var token: CSSRuleToken

    /** Selectors of the rule. */
    var selectors: Array<String>

    /** Selector tokens. */
    var selectorTokens: Array<CSSToken>

    /** Tokens before the rule. */
    var prevTokens: Array<CSSToken?>

    /** Tokens after the rule. */
    var nextTokens: Array<CSSToken>
}

/**
 * Rule inside an at-rule block, such as `@media`.
 */
external interface ParseSVGStyleCallbackItemGlobalAtRule : ParseSVGStyleCallbackItem {
    /** Parsed at-rule. */
    var token: CSSAtRuleToken

    /** Tokens inside the block. */
    var childTokens: Array<CSSToken>

    /** Tokens before the at-rule. */
    var prevTokens: Array<CSSToken?>

    /** Tokens after the at-rule. */
    var nextTokens: Array<CSSToken>
}

/**
 * Generic at-rule block.
 */
external interface ParseSVGStyleCallbackItemGlobalGenericAtRule :
    ParseSVGStyleCallbackItemGlobalAtRule

/**
 * Keyframes at-rule block.
 */
external interface ParseSVGStyleCallbackItemGlobalKeyframesAtRule :
    ParseSVGStyleCallbackItemGlobalAtRule {
    /** Keyframe selectors mapped to their declarations. */
    var from: Record<String, String>
}
