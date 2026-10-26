package iconify.tools

import iconify.utils.AwaitableUnit
import iconify.utils.StringOrDouble
import js.objects.Record
import js.reflect.unsafeCast
import org.w3c.fetch.Request
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.JsAny
import kotlin.js.Promise

/**
 * Download source type: `"git"`, `"github"`, `"gitlab"` or `"npm"`.
 */
sealed external interface DownloadSourceType

/**
 * 包装来自 JavaScript 的值。优先用 [DownloadSourceTypes] 里的常量。
 */
fun DownloadSourceType(value: String): DownloadSourceType = unsafeCast(value)

/**
 * [DownloadSourceType] 的全部字面量。
 */
object DownloadSourceTypes {
    inline val git: DownloadSourceType get() = DownloadSourceType("git")
    inline val github: DownloadSourceType get() = DownloadSourceType("github")
    inline val gitlab: DownloadSourceType get() = DownloadSourceType("gitlab")
    inline val npm: DownloadSourceType get() = DownloadSourceType("npm")
}

/**
 * Source type carried by download results and params.
 *
 * Each result and params type uses a fixed literal; this base type keeps the value
 * compiler-checked instead of a plain string.
 */
external interface DownloadSourceMixin {
    /**
     * Download source type.
     */
    var downloadType: DownloadSourceType
}

/**
 * 上游 `"not_modified"`：请求的版本或提交没有变化。
 */
sealed external interface DocumentNotModified

/**
 * 包装来自 JavaScript 的值。优先用 [DocumentNotModifieds] 里的常量。
 */
fun DocumentNotModified(value: String): DocumentNotModified = unsafeCast(value)

/**
 * [DocumentNotModified] 的全部字面量。
 */
object DocumentNotModifieds {
    inline val notModified: DocumentNotModified get() = DocumentNotModified("not_modified")
}

/**
 * NPM package options.
 */
external interface NPMPackageOptions {
    /**
     * Package name.
     */
    var `package`: String

    /**
     * Package tag.
     *
     * Defaults to `"latest"`.
     */
    var tag: String?

    /**
     * Use HTTP requests instead of the `npm` command to get the version.
     *
     * Use it in environments where the `npm` command is not available.
     */
    var fetch: Boolean?
}

/**
 * NPM version information.
 */
external interface GetNPMVersionResult {
    /**
     * Package version.
     */
    var version: String

    /**
     * URL of the package tarball.
     */
    var file: String?
}

/**
 * NPM download result.
 */
external interface DownloadNPMPackageResult : DownloadSourceMixin {
    /**
     * Directory where the archive was unpacked.
     */
    var rootDir: String

    /**
     * Directory with the package contents: `rootDir/package`.
     */
    var contentsDir: String

    /**
     * Downloaded version.
     */
    var version: String
}

/**
 * 上游 `string | true | DownloadNPMPackageResult`：版本号、`true` 或上一次的下载结果。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadNPMPackageIfModifiedSince

fun DownloadNPMPackageIfModifiedSince(
    value: String,
): DownloadNPMPackageIfModifiedSince = unsafeCast(value)

fun DownloadNPMPackageIfModifiedSince(
    value: Boolean,
): DownloadNPMPackageIfModifiedSince = unsafeCast(value)

fun DownloadNPMPackageIfModifiedSince(
    value: DownloadNPMPackageResult,
): DownloadNPMPackageIfModifiedSince = unsafeCast(value)

/**
 * Options for [downloadNPMPackage].
 */
external interface DownloadNPMPackageOptions : NPMPackageOptions, ExportTargetOptions {
    /**
     * Download only if the version changed: a version string, `true` to check the version stored in
     * the target directory, or the previous download result.
     */
    var ifModifiedSince: DownloadNPMPackageIfModifiedSince?

    /**
     * Log commands.
     */
    var log: Boolean?
}

/**
 * 上游 `DownloadNPMPackageResult | "not_modified"`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadNPMPackageResultOrNotModified

fun DownloadNPMPackageResultOrNotModified(
    value: DownloadNPMPackageResult,
): DownloadNPMPackageResultOrNotModified = unsafeCast(value)

fun DownloadNPMPackageResultOrNotModified(
    value: DocumentNotModified,
): DownloadNPMPackageResultOrNotModified = unsafeCast(value)

/**
 * GitHub API options.
 */
external interface GitHubAPIOptions {
    /**
     * API token.
     */
    var token: String

    /**
     * GitHub user or organisation.
     */
    var user: String

    /**
     * Repository name.
     */
    var repo: String

    /**
     * Branch name.
     */
    var branch: String
}

/**
 * GitHub download result.
 */
