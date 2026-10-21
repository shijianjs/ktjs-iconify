package iconify.tools

private const val TEST_TMP = "build/tmp/ktjs-tools-test"

internal fun testPath(vararg parts: String): String = (listOf(TEST_TMP) + parts).joinToString("/")

internal fun resetDirectory(path: String) {
    nodeFs.rmSync(path, jsObject("{recursive: true, force: true}"))
    nodeFs.mkdirSync(path, jsObject("{recursive: true}"))
}

internal fun removeDirectory(path: String) {
    nodeFs.rmSync(path, jsObject("{recursive: true, force: true}"))
}

internal fun writeTextFile(path: String, content: String) {
    nodeFs.writeFileSync(path, content)
}

internal fun readTextFile(path: String): String = nodeFs.readFileSync(path, "utf8")

internal fun fileExists(path: String): Boolean = nodeFs.existsSync(path)

internal fun isDirectory(path: String): Boolean =
    js("(value) => value.isDirectory()")(nodeFs.statSync(path)) as Boolean
