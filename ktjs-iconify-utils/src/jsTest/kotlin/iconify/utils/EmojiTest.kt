package iconify.utils

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EmojiTest {
    private fun emojis(vararg values: String): Array<StringOrDoubleArray> =
        Array(values.size) { StringOrDoubleArray(values[it]) }

    @Test
    fun convertsUTF16CodePoint() {
        val codePoint = getEmojiCodePoint("2764")
        assertEquals(0x2764, codePoint)

        assertNull(splitUTF32Number(codePoint))
        assertEquals(false, isUTF32SplitNumber(codePoint).unsafeCast<Boolean>())
        assertEquals("\u2764", getEmojiUnicode(DoubleOrString(codePoint.toDouble())))
    }

    @Test
    fun convertsUTF32CodePoint() {
        val codePoint = getEmojiCodePoint("1F49A")
        assertEquals(0x1F49A, codePoint)
        assertEquals(false, isUTF32SplitNumber(codePoint).unsafeCast<Boolean>())

        val sequence = assertNotNull(splitUTF32Number(codePoint)).unsafeCast<Array<Int>>()
        assertContentEquals(arrayOf(55357, 56474), sequence)
        assertEquals(1, isUTF32SplitNumber(sequence[0]).unsafeCast<Int>())
        assertEquals(2, isUTF32SplitNumber(sequence[1]).unsafeCast<Int>())

        assertEquals(codePoint, mergeUTF32Numbers(sequence[0], sequence[1]))
        assertEquals("\uD83D\uDC9A", getEmojiUnicode(DoubleOrString(codePoint.toDouble())))
    }

    @Test
    fun convertsSequencesToUTF16() {
        assertContentEquals(arrayOf<Int>(), convertEmojiSequenceToUTF16(arrayOf()))
        assertContentEquals(
            arrayOf(0x263a, 0xfe0f),
            convertEmojiSequenceToUTF16(arrayOf(0x263a, 0xfe0f)),
        )
        assertContentEquals(
            arrayOf(0xd83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
            convertEmojiSequenceToUTF16(arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f)),
        )
    }

    @Test
    fun convertsSequencesToUTF32() {
        assertContentEquals(arrayOf<Int>(), convertEmojiSequenceToUTF32(arrayOf()))
        assertContentEquals(
            arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f),
            convertEmojiSequenceToUTF32(arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f)),
        )
        assertContentEquals(
            arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f),
            convertEmojiSequenceToUTF32(
                arrayOf(0xd83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
            ),
        )

        assertContentEquals(
            arrayOf(0xa83d, 0xdc41, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f),
            convertEmojiSequenceToUTF32(
                arrayOf(0xa83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
                false,
            ),
        )
        assertFailsWith<Throwable> {
            convertEmojiSequenceToUTF32(
                arrayOf(0xa83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
            )
        }

        assertContentEquals(
            arrayOf(0xd83d, 0xec41, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f),
            convertEmojiSequenceToUTF32(
                arrayOf(0xd83d, 0xec41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
                false,
            ),
        )
        assertFailsWith<Throwable> {
            convertEmojiSequenceToUTF32(
                arrayOf(0xd83d, 0xec41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f),
            )
        }

        assertContentEquals(
            arrayOf(0x1f441, 0xfe0f, 0x200d, 0xd83d),
            convertEmojiSequenceToUTF32(
                arrayOf(0xd83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d),
                false,
            ),
        )
        assertFailsWith<Throwable> {
            convertEmojiSequenceToUTF32(arrayOf(0xd83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d))
        }
    }

    @Test
    fun formatsEmptySequence() {
        assertEquals("", getEmojiSequenceString(arrayOf()))
        assertEquals("", getEmojiSequenceKeyword(arrayOf()))
    }

    @Test
    fun formatsKeycapSequence() {
        val sequence = arrayOf(0x23, 0xfe0f, 0x20e3)
        assertEquals("23-fe0f-20e3", getEmojiSequenceString(sequence))
        assertEquals(
            "0023-fe0f-20e3",
            getEmojiSequenceString(sequence, jsObject("{ add0: true }")),
        )
        assertEquals("23-fe0f-20e3", getEmojiSequenceKeyword(sequence))
    }

    @Test
    fun formatsUTF16AndUTF32Sequences() {
        val utf16 = arrayOf(0xd83d, 0xdc41, 0xfe0f, 0x200d, 0xd83d, 0xdde8, 0xfe0f)
        assertEquals("1f441-fe0f-200d-1f5e8-fe0f", getEmojiSequenceString(utf16))
        assertEquals("d83d-dc41-fe0f-200d-d83d-dde8-fe0f", getEmojiSequenceKeyword(utf16))

        val utf32 = arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f)
        assertEquals("1f441-fe0f-200d-1f5e8-fe0f", getEmojiSequenceString(utf32))
        assertEquals("1f441-fe0f-200d-1f5e8-fe0f", getEmojiSequenceKeyword(utf32))
    }

    @Test
    fun parsesSequencesFromStrings() {
        val sequence = getEmojiSequenceFromString("1F441 FE0F 200D 1F5E8 FE0F ")
        assertContentEquals(arrayOf(0x1f441, 0xfe0f, 0x200d, 0x1f5e8, 0xfe0f), sequence)
        assertContentEquals(
            sequence,
            getEmojiSequenceFromString("1f441-fe0f-200d-1f5e8-fe0f"),
        )
        assertContentEquals(
            sequence,
            convertEmojiSequenceToUTF32(
                getEmojiSequenceFromString("\\uD83D\\uDC41\\uFE0F\\u200D\\uD83D\\uDDE8\\uFE0F"),
            ),
        )

        val split = splitEmojiSequences(sequence)
        assertEquals(2, split.size)
        assertContentEquals(arrayOf(0x1f441, 0xfe0f), split[0])
        assertContentEquals(arrayOf(0x1f5e8, 0xfe0f), split[1])
        assertContentEquals(sequence, joinEmojiSequences(split))

        assertContentEquals(
            arrayOf(0x1f441, 0x200d, 0x1f5e8),
            getUnqualifiedEmojiSequence(sequence),
        )
    }

    @Test
    fun parsesSequencesWithSkinTones() {
        val sequence =
            getEmojiSequenceFromString("1f9d1-1f3ff-200d-1f91d-200d-1f9d1-1f3ff")
        assertContentEquals(
            arrayOf(0x1f9d1, 0x1f3ff, 0x200d, 0x1f91d, 0x200d, 0x1f9d1, 0x1f3ff),
            sequence,
        )

        val split = splitEmojiSequences(sequence)
        assertEquals(3, split.size)
        assertContentEquals(arrayOf(0x1f9d1, 0x1f3ff), split[0])
        assertContentEquals(arrayOf(0x1f91d), split[1])
        assertContentEquals(arrayOf(0x1f9d1, 0x1f3ff), split[2])
        assertContentEquals(sequence, joinEmojiSequences(split))
        assertContentEquals(sequence, getUnqualifiedEmojiSequence(sequence))
    }

    @Test
    fun matchesSimpleEmojiRegex() {
        val regexValue = createOptimisedRegex(emojis("1F600", "1F603", "1F604"))
        val grinning = fromCodePoint(0x1f600)
        val bigEyes = fromCodePoint(0x1f603)

        val matches = globalMatches(
            "grinning face: $grinning\nwith big eyes: $bigEyes\ngrinning face: $grinning",
            jsRegexp(regexValue, "g"),
        )

        assertEquals(3, matches?.size)
        assertEquals(grinning, matches?.get(0))
        assertEquals(bigEyes, matches?.get(1))
        assertEquals(grinning, matches?.get(2))
    }

    @Test
    fun matchesEmojiKeywords() {
        val regexValue = createOptimisedRegex(emojis(":cat:", ":gray_cat:", ":tabby_cat:"))

        val matches = globalMatches(
            "Cat: :cat:\nTabby cat: :tabby_cat:\n",
            jsRegexp(regexValue, "g"),
        )

        assertEquals(2, matches?.size)
        assertEquals(":cat:", matches?.get(0))
        assertEquals(":tabby_cat:", matches?.get(1))
    }

    @Test
    fun matchesEmojiSequences() {
        val regexValue = createOptimisedRegex(
            emojis(
                "263A FE0F",
                "1F62E 200D 1F4A7",
                "1F62E 200D 1F4A8",
                "1F62E 200D 1F4A9",
                "1F62E",
                "1F62D",
                "1F62F",
                "1F4A8",
            ),
        )

        fun code(value: String): String =
            getSequenceFromEmojiStringOrKeyword(value)
                .map { fromCodePoint(it) }
                .joinToString("")

        val text = "dashing away: ${fromCodePoint(0x1f4a8)}\n" +
            "face exhaling: ${code("1f62e-200d-1f4a8")}\n" +
            "face with open mouth: ${fromCodePoint(0x1f62e)}\n" +
            "smiling face: ${code("263a-fe0f")}\n" +
            "smiling face: ${fromCodePoint(0x263a)}\n"

        val matches = globalMatches(text, jsRegexp(regexValue, "g"))
        assertEquals(5, matches?.size)
        assertEquals(code("1f4a8"), matches?.get(0))
        assertEquals(code("1f62e 200d 1f4a8"), matches?.get(1))
        assertEquals(fromCodePoint(0x1f62e), matches?.get(2))
        assertEquals(code("263a fe0f"), matches?.get(3))
        assertEquals(fromCodePoint(0x263a), matches?.get(4))
    }

    @Test
    fun matchesSkinTones() {
        val regexValue = createOptimisedRegex(
            emojis(
                "1f44b", "1f44b-1f3fb", "1f44b-1f3fc", "1f44b-1f3fd", "1f44b-1f3fe",
                "1f44b-1f3ff", "1f91a", "1f91a-1f3fb", "1f91a-1f3fc", "1f91a-1f3fd",
                "1f91a-1f3fe", "1f91a-1f3ff", "1f590-fe0f", "1f590", "1f590-1f3fb",
                "1f590-1f3fc", "1f590-1f3fd", "1f590-1f3fe", "1f590-1f3ff", "1f3fb",
                "1f3fc", "1f3fd", "1f3fe", "1f3ff",
            ),
        )

        val waving = fromCodePoint(0x1f44b) + fromCodePoint(0x1f3fd)
        val matches = globalMatches("waving hand: $waving\n", jsRegexp(regexValue, "g"))

        assertEquals(1, matches?.size)
        assertEquals(waving, matches?.get(0))
    }

    @Test
    fun matchesBuggedMixOfSequences() {
        val full =
            "1f9d1-1f3fb-200d-2764-fe0f-200d-1f48b-200d-1f9d1-1f3fc"
        val regexValue = createOptimisedRegexForEmojiSequences(
            arrayOf(
                getEmojiSequenceFromString(full),
                getEmojiSequenceFromString("1f9d1-1f3fb-200d-2764-fe0f-200d-1f9d1-1f3fc"),
            ),
        )
        val text = getSequenceFromEmojiStringOrKeyword(full)
            .map { fromCodePoint(it) }
            .joinToString("")

        val matches = globalMatches(text, jsRegexp(regexValue, "g"))
        assertEquals(1, matches?.size)
        assertEquals(text, matches?.get(0))
    }

    @Test
    fun replacesEmojisInText() {
        val grinningCat = fromCodePoint(0x1f63a)
        val alien = fromCodePoint(0x1f47d)
        val testEmoji = fromCodePoint(0x1f441) +
            fromCodePoint(0xfe0f) +
            fromCodePoint(0x200d) +
            fromCodePoint(0x1f5e8)

        val regex = createOptimisedRegex(
            emojis(
                "1f63a",
                "1f47d",
                "1F441 FE0F",
                "1F441 FE0F 200D 1F5E8 FE0F",
                "1F5E8 FE0F",
            ),
        )
        val text = "Grinning Cat: $grinningCat, aliens: $alien$alien$alien, Test: $testEmoji end!"

        var alienCalled = 0
        var testCalled = 0
        val replaced = findAndReplaceEmojisInText(
            jsRegexp(regex, "g"),
            text,
        ) { match, _ ->
            when (match.match) {
                grinningCat -> ":cat:"
                alien -> {
                    alienCalled++
                    ":alien:"
                }
                testEmoji -> {
                    testCalled++
                    ":test:"
                }
                else -> js("undefined")
            }
        }

        assertEquals(3, alienCalled)
        assertEquals(1, testCalled)
        assertEquals(
            "Grinning Cat: :cat:, aliens: :alien::alien::alien:, Test: :test: end!",
            replaced,
        )
    }

    @Test
    fun preparesIconsWithoutTestData() {
        val single = prepareEmojiForIconsList(jsObject("{ '2615': 'hot-beverage' }"))
        assertEquals(1, single.icons.size)
        assertEquals("hot-beverage", single.icons[0].icon)
        assertEquals("2615", single.icons[0].sequence)
        assertEquals("\\u2615\\uFE0F?", single.regex)

        val multiple = prepareEmojiForIconsList(
            jsObject(
                "{ '2615': 'hot-beverage', '1f1e6-1f1e8': 'flag-ascension-island', '1f1e6-1f1e9': 'flag-andorra', '1f1e6-1f1ea': 'flag-united-arab-emirates' }",
            ),
        )
        assertEquals(4, multiple.icons.size)
        assertEquals("hot-beverage", multiple.icons[0].icon)
        assertEquals("1f1e6-1f1e8", multiple.icons[1].sequence)
        assertEquals("flag-united-arab-emirates", multiple.icons[3].icon)
        assertEquals("\\uD83C\\uDDE6\\uD83C[\\uDDE8-\\uDDEA]|\\u2615\\uFE0F?", multiple.regex)
    }

    @Test
    fun preparesIconSetEmojis() {
        val iconSet = jsObject(
            "{ prefix: 'test', chars: { '2615': 'hot-beverage', '1f1e6-1f1e8': 'flag-ascension-island' } }",
        ).unsafeCast<iconify.IconifyJSON>()

        val result = prepareEmojiForIconSet(iconSet)
        assertEquals(2, result.icons.size)
        assertEquals("hot-beverage", result.icons[0].icon)
        assertTrue(result.regex.isNotEmpty())
    }

    @Test
    fun parsesEmojiTestFile() {
        val data = """
            # group: Smileys & Emotion
            # subgroup: face-smiling
            1F600 ; fully-qualified # 😀 E1.0 grinning face
            1F601 ; fully-qualified # 😁 E0.6 beaming face with smiling eyes
            # group: Symbols
            # subgroup: keycap
            0023 FE0F 20E3 ; fully-qualified # #️⃣ E0.6 keycap: #
            0023 20E3 ; unqualified # #️⃣ E0.6 keycap: #
        """.trimIndent()

        val parsed = parseEmojiTestFile(data)
        val grinning = prop(parsed, "1f600")
        assertEquals("Smileys & Emotion", prop(grinning, "group").unsafeCast<String>())
        assertEquals("face-smiling", prop(grinning, "subgroup").unsafeCast<String>())
        assertEquals("E1.0", prop(grinning, "version").unsafeCast<String>())
        assertEquals("fully-qualified", prop(grinning, "status").unsafeCast<String>())
        assertEquals("grinning face", prop(grinning, "name").unsafeCast<String>())
        assertContentEquals(
            arrayOf(0x1f600),
            prop(grinning, "sequence").unsafeCast<Array<Int>>(),
        )

        // Qualified and unqualified keycaps are merged into one item keyed by the
        // unqualified sequence.
        val keycap = prop(parsed, "23-20e3")
        assertTrue(prop(keycap, "sequence").unsafeCast<Array<Int>>().contains(0xfe0f))
        assertTrue(
            js("(v) => v === undefined")(prop(parsed, "23-fe0f-20e3")).unsafeCast<Boolean>(),
        )
    }

    private fun assertNotNull(value: dynamic): dynamic {
        kotlin.test.assertNotNull(value)
        return value
    }
}
