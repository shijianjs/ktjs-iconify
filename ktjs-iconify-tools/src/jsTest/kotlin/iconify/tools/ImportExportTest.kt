package iconify.tools

import iconify.IconifyJSON
import iconify.utils.IconifyIconCustomisations
import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImportExportTest {
    private fun iconSet(source: String): IconSet =
        IconSet(jsObject(source).unsafeCast<IconifyJSON>())

    private fun options(source: String): ExportToDirectoryOptions =
        jsObject(source).unsafeCast<ExportToDirectoryOptions>()

    private fun iconPackageOptions(source: String): ExportIconPackageOptions =
        jsObject(source).unsafeCast<ExportIconPackageOptions>()

    private fun jsonPackageOptions(source: String): ExportJSONPackageOptions =
        jsObject(source).unsafeCast<ExportJSONPackageOptions>()

    private fun stringifyWithTabs(value: dynamic): String =
        js("(data) => JSON.stringify(data, null, String.fromCharCode(9))")(value)

    private fun parseJson(source: String): dynamic = jsObject(source)

    private val twoIcons = """
        {
            prefix: 'foo',
            icons: {
                maximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                minimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'}
            },
            width: 24,
            height: 24
        }
    """.trimIndent()

    @Test
    fun exportsIconsToDirectory(): Promise<Unit> {
        val dir = testPath("export-dir")
        val set = iconSet(twoIcons)

        return exportToDirectory(set, options("{target: '$dir', cleanup: true}")).then { files ->
            assertEquals(2, files.size)
            assertEquals(listOf("maximize.svg", "minimize.svg"), nodeFs.readdirSync(dir).sorted())
            assertEquals(
                set.toHtml("maximize", jsObject("{width: 'auto', height: 'auto'}").unsafeCast<IconifyIconCustomisations>()),
                readTextFile("$dir/maximize.svg")
            )
            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun exportsAliasesAndCharacters(): Promise<Unit> {
        val dir = testPath("export-dir-aliases")
        val set = iconSet(
            """
            {
                prefix: 'foo',
                icons: {
                    'chrome-maximize': {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    'chrome-minimize': {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'}
                },
                aliases: {
                    maximize: {parent: 'chrome-maximize'},
                    minimize: {parent: 'chrome-minimize'},
                    whatever: {parent: 'missing'}
                },
                width: 24,
                height: 24,
                chars: {
                    f00: 'chrome-maximize',
                    f01: 'maximize'
                }
            }
            """.trimIndent()
        )

        return exportToDirectory(
            set,
            options("{target: '$dir', cleanup: true, includeChars: true}")
        ).then { files ->
            assertEquals(6, files.size)
            assertEquals(
                listOf(
                    "chrome-maximize.svg",
                    "chrome-minimize.svg",
                    "f00.svg",
                    "f01.svg",
                    "maximize.svg",
                    "minimize.svg",
                ),
                nodeFs.readdirSync(dir).sorted()
            )

            // Alias and character files use the parent icon contents
            assertEquals(readTextFile("$dir/chrome-maximize.svg"), readTextFile("$dir/maximize.svg"))
            assertEquals(readTextFile("$dir/chrome-maximize.svg"), readTextFile("$dir/f00.svg"))
            assertEquals(readTextFile("$dir/chrome-maximize.svg"), readTextFile("$dir/f01.svg"))

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun exportsIconPackage(): Promise<Unit> {
        val dir = testPath("export-icon-package")
        val set = iconSet(
            """
            {
                prefix: 'foo',
                icons: {
                    maximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    minimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'}
                },
                aliases: {
                    test: {parent: 'maximize'}
                },
                width: 24,
                height: 24
            }
            """.trimIndent()
        )

        return exportIconPackage(set, iconPackageOptions("{target: '$dir', cleanup: true}")).then { files ->
            assertEquals(
                listOf(
                    "maximize.d.ts",
                    "maximize.js",
                    "minimize.d.ts",
                    "minimize.js",
                    "package.json",
                    "test.d.ts",
                    "test.js",
                ),
                nodeFs.readdirSync(dir).sorted()
            )

            set.forEachSync({ name, _ ->
                val data = set.resolve(name)
                assertEquals(
                    "const data = ${stringifyWithTabs(data)};\nexport default data;\n",
                    readTextFile("$dir/$name.js")
                )
                null
            })

            val packageContent = parseJson(readTextFile("$dir/package.json"))
            assertEquals("*", packageContent.dependencies.`@iconify/types`)

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun exportsCommonJsIconPackage(): Promise<Unit> {
        val dir = testPath("export-icon-package-cjs")
        val set = iconSet(twoIcons)

        return exportIconPackage(
            set,
            iconPackageOptions("{target: '$dir', cleanup: true, module: false}")
        ).then { files ->
            assertEquals(
                listOf(
                    "maximize.d.ts",
                    "maximize.js",
                    "minimize.d.ts",
                    "minimize.js",
                    "package.json",
                ),
                nodeFs.readdirSync(dir).sorted()
            )

            set.forEachSync({ name, _ ->
                val data = set.resolve(name)
                assertEquals(
                    "const data = ${stringifyWithTabs(data)};\nexports.__esModule = true;\nexports.default = data;\n",
                    readTextFile("$dir/$name.js")
                )
                null
            })

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun exportsJsonPackage(): Promise<Unit> {
        val dir = testPath("export-json-package")
        val set = iconSet(
            """
            {
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    maximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    minimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'}
                },
                aliases: {
                    test: {parent: 'maximize'}
                },
                width: 24,
                height: 24
            }
            """.trimIndent()
        )

        return exportJSONPackage(
            set,
            jsonPackageOptions("{target: '$dir', cleanup: true}")
        ).then { files ->
            assertEquals(
                listOf(
                    "chars.json",
                    "icons.json",
                    "index.d.ts",
                    "index.js",
                    "index.mjs",
                    "info.json",
                    "metadata.json",
                    "package.json",
                ),
                nodeFs.readdirSync(dir).sorted()
            )

            assertJsDeepEquals(set.export(), parseJson(readTextFile("$dir/icons.json")))

            val packageContent = parseJson(readTextFile("$dir/package.json"))
            // Wildcard version is the default. Exact version is not tested because
            // upstream getTypesVersion() breaks on Windows file URLs.
            assertEquals("*", packageContent.dependencies.`@iconify/types`)

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun preparesDirectoriesForExport(): Promise<Unit> {
        val dir = testPath("prepare-export")
        resetDirectory(dir)
        writeTextFile("$dir/old.svg", "<svg/>")

        return prepareDirectoryForExport(
            jsObject("{target: '$dir/'}").unsafeCast<ExportTargetOptions>()
        ).then { normalized ->
            // Trailing slash is removed, existing files are kept without cleanup
            assertEquals(dir, normalized)
            assertTrue(fileExists("$dir/old.svg"))

            prepareDirectoryForExport(
                jsObject("{target: '$dir', cleanup: true}").unsafeCast<ExportTargetOptions>()
            )
        }.then { normalized ->
            assertEquals(dir, normalized)
            assertTrue(isDirectory(dir))
            assertTrue(!fileExists("$dir/old.svg"))

            removeDirectory(dir)
            Unit
        }
    }

    private val styleSetSvg = """
        <svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg">
          <style>
            rect { cursor: pointer }
            .round { rx: 5px; fill: green; }
          </style>

          <rect id="me" width="10" height="10">
            <set attributeName="class" to="round" begin="me.click" dur="2s" />
          </rect>
        </svg>
    """.trimIndent()

    private val styleCircleSvg = """
        <svg viewBox="0 0 10 10" xmlns="http://www.w3.org/2000/svg">
          <title>Circle</title>
          <style>
            circle {
              fill: gold;
              stroke: maroon;
              stroke-width: 2px;
            }
          </style>
          <circle cx="5" cy="5" r="4" />
        </svg>
    """.trimIndent()

    private val importedSetBody =
        """<style>.round{rx:5px;fill:green}</style><rect id="me" width="10" height="10"><set attributeName="class" to="round" begin="me.click" dur="2s"/></rect>"""

    private val importedStyleBody =
        """<circle cx="5" cy="5" r="4" fill="gold" stroke="maroon" stroke-width="2px"/>"""

    private fun writeStyleDirectory(): String {
        val dir = testPath("import-style")
        resetDirectory(dir)
        writeTextFile("$dir/set.svg", styleSetSvg)
        writeTextFile("$dir/style.svg", styleCircleSvg)
        return dir
    }

    private fun assertStyleIconSet(set: IconSet, prefix: String, includeTitle: Boolean) {
        assertEquals(listOf("set", "style").sorted(), set.list().sorted())
        assertTrue(set.lastModified > 0)

        val exported = set.export()
        assertEquals(prefix, exported.prefix)
        assertEquals(10, exported.width as Int)
        assertEquals(10, exported.height as Int)
        assertEquals(importedSetBody, exported.icons["set"]?.body)
        assertEquals(
            if (includeTitle) "<title>Circle</title>$importedStyleBody" else importedStyleBody,
            exported.icons["style"]?.body
        )
    }

    @Test
    fun importsDirectory(): Promise<Unit> {
        val dir = writeStyleDirectory()

        return importDirectory(dir).then { set ->
            assertStyleIconSet(set, "", false)

            // Synchronous version produces the same result
            val syncSet = importDirectorySync(dir)
            val exported = set.export()
            val syncExported = syncSet.export()
            syncExported.lastModified = exported.lastModified
            assertJsDeepEquals(exported, syncExported)

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun importsWithKeywordCallback(): Promise<Unit> {
        val dir = writeStyleDirectory()

        return importDirectory(
            dir,
            jsObject(
                """{
                    prefix: 'foo',
                    keepTitles: true,
                    keyword: (item) => Promise.resolve('test-' + item.file)
                }"""
            ).unsafeCast<ImportDirectoryOptions>()
        ).then { set ->
            assertEquals(listOf("test-set", "test-style").sorted(), set.list().sorted())
            val exported = set.export()
            assertEquals("foo", exported.prefix)
            assertEquals(importedSetBody, exported.icons["test-set"]?.body)
            assertEquals(
                "<title>Circle</title>$importedStyleBody",
                exported.icons["test-style"]?.body
            )

            // Synchronous callback
            val syncSet = importDirectorySync(
                dir,
                jsObject(
                    """{
                        prefix: 'foo',
                        keepTitles: true,
                        keyword: (item) => 'test-' + item.file
                    }"""
                ).unsafeCast<ImportDirectoryOptions>()
            )
            val syncExported = syncSet.export()
            syncExported.lastModified = exported.lastModified
            assertJsDeepEquals(exported, syncExported)

            removeDirectory(dir)
            Unit
        }
    }

    @Test
    fun importsSubdirectories() {
        val dir = testPath("import-subdirs")
        resetDirectory(dir)
        nodeFs.mkdirSync("$dir/outline", jsObject("{recursive: true}"))
        nodeFs.mkdirSync("$dir/regular", jsObject("{recursive: true}"))
        val icon = """<svg viewBox="0 0 24 24"><rect width="24" height="24"/></svg>"""
        writeTextFile("$dir/outline/apps.svg", icon)
        writeTextFile("$dir/outline/caret-back.svg", icon)
        writeTextFile("$dir/regular/apps.svg", icon)
        writeTextFile("$dir/regular/camera.svg", icon)

        assertEquals(
            listOf("apps", "caret-back", "camera").sorted(),
            importDirectorySync(dir).list().sorted()
        )

        assertEquals(
            emptyList(),
            importDirectorySync(
                dir,
                jsObject("{includeSubDirs: false}").unsafeCast<ImportDirectoryOptions>()
            ).list().toList()
        )

        val customKeyword = jsObject("{}").unsafeCast<ImportDirectoryOptions>()
        customKeyword.keyword = { item: dynamic, _: dynamic, _: dynamic ->
            cleanupIconKeyword(item.subdir + item.file)
        }
        assertEquals(
            listOf("outline-apps", "outline-caret-back", "regular-apps", "regular-camera"),
            importDirectorySync(dir, customKeyword).list().sorted()
        )

        removeDirectory(dir)
    }

    @Test
    fun handlesImportErrors(): Promise<Unit> {
        val dir = testPath("import-errors")
        resetDirectory(dir)
        writeTextFile("$dir/good.svg", """<svg viewBox="0 0 24 24"><rect width="24" height="24"/></svg>""")
        writeTextFile("$dir/broken.svg", "not an svg")

        // Default: both synchronous and asynchronous versions throw
        val syncError = errorMessage { importDirectorySync(dir) }
        assertNotNull(syncError)
        assertTrue(syncError.contains("Failed to import"))

        return importDirectory(dir).then(
            { error("Expected import to fail") },
            { err ->
                assertTrue(jsError(err).contains("Failed to import"))

                // Ignore errors
                val syncSet = importDirectorySync(
                    dir,
                    jsObject("{ignoreImportErrors: true}").unsafeCast<ImportDirectoryOptions>()
                )
                assertNull(syncSet.entries.`broken`)
                assertNotNull(syncSet.entries.good)

                importDirectory(
                    dir,
                    jsObject("{ignoreImportErrors: true}").unsafeCast<ImportDirectoryOptions>()
                )
            }
        ).then { set ->
            assertNull(set.entries.`broken`)
            assertNotNull(set.entries.good)

            // "warn" logs a warning instead of throwing
            val warnings = captureWarnings {
                importDirectorySync(
                    dir,
                    jsObject("{ignoreImportErrors: 'warn'}").unsafeCast<ImportDirectoryOptions>()
                )
            }
            assertEquals(1, warnings.size)
            assertTrue(warnings[0].contains("Failed to import"))

            removeDirectory(dir)
            Unit
        }
    }
}
