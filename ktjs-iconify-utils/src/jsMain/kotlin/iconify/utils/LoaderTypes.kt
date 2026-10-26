package iconify.utils

import iconify.IconifyIcon
import iconify.IconifyJSON
import iconify.StringOrStringArray
import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.Promise

/**
 * 上游 `Awaitable<string>`：同步返回字符串，或返回 Promise。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface AwaitableString

fun AwaitableString(value: String): AwaitableString = unsafeCast(value)

fun AwaitableString(value: Promise<String>): AwaitableString = unsafeCast(value)

/**
 * 上游 `Awaitable<string | undefined>` 中的字符串与 Promise 分支。
 *
 * 同步返回 `undefined` 时直接写 `null`（见 [CustomIconLoader]）。
 */
external interface AwaitableOptionalString

fun AwaitableOptionalString(value: String): AwaitableOptionalString = unsafeCast(value)

fun AwaitableOptionalString(value: Promise<String?>): AwaitableOptionalString = unsafeCast(value)

/**
 * 上游 `Awaitable<IconifyJSON>`：同步返回图标集，或返回 Promise。
 */
external interface AwaitableIconifyJSON

fun AwaitableIconifyJSON(value: IconifyJSON): AwaitableIconifyJSON = unsafeCast(value)

fun AwaitableIconifyJSON(value: Promise<IconifyJSON>): AwaitableIconifyJSON = unsafeCast(value)

/**
 * 上游 `Awaitable<void>`：同步返回时写 `null`，异步返回时写 `Promise<Unit>`。
 */
typealias AwaitableUnit = Promise<Unit>?

/**
 * 上游 `Awaitable<Record<string, string>>`：同步返回属性表，或返回 Promise。
 */
external interface AwaitableIconProps

fun AwaitableIconProps(value: Record<String, String>): AwaitableIconProps = unsafeCast(value)

fun AwaitableIconProps(value: Promise<Record<String, String>>): AwaitableIconProps = unsafeCast(value)

/**
 * 上游 `CustomIconLoader`：按图标名返回 SVG 字符串，可同步或异步；图标缺失时返回 `null`。
 */
typealias CustomIconLoader = (name: String) -> AwaitableOptionalString?

/**
 * 上游 `InlineCollection` 的值：SVG 字符串，或按图标名返回 SVG 字符串的加载函数。
 */
external interface StringOrCustomIconLoader

fun StringOrCustomIconLoader(value: String): StringOrCustomIconLoader = unsafeCast(value)

fun StringOrCustomIconLoader(value: CustomIconLoader): StringOrCustomIconLoader = unsafeCast(value)

/**
 * 上游 `InlineCollection`：图标名到 SVG 字符串或加载函数的映射。
 */
typealias InlineCollection = Record<String, StringOrCustomIconLoader>

/**
 * 带 HMR 支持的自定义图标加载器。
 */
external interface CustomHMRIconLoader {
    /**
     * 内部标记，固定为 `true`，用于避免被误判成其他加载器。
     */
    var __iconifyCustomHmrIconLoader: Boolean

    /**
     * 自定义集合名称。
     */
    var name: String

    /**
     * 用于解析图标的自定义加载器。
     */
    var iconLoader: CustomIconLoader

    /**
     * 根据规范化后的 SVG 文件路径返回此前解析出的图标名；无法识别时返回 `undefined`。
     */
    var resolveModuleIconName: (normalizedSVGPath: String) -> String?

    /**
     * 若该集合能解析此图标，返回其规范化后的 SVG 文件路径；否则返回 `undefined`。
     */
    var resolveSVGIconPath: (name: String) -> String?
}

/**
 * 上游 `CustomCollectionIconLoader`：加载单个图标的函数、内联集合或 HMR 加载器对象。
 */
external interface CustomCollectionIconLoader

fun CustomCollectionIconLoader(value: CustomIconLoader): CustomCollectionIconLoader = unsafeCast(value)

fun CustomCollectionIconLoader(value: InlineCollection): CustomCollectionIconLoader = unsafeCast(value)

fun CustomCollectionIconLoader(value: CustomHMRIconLoader): CustomCollectionIconLoader = unsafeCast(value)

/**
 * 上游 `() => Awaitable<IconifyJSON>`：返回整个图标集的加载函数。
 */
