package iconify.tools

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MiscTest {
    @Test
    fun cleansUpKeywords() {
        assertEquals("foo", cleanupIconKeyword("foo"))
        assertEquals("1f1e7-1f1fe", cleanupIconKeyword("1f1e7-1f1fe"))
        assertEquals("some-icon-svg", cleanupIconKeyword("some-icon.svg"))
        assertEquals("u1f30a", cleanupIconKeyword("u1F30A"))
        assertEquals("icon", cleanupIconKeyword("_icon_#"))

        assertEquals("some-icon", cleanupIconKeyword("someIcon", true))
        assertEquals("e1-f30-a", cleanupIconKeyword("E1F30A", true))

        assertEquals("", cleanupIconKeyword("`", true))
        assertEquals("", cleanupIconKeyword("#", true))
    }

    @Test
    fun bumpsVersions() {
        assertEquals("1.0.1", bumpVersion("1.0.0"))
        assertEquals("1.2.3.5", bumpVersion("1.2.3.4"))
        assertEquals("1.0.0-beta.2", bumpVersion("1.0.0-beta.1"))
        assertEquals("2.0.0-dev.1", bumpVersion("2.0.0-dev"))
    }

    @Test
    fun exposesQueueDefaults() {
        assertEquals(5.0, defaultQueueParams.limit)
        assertEquals(3.0, defaultQueueParams.retries)
    }

    @Test
    fun runsConcurrentQueries(): Promise<Unit> {
        val runQueries: dynamic = js(
            """(runConcurrentQueries) => {
                const delays = [2, 100, 50, 1];
                const callbacks = new Set();
                const resolved = [];
                return runConcurrentQueries({
                    total: delays.length,
                    limit: 3,
                    callback: (index) => {
                        if (callbacks.has(index)) throw new Error('Callback called twice');
                        if (resolved.includes(index)) throw new Error('Already resolved');
                        callbacks.add(index);
                        if (index < 3 && resolved.length !== 0) {
                            throw new Error('Resolved too early: ' + resolved.join(','));
                        }
                        if (index === 3 && JSON.stringify(resolved) !== '[0]') {
                            throw new Error('Unexpected resolved items: ' + resolved.join(','));
                        }
                        return new Promise((resolve) => {
                            setTimeout(() => {
                                resolved.push(index);
                                resolve(delays[index]);
                            }, delays[index]);
                        });
                    },
                }).then((result) => JSON.stringify({ result: Array.from(result), order: resolved }));
            }"""
        )

        val promise = runQueries(
            { params: ConcurrentQueriesParams<JsAny> -> runConcurrentQueries(params) }
        ) as Promise<String>
        return promise.then { value ->
            assertEquals("""{"result":[2,100,50,1],"order":[0,3,2,1]}""", value)
            Unit
        }
    }

    @Test
    fun listsDirectories() {
        val dir = testPath("scan")
        resetDirectory(dir)
        nodeFs.mkdirSync("$dir/sub", jsObject("{recursive: true}"))
        writeTextFile("$dir/a.svg", "<svg/>")
        writeTextFile("$dir/b.txt", "text")
        writeTextFile("$dir/sub/c.svg", "<svg/>")
        writeTextFile("$dir/.hidden.svg", "<svg/>")

        val all = scanDirectorySync(dir)
        assertEquals(listOf("a.svg", "b.txt", "sub/c.svg"), all.sorted())

        val withoutSubdirs = scanDirectorySync(dir, null, false)
        assertEquals(listOf("a.svg", "b.txt"), withoutSubdirs.sorted())

        val onlySvg = scanDirectorySync(dir, callback = { ext, file, subdir, _, _ ->
            if (ext == ".svg") ScanDirectoryCallbackResult(subdir + file + ext) else null
        })
        assertEquals(listOf("a.svg", "sub/c.svg"), onlySvg.sorted())

        removeDirectory(dir)
    }

    @Test
    fun listsDirectoriesAsync(): Promise<Unit> {
        val dir = testPath("scan-async")
        resetDirectory(dir)
        writeTextFile("$dir/a.svg", "<svg/>")
        writeTextFile("$dir/b.svg", "<svg/>")

        return scanDirectory(dir).then { files ->
            assertEquals(listOf("a.svg", "b.svg"), files.sorted())
            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun writesJsonFiles(): Promise<Unit> {
        val dir = testPath("json")
        resetDirectory(dir)
        val file = "$dir/data.json"

        return writeJSONFile(file, jsObject("{a: 1, b: [true, null]}")).then {
            assertEquals("{\n\t\"a\": 1,\n\t\"b\": [\n\t\ttrue,\n\t\tnull\n\t]\n}", readTextFile(file).trimEnd())
            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun comparesDirectories(): Promise<Unit> {
        val root = testPath("compare")
        val original = "$root/original"
        val copy = "$root/copy"
        val differentVersion = "$root/different-version"
        val extraFile = "$root/extra-file"
        val missingFile = "$root/missing-file"
        val formatted = "$root/formatted"

        listOf(original, copy, differentVersion, extraFile, missingFile, formatted).forEach(::resetDirectory)

        writeTextFile("$original/a.txt", "line1\nline2\n")
        writeTextFile("$original/package.json", "{\"version\":\"1.0.0\"}")
        writeTextFile("$copy/a.txt", "line1\nline2\n")
        writeTextFile("$copy/package.json", "{\"version\":\"1.0.0\"}")
        writeTextFile("$differentVersion/a.txt", "line1\nline2\n")
        writeTextFile("$differentVersion/package.json", "{\"version\":\"2.0.0\"}")
        writeTextFile("$extraFile/a.txt", "line1\nline2\n")
        writeTextFile("$extraFile/package.json", "{\"version\":\"1.0.0\"}")
        writeTextFile("$extraFile/extra.txt", "extra")
        writeTextFile("$missingFile/package.json", "{\"version\":\"1.0.0\"}")
        writeTextFile("$formatted/a.txt", "line1\r\nline2\r\n")
        writeTextFile("$formatted/package.json", "{\"version\":\"1.0.0\"}")

        return compareDirectories(original, copy).then { same ->
            assertTrue(same)
            compareDirectories(original, differentVersion)
        }.then { same ->
            assertTrue(same)
            compareDirectories(original, differentVersion, jsObject("{ignoreVersions: false}").unsafeCast<CompareDirectoriesOptions>())
        }.then { same ->
            assertTrue(!same)
            compareDirectories(original, extraFile)
        }.then { same ->
            assertTrue(!same)
            compareDirectories(original, missingFile)
        }.then { same ->
            assertTrue(!same)
            compareDirectories(original, formatted)
        }.then { same ->
            assertTrue(same)
            compareDirectories(original, formatted, jsObject("{ignoreNewLine: false}").unsafeCast<CompareDirectoriesOptions>())
        }.then { same ->
            assertTrue(!same)
            removeDirectory(root)
            Unit
        }
    }

    @Test
    fun runsShellCommands(): Promise<Unit> =
        execAsync("node -e \"console.log('tools-test')\"").then { result ->
            assertTrue(result.stdout.contains("tools-test"))
            assertEquals("", result.stderr.trim())
            Unit
        }

    @Test
    fun failsShellCommands(): Promise<Unit> {
        val promise = execAsync("node -e \"process.exit(1)\"")
        return promise.then(
            { error("Expected command to fail") },
            { err -> assertNotNull(err); Unit }
        )
    }

    @Test
    fun storesFetchCallbacks() {
        assertNotNull(fetchCallbacks.onStart)
        assertNotNull(axiosConfig)
    }

    @Test
    fun hasNoFetchOverrideByDefault() {
        // `setFetch` is covered in DownloadTest; make sure the accessor exists.
        assertNull(errorMessage { setFetch(js("undefined")) })
    }
}