external interface DownloadGitHubRepoResult : DownloadSourceMixin {
    /**
     * Root download directory.
     */
    var rootDir: String

    /**
     * Directory with the repository contents.
     */
    var contentsDir: String

    /**
     * Commit hash.
     */
    var hash: String
}

/**
 * 上游 `string | DownloadGitHubRepoResult`：提交 hash 或上一次的下载结果。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitHubRepoIfModifiedSince

fun DownloadGitHubRepoIfModifiedSince(
    value: String,
): DownloadGitHubRepoIfModifiedSince = unsafeCast(value)

fun DownloadGitHubRepoIfModifiedSince(
    value: DownloadGitHubRepoResult,
): DownloadGitHubRepoIfModifiedSince = unsafeCast(value)

/**
 * Options for [downloadGitHubRepo].
 */
external interface DownloadGitHubRepoOptions : ExportTargetOptions, GitHubAPIOptions {
    /**
     * Download only if the commit hash changed: a hash string, or the previous download result.
     */
    var ifModifiedSince: DownloadGitHubRepoIfModifiedSince?

    /**
     * Remove files from old downloads.
     *
     * Defaults to `false`.
     */
    var cleanupOldFiles: Boolean?

    /**
     * Remove directories from old downloads.
     *
     * Defaults to `true`.
     */
    var cleanupOldDirectories: Boolean?

    /**
     * Log commands.
     */
    var log: Boolean?

    /**
     * Filter files; receives the full path and returns `true` to keep the file.
     */
    var filter: ((String) -> Boolean)?
}

/**
 * 上游 `DownloadGitHubRepoResult | "not_modified"`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitHubRepoResultOrNotModified

fun DownloadGitHubRepoResultOrNotModified(
    value: DownloadGitHubRepoResult,
): DownloadGitHubRepoResultOrNotModified = unsafeCast(value)

fun DownloadGitHubRepoResultOrNotModified(
    value: DocumentNotModified,
): DownloadGitHubRepoResultOrNotModified = unsafeCast(value)

/**
 * GitLab API options.
 */
external interface GitLabAPIOptions {
    /**
     * Base URI of the GitLab API.
     *
     * Defaults to `https://gitlab.com/api/v4/projects`.
     */
    var uri: String?

    /**
     * API token.
     */
    var token: String

    /**
     * Project ID.
     */
    var project: String

    /**
     * Branch name.
     */
    var branch: String
}

/**
 * GitLab download result.
 */
external interface DownloadGitLabRepoResult : DownloadSourceMixin {
    /**
     * Root download directory.
     */
    var rootDir: String

    /**
     * Directory with the repository contents.
     */
    var contentsDir: String

    /**
     * Commit hash.
     */
    var hash: String
}

/**
 * 上游 `string | DownloadGitLabRepoResult`：提交 hash 或上一次的下载结果。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitLabRepoIfModifiedSince

fun DownloadGitLabRepoIfModifiedSince(
    value: String,
): DownloadGitLabRepoIfModifiedSince = unsafeCast(value)

fun DownloadGitLabRepoIfModifiedSince(
    value: DownloadGitLabRepoResult,
): DownloadGitLabRepoIfModifiedSince = unsafeCast(value)

/**
 * Options for [downloadGitLabRepo].
 */
external interface DownloadGitLabRepoOptions : ExportTargetOptions, GitLabAPIOptions {
    /**
     * Download only if the commit hash changed: a hash string, or the previous download result.
     */
    var ifModifiedSince: DownloadGitLabRepoIfModifiedSince?

    /**
     * Remove files from old downloads.
     *
     * Defaults to `false`.
     */
    var cleanupOldFiles: Boolean?

    /**
     * Remove directories from old downloads.
     *
     * Defaults to `true`.
     */
    var cleanupOldDirectories: Boolean?

    /**
     * Log commands.
     */
    var log: Boolean?

    /**
     * Filter files; receives the full path and returns `true` to keep the file.
     */
    var filter: ((String) -> Boolean)?
}

/**
 * 上游 `DownloadGitLabRepoResult | "not_modified"`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitLabRepoResultOrNotModified

fun DownloadGitLabRepoResultOrNotModified(
    value: DownloadGitLabRepoResult,
): DownloadGitLabRepoResultOrNotModified = unsafeCast(value)

fun DownloadGitLabRepoResultOrNotModified(
    value: DocumentNotModified,
): DownloadGitLabRepoResultOrNotModified = unsafeCast(value)

/**
 * Git download result.
 */
external interface DownloadGitRepoResult : DownloadSourceMixin {
    /**
     * Directory with the repository contents.
     */
    var contentsDir: String

    /**
     * Commit hash.
     */
    var hash: String
}

