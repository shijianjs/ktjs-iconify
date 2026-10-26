package iconify.tools

import iconify.utils.AwaitableUnit
import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.Date

/**
 * Options for exporting SVG from Figma.
 */
external interface FigmaImportSVGOptions {
    /**
     * Include IDs from Figma in the exported SVG.
     */
    var includeID: Boolean?

    /**
     * Simplify strokes.
     */
    var simplifyStroke: Boolean?

    /**
     * Use absolute bounds when exporting.
     */
    var useAbsoluteBounds: Boolean?
}

/**
 * Options shared by the Figma API queries.
 */
external interface FigmaImportCommonOptions {
    /**
     * Figma API token.
     */
    var token: String

    /**
     * Figma document ID.
     */
    var file: String

    /**
     * Document version.
     */
    var version: String?
}

/**
 * 上游 `string | Date | true`：只在文档更新后导入。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface FigmaIfModifiedSince

fun FigmaIfModifiedSince(value: String): FigmaIfModifiedSince = unsafeCast(value)

fun FigmaIfModifiedSince(value: Date): FigmaIfModifiedSince = unsafeCast(value)

fun FigmaIfModifiedSince(value: Boolean): FigmaIfModifiedSince = unsafeCast(value)

/**
 * Options for the Figma files query.
 */
external interface FigmaFilesQueryOptions : FigmaImportCommonOptions {
    /**
     * Check whether the document changed since this value: a timestamp string, a [Date], or
     * `true` to compare with the last cached document.
     */
    var ifModifiedSince: FigmaIfModifiedSince?

    /**
     * IDs of nodes to check.
     */
    var ids: Array<String>?

    /**
     * Node depth to retrieve.
     *
     * Set it to avoid retrieving too much data: `2` for Page → Icon, `3` for Page → Group or
     * Frame → Icon.
     */
    var depth: Double?
}

/**
 * Options for the Figma images query.
 */
external interface FigmaImagesQueryOptions : FigmaImportCommonOptions {
    /**
     * Options for exporting SVG from Figma.
     */
    var svgOptions: FigmaImportSVGOptions?
}

/**
 * Options for finding icon nodes in a Figma document.
 */
external interface FigmaGetIconNodesOptions {
    /**
     * Page names or IDs, an alternative to [filterParentNode].
     */
    var pages: Array<String>?

    /**
     * Callback to filter parent nodes, to skip irrelevant parts of the tree.
     */
    var filterParentNode: FigmaImportParentNodeFilter?

    /**
     * Callback that returns the icon name for a node, or `null`/`undefined` to skip the node.
     */
    var iconNameForNode: FigmaImportNodeFilter
}

/**
 * Parent node type: `"CANVAS"`, `"FRAME"`, `"GROUP"`, `"SECTION"` or `"COMPONENT_SET"`.
 */
sealed external interface FigmaImportParentNodeType

/**
 * 包装来自 JavaScript 的值。优先用 [FigmaImportParentNodeTypes] 里的常量。
 */
fun FigmaImportParentNodeType(value: String): FigmaImportParentNodeType = unsafeCast(value)

/**
 * [FigmaImportParentNodeType] 的全部字面量。
 */
object FigmaImportParentNodeTypes {
    inline val canvas: FigmaImportParentNodeType get() = FigmaImportParentNodeType("CANVAS")
    inline val frame: FigmaImportParentNodeType get() = FigmaImportParentNodeType("FRAME")
    inline val group: FigmaImportParentNodeType get() = FigmaImportParentNodeType("GROUP")
    inline val section: FigmaImportParentNodeType get() = FigmaImportParentNodeType("SECTION")
    inline val componentSet: FigmaImportParentNodeType
        get() = FigmaImportParentNodeType("COMPONENT_SET")
}

/**
 * Icon node type: `"FRAME"`, `"COMPONENT"` or `"INSTANCE"`.
 */
sealed external interface FigmaImportIconNodeType

/**
 * 包装来自 JavaScript 的值。优先用 [FigmaImportIconNodeTypes] 里的常量。
 */
fun FigmaImportIconNodeType(value: String): FigmaImportIconNodeType = unsafeCast(value)

/**
 * [FigmaImportIconNodeType] 的全部字面量。
 */
object FigmaImportIconNodeTypes {
    inline val frame: FigmaImportIconNodeType get() = FigmaImportIconNodeType("FRAME")
    inline val component: FigmaImportIconNodeType get() = FigmaImportIconNodeType("COMPONENT")
    inline val instance: FigmaImportIconNodeType get() = FigmaImportIconNodeType("INSTANCE")
}

