package demo

/**
 * UI language of the demo page.
 *
 * Only the demo is translated; the Dokka API reference stays in English.
 */
internal enum class Language(val code: String, val switchLabel: String) {
    English("en", "中文"),
    Chinese("zh", "English"),
    ;

    /** Language the switch button moves to. */
    fun other(): Language = if (this == English) Chinese else English

    companion object {
        fun fromCode(code: String?): Language? = entries.firstOrNull { it.code == code }
    }
}

/** All user-facing copy of the demo page. */
internal interface Strings {
    val navDemo: String
    val navApi: String
    val documentTitle: String
    val heroTitle: String
    val heroLead: String
    val heroApiAction: String
    val heroGithubAction: String
    val featuredTitle: String
    val featuredDescription: String
    val transformationsTitle: String
    val transformationsDescription: String
    val inlinePrefix: String
    val inlineSuffix: String
    val modesTitle: String
    val modesDescription: String
    val modeSvg: String
    val modeBackground: String
    val modeMask: String
    val storageTitle: String
    val storageDescription: String
    val storageInfoUnavailable: String
    fun storageInfo(bodyLength: Int, width: Int, height: Int): String
    val loaderTitle: String
    val loaderDescription: String
    val loaderInputLabel: String
    val loaderButton: String
    val loaderIdle: String
    fun loaderMissingName(): String
    fun loaderLoading(name: String): String
    fun loaderLoaded(name: String, width: Int, height: Int): String
    fun loaderFailed(name: String, error: String): String
    val unknownError: String
    val footerLicense: String
    val footerApi: String
    val switchLanguageAria: String
}

internal fun stringsFor(language: Language): Strings = when (language) {
    Language.English -> EnglishStrings
    Language.Chinese -> ChineseStrings
}

private object EnglishStrings : Strings {
    override val navDemo = "Demo"
    override val navApi = "API docs"
    override val documentTitle = "ktjs-iconify · Kotlin/JS wrappers for Iconify"
    override val heroTitle = "Typed Kotlin/JS wrappers for Iconify"
    override val heroLead =
        "Render icons with the official web component, parse icon sets, and build SVG, " +
            "CSS and emoji helpers from Kotlin/JS — without hand-written externals."
    override val heroApiAction = "Browse the API reference"
    override val heroGithubAction = "View on GitHub"
    override val featuredTitle = "Icons from the Iconify API"
    override val featuredDescription =
        "Each tile is a real <iconify-icon> element created through ktjs-iconify-icon."
    override val transformationsTitle = "Transformations and inline icons"
    override val transformationsDescription =
        "rotate, flip, width and height map directly to the web component attributes."
    override val inlinePrefix = "Pass "
    override val inlineSuffix = " to keep the icon aligned with the surrounding text."
    override val modesTitle = "Rendering modes"
    override val modesDescription = "Pick the element output that fits your layout."
    override val modeSvg = "Default <svg> rendering"
    override val modeBackground = "Background image"
    override val modeMask = "CSS mask, follows currentColor"
    override val storageTitle = "Local icons and @iconify/utils"
    override val storageDescription =
        "addIcon() stores data without the network; the report below comes from iconToSVG(), " +
            "iconToHTML(), stringToIcon(), colors and emoji helpers."
    override val storageInfoUnavailable = "Icon data is not available."
    override fun storageInfo(bodyLength: Int, width: Int, height: Int) =
        "getIcon(\"demo:star\") -> body $bodyLength chars, $width × $height"
    override val loaderTitle = "Load any icon"
    override val loaderDescription =
        "Type a name such as mdi:home or @provider:prefix:name; the icon is fetched on demand."
    override val loaderInputLabel = "Icon name"
    override val loaderButton = "Load icon"
    override val loaderIdle = "Loading uses loadIcon() from the Iconify API."
    override fun loaderMissingName() = "Enter an icon name first."
    override fun loaderLoading(name: String) = "Loading $name…"
    override fun loaderLoaded(name: String, width: Int, height: Int) = "Loaded $name ($width × $height)"
    override fun loaderFailed(name: String, error: String) = "Could not load $name: $error"
    override val unknownError = "unknown error"
    override val footerLicense = "ktjs-iconify · MIT-licensed wrappers for Iconify"
    override val footerApi = "API docs"
    override val switchLanguageAria = "Switch to Chinese"
}

private object ChineseStrings : Strings {
    override val navDemo = "演示"
    override val navApi = "API 文档"
    override val documentTitle = "ktjs-iconify · Iconify 的 Kotlin/JS 封装"
    override val heroTitle = "为 Iconify 打造的 Kotlin/JS 类型化封装"
    override val heroLead =
        "用官方 Web Component 渲染图标，解析图标集，并在 Kotlin/JS 中生成 SVG、CSS 和 emoji 结果" +
            "——无需手写 externals。"
    override val heroApiAction = "浏览 API 文档"
    override val heroGithubAction = "在 GitHub 上查看"
    override val featuredTitle = "来自 Iconify API 的图标"
    override val featuredDescription =
        "每个卡片都是由 ktjs-iconify-icon 创建的真实 <iconify-icon> 元素。"
    override val transformationsTitle = "变换与行内图标"
    override val transformationsDescription =
        "rotate、flip、width 和 height 直接映射到 Web Component 的属性。"
    override val inlinePrefix = "传入 "
    override val inlineSuffix = "，让图标与周围文字对齐。"
    override val modesTitle = "渲染模式"
    override val modesDescription = "按布局需要选择图标的输出形式。"
    override val modeSvg = "默认 <svg> 渲染"
    override val modeBackground = "背景图"
    override val modeMask = "CSS mask，跟随 currentColor"
    override val storageTitle = "本地图标与 @iconify/utils"
    override val storageDescription =
        "addIcon() 无需网络即可保存图标数据；下方报告来自 iconToSVG()、iconToHTML()、" +
            "stringToIcon()、颜色与 emoji 工具。"
    override val storageInfoUnavailable = "图标数据不可用。"
    override fun storageInfo(bodyLength: Int, width: Int, height: Int) =
        "getIcon(\"demo:star\") → body $bodyLength 字符，$width × $height"
    override val loaderTitle = "加载任意图标"
    override val loaderDescription =
        "输入 mdi:home 或 @provider:prefix:name 这类名称，图标会按需获取。"
    override val loaderInputLabel = "图标名称"
    override val loaderButton = "加载图标"
    override val loaderIdle = "加载使用 Iconify API 的 loadIcon()。"
    override fun loaderMissingName() = "请先输入图标名称。"
    override fun loaderLoading(name: String) = "正在加载 $name…"
    override fun loaderLoaded(name: String, width: Int, height: Int) = "已加载 $name（$width × $height）"
    override fun loaderFailed(name: String, error: String) = "无法加载 $name：$error"
    override val unknownError = "未知错误"
    override val footerLicense = "ktjs-iconify · MIT 许可的 Iconify 封装"
    override val footerApi = "API 文档"
    override val switchLanguageAria = "切换到英文"
}