typealias IconSetLoader = () -> AwaitableIconifyJSON

/**
 * [IconifyLoaderOptions.customCollections] 的值：图标集加载函数或单个图标的加载器。
 */
external interface CustomCollectionEntry

fun CustomCollectionEntry(value: IconSetLoader): CustomCollectionEntry = unsafeCast(value)

fun CustomCollectionEntry(value: CustomCollectionIconLoader): CustomCollectionEntry = unsafeCast(value)

/**
 * 上游 `CustomCollections`：集合名称到图标集加载函数或单图标加载器的映射。
 */
typealias CustomCollections = Record<String, CustomCollectionEntry?>

/**
 * 上游 `AutoInstall`：是否自动安装图标包，或安装图标包的回调。
 */
external interface AutoInstall

fun AutoInstall(value: Boolean): AutoInstall = unsafeCast(value)

fun AutoInstall(value: (name: String) -> Promise<Unit>): AutoInstall = unsafeCast(value)

/**
 * 上游 `IconCustomizer`：按集合、图标名和属性自定义图标，可同步或异步。
 */
typealias IconCustomizer = (collection: String, icon: String, props: Record<String, String>) -> AwaitableUnit

/**
 * 应用到所有已解析图标的自定义项。
 *
 * 处理顺序：`transform` → `customize` → `iconCustomizer` → `additionalProps`，最后在使用自定义
 * 集合且 [trimCustomSvg] 开启时执行 `trimSVG`。
 */
external interface IconCustomizations {
    /**
     * 转换原始 SVG。
     *
     * 仅在使用自定义集合、即调用 [getCustomIcon] 时生效；[searchForIcon] 不会应用。
     */
    var transform: ((svg: String, collection: String, icon: String) -> AwaitableString)?

    /**
     * 修改默认的自定义值。
     *
     * 图标数据可变，可直接修改。
     */
    var customize: ((defaultCustomizations: FullIconCustomisations, data: IconifyIcon, name: String) -> FullIconCustomisations?)?

    /**
     * 自定义图标属性。
     */
    var iconCustomizer: IconCustomizer?

    /**
     * 附加到图标上的属性；值为 `undefined` 的属性不会应用。
     */
    var additionalProps: Record<String, String?>?

    /**
     * 是否优化自定义 SVG 图标。
     *
     * 在 CSS 中使用自定义 SVG 时开启。
     *
     * 默认 `false`。
     */
    var trimCustomSvg: Boolean?
}

/**
 * Options for the icon loader.
 */
external interface IconifyLoaderOptions {
    /**
     * Emit a warning when a missing icon is matched by name search.
     *
     * The string is used as the warning message; `undefined` disables warnings.
     */
    var warn: String?

    /**
     * Add `xmlns` and `xlink` namespaces when necessary.
     *
     * Defaults to `false`.
     */
    var addXmlNs: Boolean?

    /**
     * Scale of icons against `1em`.
     */
    var scale: Double?

    /**
     * Style applied to icons by default.
     *
     * Defaults to an empty string.
     */
    var defaultStyle: String?

    /**
     * Class names applied to icons by default.
     *
     * Defaults to an empty string.
     */
    var defaultClass: String?

    /**
     * Custom collections, keyed by collection name.
     *
     * A value can be a loader function, an inline collection object, or a function returning an
     * icon set.
     */
    var customCollections: CustomCollections?

    /**
     * Customisations applied to all resolved icons.
     *
     * Supports `transform`, `customize`, `iconCustomizer`, `additionalProps` and `trimCustomSvg`.
     */
    var customizations: IconCustomizations?

    /**
     * Auto-install icon packages when their usage is detected.
     *
     * Can be a boolean or a function that installs the package. Node.js only; ignored in browsers.
     *
     * Defaults to `false`.
     */
    var autoInstall: AutoInstall?

    /**
     * Additional properties applied to the SVG.
     *
     * `width` and `height` already present on the SVG are kept. Set this to an empty object to keep
     * the properties from the customisations instead.
     */
    var usedProps: Record<String, String>?

    /**
     * Working directory used to resolve `@iconify-json` packages.
     *
     * A string or an array of strings, where the first existing path is used. Node.js only.
     *
     * Defaults to the current working directory.
     */
    var cwd: StringOrStringArray?
}
