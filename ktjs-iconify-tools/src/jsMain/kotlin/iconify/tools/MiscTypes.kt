package iconify.tools

import js.objects.Record
import js.reflect.unsafeCast
import kotlin.js.Date
import kotlin.js.Promise

/**
 * File statistics passed to [scanDirectory] callbacks.
 *
 * Mirrors the fields of Node.js `fs.Stats` that callbacks can use; the upstream type is
 * `node:fs.Stats`.
 */
external interface Stats {
    /** Device ID. */
    val dev: Double
    /** Inode number. */
    val ino: Double
    /** File mode. */
    val mode: Double
    /** Number of hard links. */
    val nlink: Double
    /** Owner user ID. */
    val uid: Double
    /** Owner group ID. */
    val gid: Double
    /** Device ID, if the file is special. */
    val rdev: Double
    /** File size in bytes. */
    val size: Double
    /** Block size for filesystem I/O. */
    val blksize: Double
    /** Number of 512-byte blocks allocated. */
    val blocks: Double
    /** Last access time in milliseconds. */
    val atimeMs: Double
    /** Last modification time in milliseconds. */
    val mtimeMs: Double
    /** Last status change time in milliseconds. */
    val ctimeMs: Double
    /** Creation time in milliseconds. */
    val birthtimeMs: Double
    /** Last access time. */
    val atime: Date
    /** Last modification time. */
    val mtime: Date
    /** Last status change time. */
    val ctime: Date
    /** Creation time. */
    val birthtime: Date

    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isBlockDevice(): Boolean
    fun isCharacterDevice(): Boolean
    fun isFIFO(): Boolean
    fun isSocket(): Boolean
    fun isSymbolicLink(): Boolean
}

/**
 * Options for [execAsync], mirroring Node.js `child_process.ExecOptions`.
 */
external interface ExecOptions {
    /** Working directory. */
    var cwd: String?
    /** Environment variables. */
    var env: Record<String, String?>?
    /** Output encoding. */
    var encoding: String?
    /** Shell to run the command with. */
    var shell: String?
    /** Timeout in milliseconds. */
    var timeout: Double?
    /** Maximum output size in bytes. */
    var maxBuffer: Double?
    /** Signal used to kill the process on timeout. */
    var killSignal: String?
    /** Hide the subprocess console window on Windows. */
    var windowsHide: Boolean?
}

/**
 * `URLSearchParams` global, used for API query parameters.
 *
 * The upstream type is the Node.js/DOM `URLSearchParams`; this interface declares the
 * commonly used subset.
 */
external interface URLSearchParams {
    fun append(name: String, value: String)
    fun delete(name: String)
    fun get(name: String): String?
    fun getAll(name: String): Array<String>
    fun has(name: String): Boolean
    fun set(name: String, value: String)
    fun sort()
    override fun toString(): String
}

/**
 * Result of a [scanDirectory] callback: `false`, `null` or `undefined` skips the file, `true`
 * includes `subdir + file`, and a string includes the custom value.
 */
external interface ScanDirectoryCallbackResult

fun ScanDirectoryCallbackResult(value: Boolean): ScanDirectoryCallbackResult = unsafeCast(value)

fun ScanDirectoryCallbackResult(value: String): ScanDirectoryCallbackResult = unsafeCast(value)

/**
 * Awaitable [ScanDirectoryCallbackResult].
 */
external interface AwaitableScanDirectoryCallbackResult

fun AwaitableScanDirectoryCallbackResult(
    value: ScanDirectoryCallbackResult,
): AwaitableScanDirectoryCallbackResult = unsafeCast(value)

fun AwaitableScanDirectoryCallbackResult(
    value: Promise<ScanDirectoryCallbackResult?>,
): AwaitableScanDirectoryCallbackResult = unsafeCast(value)

/**
 * Callback for the asynchronous [scanDirectory].
 */
typealias ScanDirectoryCallback =
    (
        ext: String,
        file: String,
        subdir: String,
        path: String,
        stat: Stats,
    ) -> AwaitableScanDirectoryCallbackResult?

/**
 * Callback for the synchronous [scanDirectorySync].
 */
typealias ScanDirectorySyncCallback =
    (
        ext: String,
        file: String,
        subdir: String,
        path: String,
        stat: Stats,
    ) -> ScanDirectoryCallbackResult?

/**
 * Custom callback result for the typed [scanDirectoryValues] and [scanDirectoryValuesSync]:
 * a custom value, `true`/`false` or a string.
 */
external interface ScanDirectoryValue<T : JsAny?>

fun <T : JsAny?> ScanDirectoryValue(value: T): ScanDirectoryValue<T> =
    unsafeCast<ScanDirectoryValue<T>>(value)!!

fun <T : JsAny?> ScanDirectoryValue(value: Boolean): ScanDirectoryValue<T> =
    unsafeCast(value)

fun <T : JsAny?> ScanDirectoryValue(value: String): ScanDirectoryValue<T> =
    unsafeCast(value)

/**
 * Awaitable [ScanDirectoryValue].
 */
external interface AwaitableScanDirectoryValue<T : JsAny?>

fun <T : JsAny?> AwaitableScanDirectoryValue(
    value: ScanDirectoryValue<T>,
): AwaitableScanDirectoryValue<T> = unsafeCast(value)

fun <T : JsAny?> AwaitableScanDirectoryValue(
    value: Promise<ScanDirectoryValue<T>?>,
): AwaitableScanDirectoryValue<T> = unsafeCast(value)

/**
 * Callback for the asynchronous [scanDirectoryValues].
 */
typealias ScanDirectoryValueCallback<T> =
    (
        ext: String,
        file: String,
        subdir: String,
        path: String,
        stat: Stats,
    ) -> AwaitableScanDirectoryValue<T>?

/**
 * Callback for the synchronous [scanDirectoryValuesSync].
 */
typealias ScanDirectoryValueSyncCallback<T> =
    (
        ext: String,
        file: String,
        subdir: String,
        path: String,
        stat: Stats,
    ) -> ScanDirectoryValue<T>?
