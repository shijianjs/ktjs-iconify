@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.JsAny
import kotlin.js.Promise

/**
 * Write JSON to a file, using tab indentation and a trailing newline.
 *
 * 上游类型是 `unknown`，因此 [data] 是不透明的。
 */
external fun writeJSONFile(filename: String, data: JsAny?): Promise<Unit>

/**
 * Find files in a directory. The callback can return a custom string per file.
 *
 * Hidden files and directories are skipped, and subdirectories are scanned unless
 * [subdirs] is `false`. The callback receives `ext` (starts with `.`, can be empty),
 * `file` (name without extension), `subdir` (ends with `/`, can be empty), the scanned
 * path and file stats. Return `false`, `null` or `undefined` to skip the file, `true` to
 * include `subdir + filename`, or a custom string to include it as-is. The callback may be
 * asynchronous.
 */
external fun scanDirectory(
    path: String,
    callback: ScanDirectoryCallback? = definedExternally,
    subdirs: Boolean = definedExternally,
): Promise<Array<String>>

/**
 * Synchronous version of [scanDirectory].
 */
external fun scanDirectorySync(
    path: String,
    callback: ScanDirectorySyncCallback? = definedExternally,
    subdirs: Boolean = definedExternally,
): Array<String>

/**
 * Find files in a directory, returning a custom value per file.
 *
 * Same as [scanDirectory], but the callback can return any [T] instead of a string.
 */
@JsName("scanDirectory")
external fun <T : JsAny?> scanDirectoryValues(
    path: String,
    callback: ScanDirectoryValueCallback<T>? = definedExternally,
    subdirs: Boolean = definedExternally,
): Promise<Array<T>>

/**
 * Synchronous version of [scanDirectoryValues].
 */
@JsName("scanDirectorySync")
external fun <T : JsAny?> scanDirectoryValuesSync(
    path: String,
    callback: ScanDirectoryValueSyncCallback<T>? = definedExternally,
    subdirs: Boolean = definedExternally,
): Array<T>

/**
 * Compare directories, returning `true` when all files are identical.
 *
 * Text files are compared after normalising newlines when
 * [CompareDirectoriesOptions.ignoreNewLine] is `true` (default), and the `version` field
 * in `package.json` is ignored when [CompareDirectoriesOptions.ignoreVersions] is `true`
 * (default). Other files are compared byte by byte.
 */
external fun compareDirectories(
    dir1: String,
    dir2: String,
    options: CompareDirectoriesOptions? = definedExternally,
): Promise<Boolean>

/**
 * Run a shell command. Resolves with `{ stdout, stderr }` and rejects when the command
 * fails. A relative `cwd` in [options] is resolved to an absolute path.
 */
external fun execAsync(
    cmd: String,
    options: ExecOptions? = definedExternally,
): Promise<ExecResult>

/**
 * Clean up an icon keyword.
 *
 * Lowercases the value, replaces whitespace and `_ . : / \` with dashes, removes other
 * invalid characters, collapses repeated dashes and trims leading/trailing dashes. With
 * [convertCamelCase], camelCase is converted to dash-case first.
 */
external fun cleanupIconKeyword(keyword: String, convertCamelCase: Boolean = definedExternally): String

/**
 * Bump a version string, for example `1.2.3` to `1.2.4`.
 *
 * If the last part is not a number, `.1` is appended instead.
 */
external fun bumpVersion(version: String): String
