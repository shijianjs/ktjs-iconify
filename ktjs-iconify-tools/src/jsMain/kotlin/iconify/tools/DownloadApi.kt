@file:JsModule("@iconify/tools/lib/index.js")

package iconify.tools

import kotlin.js.Promise

/**
 * Download a package from git, GitHub, GitLab or npm, depending on `downloadType`.
 *
 * Returns `"not_modified"` when `ifModifiedSince` matches.
 */
external fun downloadPackage(options: dynamic): Promise<dynamic>

/**
 * Download an npm package. Returns `"not_modified"` when `ifModifiedSince` matches.
 */
external fun downloadNPMPackage(options: DownloadNPMPackageOptions): Promise<dynamic>

external fun getNPMVersion(options: NPMPackageOptions): Promise<GetNPMVersionResult>

external fun getPackageVersion(target: String): Promise<String>

/**
 * Download a GitHub repository using the API.
 */
external fun downloadGitHubRepo(options: DownloadGitHubRepoOptions): Promise<dynamic>

external fun getGitHubRepoHash(options: GitHubAPIOptions): Promise<String>

/**
 * Download a GitLab repository using the API.
 */
external fun downloadGitLabRepo(options: DownloadGitLabRepoOptions): Promise<dynamic>

external fun getGitLabRepoHash(options: GitLabAPIOptions): Promise<String>

/**
 * Clone or update a git repository.
 */
external fun downloadGitRepo(options: DownloadGitRepoOptions): Promise<dynamic>

external fun getGitRepoBranch(
    options: ExportTargetOptions,
    checkout: String = definedExternally,
): Promise<String>

external fun getGitRepoHash(options: ExportTargetOptions): Promise<String>

external fun resetGitRepoContents(target: String): Promise<Unit>

external fun unzip(source: String, path: String, filter: ((String) -> Boolean)? = definedExternally): Promise<Unit>

/**
 * Unpack a `.tgz` archive.
 */
external fun untar(file: String, path: String): Promise<Unit>

/**
 * Import an icon set from Figma.
 */
external fun importFromFigma(options: FigmaImportOptions): Promise<dynamic>

external fun sendAPIQuery(
    query: APIQueryParams,
    cache: APICacheOptions? = definedExternally,
): Promise<dynamic>

external fun downloadFile(query: APIQueryParams, target: String): Promise<Unit>

external fun setFetch(fetchFunction: dynamic)

external val defaultQueueParams: dynamic

/**
 * Run asynchronous operations with concurrency and retry limits.
 */
external fun runConcurrentQueries(params: dynamic): Promise<dynamic>

external val axiosConfig: dynamic

external val fetchCallbacks: dynamic
