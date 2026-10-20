@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.Promise

external fun writeJSONFile(filename: String, data: dynamic): Promise<Unit>

/**
 * Find files in a directory. The callback can return a custom value per file.
 */
external fun scanDirectory(
    path: String,
    callback: ((String, String, String, String, dynamic) -> dynamic)? = definedExternally,
    subdirs: Boolean = definedExternally,
): Promise<Array<String>>

external fun scanDirectorySync(
    path: String,
    callback: ((String, String, String, String, dynamic) -> dynamic)? = definedExternally,
    subdirs: Boolean = definedExternally,
): Array<String>

external fun compareDirectories(
    dir1: String,
    dir2: String,
    options: CompareDirectoriesOptions? = definedExternally,
): Promise<Boolean>

external fun execAsync(cmd: String, options: dynamic = definedExternally): Promise<ExecResult>

/**
 * Clean up an icon keyword.
 */
external fun cleanupIconKeyword(keyword: String, convertCamelCase: Boolean = definedExternally): String

/**
 * Bump a version string, for example `1.2.3` to `1.2.4`.
 */
external fun bumpVersion(version: String): String
