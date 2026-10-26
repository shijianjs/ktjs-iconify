package iconify.tools

import iconify.utils.StringOrDouble
import js.collections.JsMap
import js.collections.JsSet
import js.objects.Record
import js.reflect.unsafeCast

/**
 * Type of a parsed XML node: `"tag"` or `"text"`.
 */
sealed external interface ParsedXMLNodeType

/**
 * 包装来自 JavaScript 的值。优先用 [ParsedXMLNodeTypes] 里的常量。
 */
fun ParsedXMLNodeType(value: String): ParsedXMLNodeType = unsafeCast(value)

/**
 * [ParsedXMLNodeType] 的全部字面量。
 */
object ParsedXMLNodeTypes {
    inline val tag: ParsedXMLNodeType get() = ParsedXMLNodeType("tag")
    inline val text: ParsedXMLNodeType get() = ParsedXMLNodeType("text")
}

/**
 * Parsed XML node: a tag or a text node.
 */
external interface ParsedXMLNode {
    var type: ParsedXMLNodeType
}

/**
 * Parsed XML tag.
 */
external interface ParsedXMLTagElement : ParsedXMLNode {
    /** Tag name, such as `svg` or `path`. */
    var tag: String

    /**
     * Attributes. Numeric values stay numbers because the parser converts numeric attribute
     * values.
     */
    var attribs: Record<String, StringOrDouble>

    /** Child nodes. */
    var children: Array<ParsedXMLNode>
}

/**
 * Parsed XML text node.
 */
external interface ParsedXMLTextElement : ParsedXMLNode {
    /** Text content. */
    var content: String
}

/**
 * Link from an element to an element with an ID.
 */
external interface LinkToElementWithID {
    /** Target ID. */
    var id: String

    /** `true` if the target is used as a mask or clip path. */
    var usedAsMask: Boolean

    /** Index of the element that references the ID. */
    var usedByIndex: Double
}

/**
 * How a parent element uses the element.
 */
external interface ExtendedTagElementUses {
    /** `true` if the element is used as a mask or clip path. */
    var _usedAsMask: Boolean

    /** `true` if the element is rendered as paint. */
    var _usedAsPaint: Boolean
}

/**
 * Parent and child element indexes. This is not the SVG tree, but the tree of elements that
 * inherit attributes from their parent.
 */
external interface ExtendedTagElementRelations {
    /** Index of the parent element. */
    var _parentElement: Double?

    /** Indexes of child elements. */
    var _childElements: Array<Double>?
}

/**
 * Definition element that can be reused: mask, clip path, symbol and similar.
 */
external interface ReusableElement {
    /** Element ID. */
    var id: String

    /** `true` if the element is a mask. */
    var isMask: Boolean

    /** Element index. */
    var index: Double
}

/**
 * Any element with an ID, and the indexes of its child elements.
 */
external interface ElementWithID {
    /** Element ID. */
    var id: String

    /** `true` if the element is a mask. */
    var isMask: Boolean

    /** Indexes of elements with this ID. */
    var indexes: JsSet<Double>
}

/**
 * Parsed element with the extra properties added by [analyseSVGStructure].
 */
external interface ExtendedTagElement :
    ParsedXMLTagElement,
    ExtendedTagElementUses,
    ExtendedTagElementRelations {
    /** Element index. */
    var _index: Double

    /** Element ID, if any. */
    var _id: String?

    /** Elements that use this element's ID. */
    var _belongsTo: Array<ElementWithID>?

    /** Definition data when this element is reused. */
    var _reusableElement: ReusableElement?

    /** IDs referenced by this element. */
    var _linksTo: Array<LinkToElementWithID>?
}

/**
 * Root `<svg>` element with the extra `_parsed` flag.
 */
external interface ExtendedRootTagElement : ExtendedTagElement {
    /** `true` after the element has been parsed. */
    var _parsed: Boolean?
}

/**
 * Element in the tree of elements that inherit attributes from their parent.
 */
external interface ElementsTreeItem {
    /** Element index. */
    var index: Double

    /** `true` if the element is used as a mask or clip path. */
    var usedAsMask: Boolean

    /** Parent item. */
    var parent: ElementsTreeItem?

    /** Child items. */
    var children: Array<ElementsTreeItem>
}

/**
 * Result of [cleanupRootStyle].
 */
external interface CleanupRootStyleResult {
    /** Removed animations. Omitted when empty. */
    var animations: JsSet<String>?

    /** Removed at-rules. Omitted when empty. */
    var removedAtRules: JsSet<String>?
}

/**
 * Elements keyed by their index.
 */
typealias ElementsMap = JsMap<Double, ExtendedTagElement>
