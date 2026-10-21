package iconify.tools

import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DownloadTest {
    private fun apiQuery(source: String): APIQueryParams =
        jsObject(source).unsafeCast<APIQueryParams>()

    private fun exportTarget(target: String): ExportTargetOptions =
        jsObjectWith("target" to target).unsafeCast<ExportTargetOptions>()

    private fun createFetch(requests: dynamic): dynamic =
        js(
            """(requests) => async (url, options) => {
                requests.push({ url, headers: options && options.headers });
                if (url.indexOf('/missing') !== -1) {
                    return {
                        status: 404,
                        text: async () => '',
                        arrayBuffer: async () => new ArrayBuffer(0),
                    };
                }
                return {
                    status: 200,
                    text: async () => 'response:' + url,
                    arrayBuffer: async () => new TextEncoder().encode('file:' + url).buffer,
                };
            }"""
        )(requests)

    private fun withFetch(requests: dynamic, block: () -> dynamic): Promise<Unit> {
        val originalFetch: dynamic = js("globalThis.fetch")
        setFetch(createFetch(requests))
        return (block() as Promise<Unit>).then(
            { value: Unit ->
                setFetch(originalFetch)
                value
            },
            { err: Throwable ->
                setFetch(originalFetch)
                throw err
            }
        )
    }

    private fun requestInfo(requests: dynamic, index: Int): dynamic =
        js("(requests, index) => requests[index]")(requests, index)

    @Test
    fun readsPackageVersion(): Promise<Unit> {
        val dir = testPath("package-version")
        resetDirectory(dir)
        writeTextFile("$dir/package.json", """{"name":"test","version":"1.2.3"}""")

        return getPackageVersion(dir).then { version ->
            assertEquals("1.2.3", version)
            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun sendsApiQueries(): Promise<Unit> {
        val requests: dynamic = js("[]")

        return withFetch(requests) {
            sendAPIQuery(
                apiQuery(
                    """{
                        uri: 'https://example.com/test',
                        params: new URLSearchParams({a: '1'}),
                        headers: {Authorization: 'token'}
                    }"""
                )
            ).then { response: dynamic ->
                assertEquals(true, response.success as Boolean)
                assertEquals("response:https://example.com/test?a=1", response.content as String)
                assertEquals(1, requests.length as Int)
                assertEquals("https://example.com/test?a=1", requestInfo(requests, 0).url as String)
                assertEquals(
                    "token",
                    requestInfo(requests, 0).headers.Authorization as String
                )
                Unit
            }
        }
    }

    @Test
    fun failsApiQueries(): Promise<Unit> {
        val requests: dynamic = js("[]")

        return withFetch(requests) {
            sendAPIQuery(apiQuery("{uri: 'https://example.com/missing'}")).then { response: dynamic ->
                assertEquals(false, response.success as Boolean)
                assertEquals(404, response.error as Int)
                Unit
            }
        }
    }

    @Test
    fun cachesApiResponses(): Promise<Unit> {
        val requests: dynamic = js("[]")
        val cacheDir = testPath("api-cache")
        resetDirectory(cacheDir)
        val query = apiQuery("{uri: 'https://example.com/cached'}")
        val cache = jsObject("{dir: '$cacheDir', ttl: 3600}").unsafeCast<APICacheOptions>()

        return withFetch(requests) {
            sendAPIQuery(query, cache).then { first: dynamic ->
                assertEquals("response:https://example.com/cached", first.content as String)
                assertEquals(1, requests.length as Int)
                sendAPIQuery(query, cache)
            }.then { second: dynamic ->
                assertEquals("response:https://example.com/cached", second.content as String)
                // Served from cache, no extra request
                assertEquals(1, requests.length as Int)

                val files = nodeFs.readdirSync(cacheDir)
                assertEquals(1, files.size)
                assertTrue(files[0].endsWith(".json"))

                removeDirectory(cacheDir)
                Unit
            }
        }
    }

    @Test
    fun downloadsFiles(): Promise<Unit> {
        val requests: dynamic = js("[]")
        val dir = testPath("download-file")
        resetDirectory(dir)
        val target = "$dir/file.txt"

        return withFetch(requests) {
            downloadFile(apiQuery("{uri: 'https://example.com/file.txt'}"), target).then {
                assertEquals("file:https://example.com/file.txt", readTextFile(target))
                removeDirectory(dir)
                Unit
            }
        }
    }

    @Test
    fun failsFileDownloads(): Promise<Unit> {
        val requests: dynamic = js("[]")
        val target = testPath("download-file-fail/file.txt")

        return withFetch(requests) {
            downloadFile(apiQuery("{uri: 'https://example.com/missing'}"), target).then(
                { error("Expected download to fail"); Unit },
                { err ->
                    assertTrue(jsError(err).contains("Error downloading"))
                    Unit
                }
            )
        }
    }

    @Test
    fun rejectsInvalidDownloadType() {
        val message = errorMessage {
            downloadPackage(jsObject("{downloadType: 'invalid'}"))
        }
        assertEquals("Invalid download type: invalid", message)
    }

    private fun git(cwd: String, command: String): Promise<ExecResult> =
        execAsync("git $command", jsObjectWith("cwd" to cwd))

    private fun gitDownloadOptions(
        remote: String,
        target: String,
        ifModifiedSince: Any? = null,
    ): DownloadGitRepoOptions {
        val options = jsObjectWith(
            "remote" to remote,
            "branch" to "main",
            "target" to target,
        )
        if (ifModifiedSince != null) {
            js("(options, value) => { options.ifModifiedSince = value; }")(options, ifModifiedSince)
        }
        return options.unsafeCast<DownloadGitRepoOptions>()
    }

    @Test
    fun downloadsLocalGitRepository(): Promise<Unit> {
        // Git on Windows does not accept relative paths as remotes
        val remote = nodePath.resolve(testPath("git-remote"))
        val target = nodePath.resolve(testPath("git-download"))
        resetDirectory(remote)
        removeDirectory(target)
        writeTextFile("$remote/package.json", """{"name":"test","version":"1.0.0"}""")
        writeTextFile("$remote/README.md", "test repository")

        var remoteHash = ""

        return git(remote, "init -b main").then {
            git(remote, "add .")
        }.then {
            git(remote, "-c user.name=Test -c user.email=test@example.com commit -m initial")
        }.then {
            git(remote, "rev-parse HEAD")
        }.then { result ->
            remoteHash = result.stdout.trim()

            downloadGitRepo(gitDownloadOptions(remote, target, ifModifiedSince = true))
        }.then { download ->
            assertEquals("git", download.downloadType as String)
            assertEquals(normalizePath(target), download.contentsDir as String)
            assertEquals(remoteHash, download.hash as String)
            assertTrue(fileExists("$target/package.json"))

            // Checking the previous result reports no changes
            downloadGitRepo(gitDownloadOptions(remote, target, ifModifiedSince = download))
        }.then { result ->
            assertEquals("not_modified", result as String)
            getGitRepoHash(exportTarget(target))
        }.then { hash ->
            assertEquals(remoteHash, hash)
            getGitRepoBranch(exportTarget(target), "main")
        }.then { branch ->
            assertEquals("main", branch)
            removeDirectory(remote)
            removeDirectory(target)
            Unit
        }
    }

    @Test
    fun resetsGitRepoContents(): Promise<Unit> {
        val remote = nodePath.resolve(testPath("git-reset-remote"))
        val target = nodePath.resolve(testPath("git-reset-download"))
        resetDirectory(remote)
        removeDirectory(target)
        writeTextFile("$remote/package.json", """{"name":"test","version":"2.0.0"}""")

        return git(remote, "init -b main").then {
            git(remote, "add .")
        }.then {
            git(remote, "-c user.name=Test -c user.email=test@example.com commit -m initial")
        }.then {
            downloadGitRepo(gitDownloadOptions(remote, target))
        }.then {
            assertEquals("2.0.0", readPackageVersion(target))

            // Modify tracked file and add untracked file
            writeTextFile("$target/package.json", """{"name":"test","version":"9.9.9"}""")
            writeTextFile("$target/extra.txt", "extra")

            getGitRepoBranch(exportTarget(target), "main").then { branch ->
                assertEquals("main", branch)
                resetGitRepoContents(target)
            }
        }.then {
            assertEquals("2.0.0", readPackageVersion(target))
            assertTrue(!fileExists("$target/extra.txt"))

            removeDirectory(remote)
            removeDirectory(target)
            Unit
        }
    }

    private fun readPackageVersion(dir: String): String {
        val content = jsObject(readTextFile("$dir/package.json"))
        return content.version as String
    }
}
