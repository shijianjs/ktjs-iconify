package iconify.tools

/** `"git"`, `"github"`, `"gitlab"` or `"npm"`. */
external interface DownloadSourceMixin {
    var downloadType: String
}

external interface NPMPackageOptions {
    var `package`: String
    var tag: String?
    var fetch: Boolean?
}

external interface GetNPMVersionResult {
    var version: String
    var file: String?
}

external interface DownloadNPMPackageOptions : NPMPackageOptions, ExportTargetOptions {
    /** `true`, a timestamp string, or a previous result. */
    var ifModifiedSince: dynamic
    var log: Boolean?
}

external interface DownloadNPMPackageResult : DownloadSourceMixin {
    var rootDir: String
    var contentsDir: String
    var version: String
}

external interface GitHubAPIOptions {
    var token: String
    var user: String
    var repo: String
    var branch: String
}

external interface DownloadGitHubRepoOptions : ExportTargetOptions, GitHubAPIOptions {
    var ifModifiedSince: dynamic
    var cleanupOldFiles: Boolean?
    var cleanupOldDirectories: Boolean?
    var log: Boolean?
    var filter: ((String) -> Boolean)?
}

external interface DownloadGitHubRepoResult : DownloadSourceMixin {
    var rootDir: String
    var contentsDir: String
    var hash: String
}

external interface GitLabAPIOptions {
    var uri: String?
    var token: String
    var project: String
    var branch: String
}

external interface DownloadGitLabRepoOptions : ExportTargetOptions, GitLabAPIOptions {
    var ifModifiedSince: dynamic
    var cleanupOldFiles: Boolean?
    var cleanupOldDirectories: Boolean?
    var log: Boolean?
    var filter: ((String) -> Boolean)?
}

external interface DownloadGitLabRepoResult : DownloadSourceMixin {
    var rootDir: String
    var contentsDir: String
    var hash: String
}

external interface DownloadGitRepoOptions : ExportTargetOptions {
    var remote: String
    var branch: String
    var ifModifiedSince: dynamic
    var log: Boolean?
}

external interface DownloadGitRepoResult : DownloadSourceMixin {
    var contentsDir: String
    var hash: String
}

external interface APIQueryParams {
    var uri: String
    var params: dynamic
    var headers: dynamic
}

external interface APICacheOptions {
    var dir: String
    var ttl: Double
}

external interface FigmaImportOptions {
    var token: String
    var file: String
    var prefix: String
    var version: String?
    var ids: Array<String>?
    var depth: Double?
    var pages: Array<String>?
    var ifModifiedSince: dynamic
    var cacheDir: String?
    var cacheAPITTL: Double?
    var cacheSVGTTL: Double?
    var svgOptions: dynamic
    var filterParentNode: dynamic
    var iconNameForNode: dynamic
    var beforeImportingIcon: dynamic
    var afterImportingIcon: dynamic
}

external interface FigmaIconNode {
    var id: String
    var name: String
    var keyword: String
    var url: String?
    var content: String?
}

external interface FigmaImportResult {
    var name: String
    var version: String
    var lastModified: String
    var iconSet: IconSet
    var missing: Array<FigmaIconNode>
    var nodesCount: Double
    var generatedIconsCount: Double
    var downloadedIconsCount: Double
}
