package iconify.tools

/**
 * Download source type: `"git"`, `"github"`, `"gitlab"` or `"npm"`.
 */
external interface DownloadSourceMixin {
    /**
     * Download source type.
     */
    var downloadType: String
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
 * Options for [downloadNPMPackage].
 */
external interface DownloadNPMPackageOptions : NPMPackageOptions, ExportTargetOptions {
    /**
     * Download only if the version changed: a version string, `true` to check the version stored in
     * the target directory, or the previous download result.
     */
    var ifModifiedSince: dynamic

    /**
     * Log commands.
     */
    var log: Boolean?
}

/**
 * Result of [downloadNPMPackage].
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
 * Options for [downloadGitHubRepo].
 */
external interface DownloadGitHubRepoOptions : ExportTargetOptions, GitHubAPIOptions {
    /**
     * Download only if the commit hash changed: a hash string, or the previous download result.
     */
    var ifModifiedSince: dynamic

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
 * Result of [downloadGitHubRepo].
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
 * Options for [downloadGitLabRepo].
 */
external interface DownloadGitLabRepoOptions : ExportTargetOptions, GitLabAPIOptions {
    /**
     * Download only if the commit hash changed: a hash string, or the previous download result.
     */
    var ifModifiedSince: dynamic

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
 * Result of [downloadGitLabRepo].
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
    var ifModifiedSince: dynamic

    /**
     * Log commands.
     */
    var log: Boolean?
}

/**
 * Result of [downloadGitRepo].
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
 * API query parameters.
 */
external interface APIQueryParams {
    /**
     * Request URI, without query parameters.
     */
    var uri: String

    /**
     * Query parameters, as a `URLSearchParams` object.
     */
    var params: dynamic

    /**
     * Request headers.
     */
    var headers: dynamic
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
 * Options for [importFromFigma].
 */
external interface FigmaImportOptions {
    /**
     * Figma API token. Required.
     */
    var token: String

    /**
     * Figma document ID.
     */
    var file: String

    /**
     * Icon set prefix.
     */
    var prefix: String

    /**
     * Document version.
     */
    var version: String?

    /**
     * IDs of nodes to check.
     */
    var ids: Array<String>?

    /**
     * Node depth to retrieve.
     *
     * Set it to avoid retrieving too much data: `2` for Page → Icon, `3` for Page → Group or Frame
     * → Icon.
     */
    var depth: Double?

    /**
     * Page names or IDs, an alternative to [filterParentNode].
     */
    var pages: Array<String>?

    /**
     * Check whether the document changed since this value: a timestamp string, a `Date`, or `true`
     * to compare with the last cached document.
     */
    var ifModifiedSince: dynamic

    /**
     * Cache directory.
     */
    var cacheDir: String?

    /**
     * Cache TTL for API queries in seconds.
     *
     * Defaults to three days.
     */
    var cacheAPITTL: Double?

    /**
     * Cache TTL for downloaded SVG files in seconds.
     *
     * Defaults to 30 days.
     */
    var cacheSVGTTL: Double?

    /**
     * Options for exporting SVG from Figma: `includeID`, `simplifyStroke` and `useAbsoluteBounds`.
     */
    var svgOptions: dynamic

    /**
     * Callback to filter parent nodes, to skip irrelevant parts of the tree.
     */
    var filterParentNode: dynamic

    /**
     * Callback that returns the icon name for a node, or `undefined` to skip the node.
     */
    var iconNameForNode: dynamic

    /**
     * Callback invoked before importing each icon.
     */
    var beforeImportingIcon: dynamic

    /**
     * Callback invoked after importing each icon.
     */
    var afterImportingIcon: dynamic
}

/**
 * Icon node from a Figma document.
 */
external interface FigmaIconNode {
    /**
     * Node ID.
     */
    var id: String

    /**
     * Node name.
     */
    var name: String

    /**
     * Icon keyword.
     */
    var keyword: String

    /**
     * URL to download the icon from.
     */
    var url: String?

    /**
     * Downloaded SVG content.
     */
    var content: String?
}

/**
 * Result of [importFromFigma].
 */
external interface FigmaImportResult {
    /**
     * Document name.
     */
    var name: String

    /**
     * Document version.
     */
    var version: String

    /**
     * Last modification time of the document.
     */
    var lastModified: String

    /**
     * Imported icon set.
     */
    var iconSet: IconSet

    /**
     * Nodes that could not be imported.
     */
    var missing: Array<FigmaIconNode>

    /**
     * Number of nodes marked as icons.
     */
    var nodesCount: Double

    /**
     * Number of generated icons.
     */
    var generatedIconsCount: Double

    /**
     * Number of downloaded icons.
     */
    var downloadedIconsCount: Double
}