/**
 * Parent node information passed to [FigmaImportParentNodeFilter].
 */
external interface FigmaParentNodeData {
    /** Node ID. */
    var id: String

    /** Node type. */
    var type: FigmaImportParentNodeType

    /** Node name. */
    var name: String
}

/**
 * Node information passed to [FigmaImportNodeFilter].
 */
external interface FigmaImportNodeData {
    /** Node ID. */
    var id: String

    /** Node type. */
    var type: FigmaImportIconNodeType

    /** Node name. */
    var name: String

    /** Node width. */
    var width: Double

    /** Node height. */
    var height: Double

    /** Parent nodes, direct parent first. */
    var parents: Array<FigmaParentNodeData>
}

/**
 * Callback that checks whether a parent node needs to be scanned for icons.
 */
typealias FigmaImportParentNodeFilter =
    (node: Array<FigmaParentNodeData>, document: FigmaDocument) -> Boolean

/**
 * Icon node with only the keyword filled in.
 */
external interface FigmaIconNodeWithKeyword {
    /** Icon keyword. */
    var keyword: String

    /** Node ID. */
    var id: String?

    /** Node name. */
    var name: String?

    /** URL to download the icon from. */
    var url: String?

    /** Downloaded SVG content. */
    var content: String?
}

/**
 * 上游 `string | FigmaIconNodeWithKeyword`：图标名称或部分图标节点。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface FigmaImportNodeName

fun FigmaImportNodeName(value: String): FigmaImportNodeName = unsafeCast(value)

fun FigmaImportNodeName(value: FigmaIconNodeWithKeyword): FigmaImportNodeName = unsafeCast(value)

/**
 * Callback that checks whether a node is an icon.
 *
 * Returns the icon name, a partially filled icon node, or `null`/`undefined` to skip it.
 */
typealias FigmaImportNodeFilter =
    (
        node: FigmaImportNodeData,
        nodes: FigmaNodesImportResult,
        document: FigmaDocument,
    ) -> FigmaImportNodeName?

/**
 * Callback invoked before or after importing an icon.
 */
typealias FigmaImportedIconCallback = (node: FigmaIconNode, iconSet: IconSet) -> AwaitableUnit

/**
 * Options for [importFromFigma].
 */
external interface FigmaImportOptions :
    FigmaFilesQueryOptions,
    FigmaImagesQueryOptions,
    FigmaGetIconNodesOptions {
    /**
     * Icon set prefix.
     */
    var prefix: String

    /**
     * Cache directory.
     */
    var cacheDir: String?

    /**
     * Cache TTL for API queries in seconds.
     *
     * Defaults to three days.
     */
    var cacheAPITTL: Double?

    /**
     * Cache TTL for downloaded SVG files in seconds.
     *
     * Defaults to 30 days.
     */
    var cacheSVGTTL: Double?

    /**
     * Callback invoked before importing each icon.
     */
    var beforeImportingIcon: FigmaImportedIconCallback?

    /**
     * Callback invoked after importing each icon.
     */
    var afterImportingIcon: FigmaImportedIconCallback?
}

/**
 * Icon node from a Figma document.
 */
external interface FigmaIconNode {
    /**
     * Node ID.
     */
    var id: String

    /**
     * Node name.
     */
    var name: String

    /**
     * Icon keyword.
     */
    var keyword: String

    /**
     * URL to download the icon from.
     */
    var url: String?

    /**
     * Downloaded SVG content.
     */
    var content: String?
}

/**
 * Node and icon counters.
 */
external interface FigmaNodesCount {
    /**
     * Number of nodes marked as icons.
     */
    var nodesCount: Double

    /**
     * Number of generated icons.
     */
    var generatedIconsCount: Double

    /**
     * Number of downloaded icons.
     */
    var downloadedIconsCount: Double
}

/**
 * Import result for icons, before the icon set is built.
 */
external interface FigmaNodesImportResult {
    /**
     * Number of nodes marked as icons.
     */
    var nodesCount: Double?

    /**
     * Number of generated icons.
     */
    var generatedIconsCount: Double?

    /**
     * Number of downloaded icons.
     */
    var downloadedIconsCount: Double?

    /**
     * Found icons, keyed by icon name.
     */
    var icons: Record<String, FigmaIconNode>
}

/**
 * Result of [importFromFigma].
 */