/**
 * 上游 `string | true | DownloadGitRepoResult`：提交 hash、`true` 或上一次的下载结果。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitRepoIfModifiedSince

fun DownloadGitRepoIfModifiedSince(
    value: String,
): DownloadGitRepoIfModifiedSince = unsafeCast(value)

fun DownloadGitRepoIfModifiedSince(
    value: Boolean,
): DownloadGitRepoIfModifiedSince = unsafeCast(value)

fun DownloadGitRepoIfModifiedSince(
    value: DownloadGitRepoResult,
): DownloadGitRepoIfModifiedSince = unsafeCast(value)

/**
 * Options for [downloadGitRepo].
 */
external interface DownloadGitRepoOptions : ExportTargetOptions {
    /**
     * Repository URL.
     */
    var remote: String

    /**
     * Branch name.
     */
    var branch: String

    /**
     * Download only if the commit hash changed: a hash string, `true` to check the hash stored in
     * the target directory, or the previous download result.
     *
     * The function does not check that the target directory belongs to the requested repository
     * and branch, so do not reuse a target directory for different repositories or branches.
     */
    var ifModifiedSince: DownloadGitRepoIfModifiedSince?

    /**
     * Log commands.
     */
    var log: Boolean?
}

/**
 * 上游 `DownloadGitRepoResult | "not_modified"`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadGitRepoResultOrNotModified

fun DownloadGitRepoResultOrNotModified(
    value: DownloadGitRepoResult,
): DownloadGitRepoResultOrNotModified = unsafeCast(value)

fun DownloadGitRepoResultOrNotModified(
    value: DocumentNotModified,
): DownloadGitRepoResultOrNotModified = unsafeCast(value)

/**
 * Git options combined with the download source type, as accepted by [downloadPackage].
 */
external interface DownloadGitRepoParams : DownloadGitRepoOptions, DownloadSourceMixin

/**
 * GitHub options combined with the download source type, as accepted by [downloadPackage].
 */
external interface DownloadGitHubRepoParams : DownloadGitHubRepoOptions, DownloadSourceMixin

/**
 * GitLab options combined with the download source type, as accepted by [downloadPackage].
 */
external interface DownloadGitLabRepoParams : DownloadGitLabRepoOptions, DownloadSourceMixin

/**
 * NPM options combined with the download source type, as accepted by [downloadPackage].
 */
external interface DownloadNPMPackageParams : DownloadNPMPackageOptions, DownloadSourceMixin

/**
 * 上游 `DownloadGitRepo | DownloadGitHubRepo | DownloadGitLabRepo | DownloadNPMPackage`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadParams

fun DownloadParams(value: DownloadGitRepoParams): DownloadParams = unsafeCast(value)

fun DownloadParams(value: DownloadGitHubRepoParams): DownloadParams = unsafeCast(value)

fun DownloadParams(value: DownloadGitLabRepoParams): DownloadParams = unsafeCast(value)

fun DownloadParams(value: DownloadNPMPackageParams): DownloadParams = unsafeCast(value)

/**
 * 上游 `DocumentNotModified | DownloadGitRepoResult | DownloadGitHubRepoResult |
 * DownloadGitLabRepoResult | DownloadNPMPackageResult`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface DownloadResult

fun DownloadResult(value: DocumentNotModified): DownloadResult = unsafeCast(value)

fun DownloadResult(value: DownloadGitRepoResult): DownloadResult = unsafeCast(value)

fun DownloadResult(value: DownloadGitHubRepoResult): DownloadResult = unsafeCast(value)

fun DownloadResult(value: DownloadGitLabRepoResult): DownloadResult = unsafeCast(value)

fun DownloadResult(value: DownloadNPMPackageResult): DownloadResult = unsafeCast(value)

/**
 * API query parameters.
 */
external interface APIQueryParams {
    /**
     * Request URI, without query parameters.
     */
    var uri: String

    /**
     * Query parameters.
     */
    var params: URLSearchParams?

    /**
     * Request headers.
     */
    var headers: Record<String, String>?
}

/**
 * API response cache options.
 */
external interface APICacheOptions {
    /**
     * Directory where the cache is stored.
     */
    var dir: String

    /**
     * Time to live in seconds.
     */
    var ttl: Double
}

/**
 * Successful API query.
 */
external interface APIQuerySuccess {
    /** Always `true`. */
    var success: Boolean

    /** Response body. */
    var content: String
}

/**
 * Failed API query.
 */
external interface APIQueryFailure {
    /** Always `false`. */
    var success: Boolean

    /** Response, when one was received. */
    var response: Response?

    /** HTTP status code. */
    var error: Double
}

