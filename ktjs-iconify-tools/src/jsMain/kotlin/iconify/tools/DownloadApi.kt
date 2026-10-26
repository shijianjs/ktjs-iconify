@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.JsAny
import kotlin.js.Promise

/**
 * Download a package from git, GitHub, GitLab or npm, depending on `downloadType`.
 *
 * Returns `"not_modified"` when the requested version or commit matches
 * `ifModifiedSince`.
 */
external fun downloadPackage(options: DownloadParams): Promise<DownloadResult>

/**
 * Download an npm package. Returns `"not_modified"` when `ifModifiedSince` matches.
 *
 * `ifModifiedSince` can be a version string, `true` to read the version from
 * `target/package.json`, or a previous result object. The package is unpacked into
 * `target/package`; `target` also holds the cached `.tgz` archive. Returns
 * `{ downloadType: "npm", rootDir, contentsDir, version }`.
 */
external fun downloadNPMPackage(
    options: DownloadNPMPackageOptions,
): Promise<DownloadNPMPackageResultOrNotModified>

/**
 * Get the version of an npm package from the registry.
 *
 * Uses the npm tag from [NPMPackageOptions.tag] (default `latest`). When
 * [NPMPackageOptions.fetch] is `true`, fetches `registry.npmjs.org` directly instead of
 * running `npm view`. The result also contains the archive URL in `file`.
 */
external fun getNPMVersion(options: NPMPackageOptions): Promise<GetNPMVersionResult>

/**
 * Get the version of a package from its `package.json`, where `target` is the package
 * directory.
 */
external fun getPackageVersion(target: String): Promise<String>

/**
 * Download a GitHub repository using the API.
 *
 * Resolves the latest commit hash of the branch first and returns `"not_modified"` when
 * it matches `ifModifiedSince`. `target` supports `{hash}`. The `.zip` archive is cached
 * in `target`. Returns `{ downloadType: "github", rootDir, contentsDir, hash }`.
 *
 * Old directories are removed by default; set
 * [DownloadGitHubRepoOptions.cleanupOldDirectories] to `false` to keep them.
 * [DownloadGitHubRepoOptions.cleanupOldFiles] defaults to `false`.
 */
external fun downloadGitHubRepo(
    options: DownloadGitHubRepoOptions,
): Promise<DownloadGitHubRepoResultOrNotModified>

/**
 * Get the latest commit SHA of a GitHub branch using the API.
 */
external fun getGitHubRepoHash(options: GitHubAPIOptions): Promise<String>

/**
 * Download a GitLab repository using the API.
 *
 * Works like [downloadGitHubRepo], using [GitLabAPIOptions.uri] (default
 * `https://gitlab.com/api/v4/projects`). Returns
 * `{ downloadType: "gitlab", rootDir, contentsDir, hash }`.
 */
external fun downloadGitLabRepo(
    options: DownloadGitLabRepoOptions,
): Promise<DownloadGitLabRepoResultOrNotModified>

/**
 * Get the latest commit SHA of a GitLab branch using the API.
 */
external fun getGitLabRepoHash(options: GitLabAPIOptions): Promise<String>

/**
 * Clone or update a git repository.
 *
 * Clones with `--depth 1` for the specified branch. `target` supports `{hash}`; stale
 * contents are reset when the repository was not modified. Returns
 * `{ downloadType: "git", contentsDir, hash }`.
 *
 * `ifModifiedSince` can be a hash, `true` to read the hash from the existing clone, or
 * a previous result object. The target directory is not checked for the correct remote
 * or branch, so do not reuse it for different repositories or branches.
 */
external fun downloadGitRepo(
    options: DownloadGitRepoOptions,
): Promise<DownloadGitRepoResultOrNotModified>

/**
 * Get the current branch of a cloned repository, optionally checking out [checkout]
 * first.
 */
external fun getGitRepoBranch(
    options: ExportTargetOptions,
    checkout: String = definedExternally,
): Promise<String>

/**
 * Get the current commit hash (`git rev-parse HEAD`) of a cloned repository.
 */
external fun getGitRepoHash(options: ExportTargetOptions): Promise<String>

/**
 * Reset the contents of a cloned repository, discarding local changes.
 */
external fun resetGitRepoContents(target: String): Promise<Unit>

/**
 * Unpack a `.zip` archive into `path`.
 *
 * [filter] receives the normalized target path of each entry and returns `true` to
 * extract it. Hidden macOS `._*` files are skipped, and entries with absolute paths or
 * path traversal are rejected.
 */
external fun unzip(source: String, path: String, filter: ((String) -> Boolean)? = definedExternally): Promise<Unit>

/**
 * Unpack a `.tgz` archive.
 */
external fun untar(file: String, path: String): Promise<Unit>

/**
 * Import an icon set from Figma.
 *
 * Fetches the document, extracts icon nodes, downloads SVGs, cleans them up with
 * [cleanupSVG] and builds an [IconSet]. Returns `"not_modified"` when the document
 * version matches `ifModifiedSince`.
 *
 * The result contains the document name, version and modification time, node and icon
 * counters, the generated [IconSet] and a list of icons that could not be imported.
 * `cacheDir` enables caching for API responses and SVG files.
 */
external fun importFromFigma(options: FigmaImportOptions): Promise<FigmaImportResultOrNotModified>

/**
 * Send an API query and return the response body as text.
 *
 * Returns `{ success: true, content }` for HTTP 200, otherwise
 * `{ success: false, error, response? }`. With [cache], responses are stored in
 * `cache.dir` for `cache.ttl` seconds and reused while valid.
 */
external fun sendAPIQuery(
    query: APIQueryParams,
    cache: APICacheOptions? = definedExternally,
): Promise<APIQueryResult>

/**
 * Download a file to `target`. Throws when the response status is not 200.
 */
external fun downloadFile(query: APIQueryParams, target: String): Promise<Unit>

/**
 * Replace the `fetch` implementation used for API queries and file downloads.
 */
external fun setFetch(fetchFunction: FetchFunction)

/**
 * Default concurrency limit (`limit: 5`) and retry count (`retries: 3`) for
 * [runConcurrentQueries].
 */
external val defaultQueueParams: ConcurrentQueriesCommonParams<JsAny?>

/**
 * Run asynchronous operations with concurrency and retry limits.
 *
 * Pass either `{ total, callback }` or `{ promises }`, plus optional `limit`, `retries`,
 * `onStart`, `onSuccess` and `onError`. Results are returned in input order. Failed
 * operations are retried; after the last attempt the promise is rejected unless
 * `onError` is provided, in which case its error handling decides whether to abort.
 */
external fun <T : JsAny?> runConcurrentQueries(
    params: ConcurrentQueriesParams<T>,
): Promise<Array<T>>

/**
 * Extra options passed to every `fetch` call, such as proxy settings.
 */
external val axiosConfig: AxiosConfig

/**
 * Logging callbacks called around API queries: `onStart`, `onSuccess` and `onError`.
 * By default `onStart` logs the URL.
 */
external val fetchCallbacks: AxiosCallbacks
