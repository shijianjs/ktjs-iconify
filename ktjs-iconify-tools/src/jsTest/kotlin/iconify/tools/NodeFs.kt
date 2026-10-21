package iconify.tools

import kotlin.js.JsModule

@JsModule("node:fs")
internal external object nodeFs {
    fun mkdirSync(path: String, options: dynamic = definedExternally)
    fun rmSync(path: String, options: dynamic = definedExternally)
    fun existsSync(path: String): Boolean
    fun writeFileSync(path: String, data: String)
    fun readFileSync(path: String, encoding: String): String
    fun readdirSync(path: String): Array<String>
    fun statSync(path: String): dynamic
}

@JsModule("node:path")
internal external object nodePath {
    fun resolve(path: String): String
}

/**
 * Convert a Windows path to the forward-slash form used by `pathe`.
 */
internal fun normalizePath(path: String): String =
    js("(path) => path.replace(/\\\\/g, '/')")(path)