external interface FigmaImportResult : FigmaNodesCount {
    /**
     * Document name.
     */
    var name: String

    /**
     * Document version.
     */
    var version: String

    /**
     * Last modification time of the document.
     */
    var lastModified: String

    /**
     * Imported icon set.
     */
    var iconSet: IconSet

    /**
     * Nodes that could not be imported.
     */
    var missing: Array<FigmaIconNode>
}

/**
 * 上游 `FigmaImportResult | "not_modified"`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface FigmaImportResultOrNotModified

fun FigmaImportResultOrNotModified(
    value: FigmaImportResult,
): FigmaImportResultOrNotModified = unsafeCast(value)

fun FigmaImportResultOrNotModified(
    value: DocumentNotModified,
): FigmaImportResultOrNotModified = unsafeCast(value)

/**
 * Bounding box of a Figma node.
 */
external interface FigmaBoundingBox {
    /** X position. */
    var x: Double

    /** Y position. */
    var y: Double

    /** Width. */
    var width: Double

    /** Height. */
    var height: Double
}

/**
 * Common fields of Figma API nodes.
 */
external interface BaseFigmaNode {
    /** Node ID. */
    var id: String

    /** Node name. */
    var name: String
}

/**
 * Figma node with an arbitrary type.
 */
external interface GenericFigmaNode : BaseFigmaNode {
    /** Node type, such as `"GROUP"` or `"TEXT"`. */
    var type: String

    /** Child nodes. */
    var children: Array<FigmaNode>?
}

/**
 * Figma node that can be exported as an icon.
 */
external interface IconFigmaNode : BaseFigmaNode {
    /** Node type. */
    var type: FigmaImportIconNodeType

    /** `true` when the node clips its content. */
    var clipsContent: Boolean?

    /** Absolute bounding box. */
    var absoluteBoundingBox: FigmaBoundingBox?

    /** Child nodes. */
    var children: Array<FigmaNode>
}

/**
 * Node type of the document root: `"DOCUMENT"`.
 */
sealed external interface FigmaDocumentNodeType

/**
 * 包装来自 JavaScript 的值。优先用 [FigmaDocumentNodeTypes] 里的常量。
 */
fun FigmaDocumentNodeType(value: String): FigmaDocumentNodeType = unsafeCast(value)

/**
 * [FigmaDocumentNodeType] 的全部字面量。
 */
object FigmaDocumentNodeTypes {
    inline val document: FigmaDocumentNodeType get() = FigmaDocumentNodeType("DOCUMENT")
}

/**
 * Document root node.
 */
external interface FigmaDocumentNode : BaseFigmaNode {
    /** Node type. */
    var type: FigmaDocumentNodeType

    /** Child nodes. */
    var children: Array<FigmaNode>
}

/**
 * 上游 `GenericFigmaNode | IconFigmaNode`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface FigmaNode

fun FigmaNode(value: GenericFigmaNode): FigmaNode = unsafeCast(value)

fun FigmaNode(value: IconFigmaNode): FigmaNode = unsafeCast(value)

/**
 * Editor type: `"figma"` or `"figjam"`.
 */
sealed external interface FigmaEditorType

/**
 * 包装来自 JavaScript 的值。优先用 [FigmaEditorTypes] 里的常量。
 */
fun FigmaEditorType(value: String): FigmaEditorType = unsafeCast(value)

/**
 * [FigmaEditorType] 的全部字面量。
 */
object FigmaEditorTypes {
    inline val figma: FigmaEditorType get() = FigmaEditorType("figma")
    inline val figjam: FigmaEditorType get() = FigmaEditorType("figjam")
}

/**
 * Document response from the Figma API.
 */
external interface FigmaDocument {
    /** Document root. */
    var document: FigmaDocumentNode

    /** Document name. */
    var name: String

    /** Document version. */
    var version: String

    /** Last modification time. */
    var lastModified: String

    /** Thumbnail URL. */
    var thumbnailUrl: String

    /** Role of the current user. */
    var role: String

    /** Editor type. */
    var editorType: FigmaEditorType
}

/**
 * Error response from the Figma API.
 */
external interface FigmaAPIError {
    /** HTTP status code. */
    var status: Double

    /** Error message. */
    var err: String
}

/**
 * Response of the Figma images endpoint.
 */
external interface FigmaAPIImagesResponse {
    /** Error message, if any. */
    var err: String?

    /** Image URLs keyed by node ID; `null` when the image could not be generated. */
    var images: Record<String, String?>
}
