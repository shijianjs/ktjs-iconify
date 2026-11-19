package io.github.shijianjs.ktjs.iconify.demo

import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.SiteTexts
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/**
 * Every string the layout and the navigation show, in English.
 *
 * One object per language, declared once in [siteTexts].
 */
private val englishSite: SiteTexts = SiteTexts(
    siteTitle = "ktjs-iconify",
    langLabel = "English",
    languageLabel = "Language",
    themeLabel = "Theme",
    docsLabel = "Docs",
    siteDocsLabel = "Documentation",
    apiLabel = "API reference",
    sourceLabel = "GitHub",
    lightLabel = "Light",
    darkLabel = "Dark",
    systemLabel = "Follow browser",
    groupTitles = mapOf(
        "start" to "Getting started",
        "element" to "The element",
        "utils" to "Utils and loading",
    ),
    pageTitles = mapOf(
        "overview" to "Overview",
        "transformations" to "Rotate and flip",
        "modes" to "Rendering modes",
        "utils" to "SVG, CSS and registration",
        "loader" to "Load an icon by name",
    ),
)

private val chineseSite: SiteTexts = SiteTexts(
    siteTitle = "ktjs-iconify",
    langLabel = "中文",
    languageLabel = "语言",
    themeLabel = "外观",
    docsLabel = "文档",
    siteDocsLabel = "使用文档",
    apiLabel = "API 文档",
    sourceLabel = "GitHub 仓库",
    lightLabel = "亮色",
    darkLabel = "暗色",
    systemLabel = "跟随浏览器",
    groupTitles = mapOf(
        "start" to "开始",
        "element" to "图标元素",
        "utils" to "工具与加载",
    ),
    pageTitles = mapOf(
        "overview" to "概览",
        "transformations" to "旋转与镜像",
        "modes" to "渲染模式",
        "utils" to "SVG、CSS 与本地注册",
        "loader" to "按名字加载图标",
    ),
)

/** The layout's strings, one entry per registered language. */
val siteTexts: Translations<SiteTexts> = texts(
    Langs.EN to englishSite,
    Langs.ZH_HANS to chineseSite,
)