/**
 * 上游 `APIQuerySuccess | APIQueryFailure`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface APIQueryResult

fun APIQueryResult(value: APIQuerySuccess): APIQueryResult = unsafeCast(value)

fun APIQueryResult(value: APIQueryFailure): APIQueryResult = unsafeCast(value)

/**
 * 上游 `Request | string`：`fetch` 的输入。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface RequestOrString

fun RequestOrString(value: String): RequestOrString = unsafeCast(value)

fun RequestOrString(value: Request): RequestOrString = unsafeCast(value)

/**
 * Custom `fetch` implementation, matching `typeof fetch`.
 */
typealias FetchFunction = (input: RequestOrString, init: RequestInit?) -> Promise<Response>

/**
 * Parameters shared by both [ConcurrentQueriesParams] variants.
 */
external interface ConcurrentQueriesCommonParams<T : JsAny?> {
    /**
     * Maximum number of concurrent queries.
     */
    var limit: Double?

    /**
     * Number of retries for failed queries.
     */
    var retries: Double?

    /**
     * Callback invoked when a query fails after the last retry.
     */
    var onError: ((index: Double, error: JsAny?, params: ConcurrentQueriesParams<T>) -> AwaitableUnit)?

    /**
     * Callback invoked when a query succeeds.
     */
    var onSuccess: ((index: Double, params: ConcurrentQueriesParams<T>, result: T) -> Unit)?

    /**
     * Callback invoked before a query starts.
     */
    var onStart: ((index: Double, params: ConcurrentQueriesParams<T>) -> Unit)?
}

/**
 * Parameters that generate queries from an index.
 */
external interface ConcurrentQueriesParamsWithCount<T : JsAny?> : ConcurrentQueriesCommonParams<T> {
    /**
     * Number of queries.
     */
    var total: Double

    /**
     * Query factory, called with the query index.
     */
    var callback: (index: Double) -> Promise<T>
}

/**
 * Parameters that run an existing list of promises.
 */
external interface ConcurrentQueriesParamsWithPromises<T : JsAny?> : ConcurrentQueriesCommonParams<T> {
    /**
     * Promises to run.
     */
    var promises: Array<Promise<T>>
}

/**
 * 上游 `ConcurrentQueriesParamsWithCount<T> | ConcurrentQueriesParamsWithPromises<T>`。
 *
 * 用同名工厂函数包装，调用方必须显式说明选的是哪一支。
 */
external interface ConcurrentQueriesParams<T : JsAny?>

fun <T : JsAny?> ConcurrentQueriesParams(
    value: ConcurrentQueriesParamsWithCount<T>,
): ConcurrentQueriesParams<T> = unsafeCast(value)

fun <T : JsAny?> ConcurrentQueriesParams(
    value: ConcurrentQueriesParamsWithPromises<T>,
): ConcurrentQueriesParams<T> = unsafeCast(value)

/**
 * Extra options passed to every `fetch` call, such as proxy settings.
 *
 * Mirrors Node.js `http.RequestOptions` without `headers`, `responseType`, `url`, `method`
 * and `data`. Function-valued node options are kept as opaque [JsAny].
 */
external interface AxiosConfig {
    /** HTTP agent, or `false` to use the default agent. */
    var agent: JsAny?

    /** Basic authentication credentials, `user:password`. */
    var auth: String?

    /** IP address family. */
    var family: StringOrDouble?

    /** Host name or IP address. */
    var host: String?

    /** Host name or IP address, preferred over [host]. */
    var hostname: String?

    /** Accept invalid HTTP headers. */
    var insecureHTTPParser: Boolean?

    /** Join duplicate headers. */
    var joinDuplicateHeaders: Boolean?

    /** Local interface to bind for network connections. */
    var localAddress: String?

    /** Local port to bind for network connections. */
    var localPort: Double?

    /** Maximum length of response headers. */
    var maxHeaderSize: Double?

    /** Request path. */
    var path: String?

    /** Remote port. */
    var port: StringOrDouble?

    /** Request protocol. */
    var protocol: String?

    /** Send the `Connection: keep-alive` header. */
    var setDefaultHeaders: Boolean?

    /** Set the `Host` header. */
    var setHost: Boolean?

    /** Unix domain socket path. */
    var socketPath: String?

    /** Socket timeout in milliseconds. */
    var timeout: Double?

    /** Abort signal. */
    var signal: JsAny?

    /** Custom DNS lookup function. */
    var lookup: JsAny?

    /** Custom socket factory. */
    var createConnection: JsAny?
}

/**
 * Logging callbacks called around API queries.
 */
external interface AxiosCallbacks {
    /** Called before a query starts. */
    var onStart: ((url: String, params: APIQueryParams) -> Unit)?

    /** Called when a query succeeds. */
    var onSuccess: ((url: String, params: APIQueryParams) -> Unit)?

    /** Called when a query fails. */
    var onError: ((url: String, params: APIQueryParams, errorCode: Double?) -> Unit)?
}
