package iconify.tools

import iconify.IconifyJSON
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MergeTest {
    private fun iconSet(source: String): IconSet =
        IconSet(jsObject(source).unsafeCast<IconifyJSON>())

    @Test
    fun mergesSimpleIconSets() {
        val oldSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    ChromeMaximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    ChromeMinimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'},
                },
                width: 24,
                height: 24,
                info: {name: 'Foo', author: {name: ''}, license: {title: ''}},
            }"""
        )
        val newSet = iconSet(
            """{
                prefix: 'bar',
                icons: {
                    remove: {body: '<g fill="currentColor"><path d="M15 8H1V7h14v1z"/></g>'},
                },
            }"""
        )

        val merged = mergeIconSets(oldSet, newSet)
        assertTrue(merged.lastModified > 0.0)

        val expected = jsObject(
            """{
                prefix: 'bar',
                icons: {
                    remove: {body: '<g fill="currentColor"><path d="M15 8H1V7h14v1z"/></g>', width: 16, height: 16},
                    ChromeMaximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>', hidden: true},
                    ChromeMinimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>', hidden: true},
                },
                width: 24,
                height: 24,
            }"""
        )
        expected.lastModified = merged.lastModified
        assertJsDeepEquals(expected, merged.export())
        assertEquals(1, merged.count())
    }

    @Test
    fun mergesSimpleIconSetsReversed() {
        val newSet = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    ChromeMaximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    ChromeMinimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'},
                },
                width: 24,
                height: 24,
                info: {name: 'Foo', author: {name: ''}, license: {title: ''}, tags: ['Foo']},
            }"""
        )
        val oldSet = iconSet(
            """{
                prefix: 'bar',
                icons: {
                    remove: {body: '<g fill="currentColor"><path d="M15 8H1V7h14v1z"/></g>'},
                },
            }"""
        )

        val merged = mergeIconSets(oldSet, newSet)
        assertTrue(merged.lastModified > 0.0)

        val expected = jsObject(
            """{
                prefix: 'foo',
                info: {name: 'Foo', author: {name: ''}, license: {title: ''}, total: 2, tags: ['Foo']},
                icons: {
                    remove: {body: '<g fill="currentColor"><path d="M15 8H1V7h14v1z"/></g>', width: 16, height: 16, hidden: true},
                    ChromeMaximize: {body: '<g fill="currentColor"><path d="M3 3v10h10V3H3zm9 9H4V4h8v8z"/></g>'},
                    ChromeMinimize: {body: '<g fill="currentColor"><path d="M14 8v1H3V8h11z"/></g>'},
                },
                width: 24,
                height: 24,
            }"""
        )
        expected.lastModified = merged.lastModified
        assertJsDeepEquals(expected, merged.export())
        assertEquals(2, merged.count())
    }

    @Test
    fun swapsIconAndAlias() {
        val lastModified1 = 12345.0
        val set1 = iconSet(
            """{
                prefix: 'foo',
                lastModified: 12345,
                icons: {
                    bar: {body: '<g/>'},
                },
                aliases: {
                    baz: {parent: 'bar'},
                    foo: {parent: 'bar'},
                    foo2: {parent: 'bar', rotate: 2},
                },
            }"""
        )
        val set2 = iconSet(
            """{
                prefix: 'bar',
                lastModified: 23456,
                icons: {
                    baz: {body: '<g/>'},
                },
                aliases: {
                    bar: {parent: 'baz'},
                },
            }"""
        )

        val merged = mergeIconSets(set1, set2)
        assertEquals(lastModified1, merged.lastModified)

        assertJsObject(
            """{
                prefix: 'bar',
                lastModified: 12345,
                icons: {
                    baz: {body: '<g/>'},
                },
                aliases: {
                    bar: {parent: 'baz'},
                    foo: {parent: 'baz'},
                    foo2: {parent: 'baz', rotate: 2, hidden: true},
                },
            }""",
            merged.export(),
        )
        assertEquals(1, merged.count())
    }

    @Test
    fun aliasesIdenticalIconsWithDifferentNames() {
        val set1 = iconSet(
            """{
                prefix: 'foo',
                lastModified: 123456,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                },
            }"""
        )
        val set2 = iconSet(
            """{
                prefix: 'bar',
                lastModified: 234567,
                icons: {
                    icon2: {body: '<g/>'},
                },
                aliases: {
                    alias2: {parent: 'icon2'},
                },
            }"""
        )

        val merged = mergeIconSets(set1, set2)
        val lastModified = merged.lastModified
        assertTrue(lastModified > 0.0)
        assertTrue(lastModified != 123456.0)
        assertTrue(lastModified != 234567.0)

        val expected = jsObject(
            """{
                prefix: 'bar',
                icons: {
                    icon2: {body: '<g/>'},
                },
                aliases: {
                    alias2: {parent: 'icon2'},
                    icon1: {parent: 'icon2'},
                    alias1: {parent: 'icon2'},
                },
            }"""
        )
        expected.lastModified = lastModified
        assertJsDeepEquals(expected, merged.export())
        assertEquals(1, merged.count())
    }

    @Test
    fun keepsLastModifiedOfIdenticalIconSets() {
        val lastModified1 = 123456.0
        val set1 = iconSet(
            """{
                prefix: 'foo',
                lastModified: 123456,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                },
            }"""
        )
        val set2 = iconSet(
            """{
                prefix: 'bar',
                lastModified: 234567,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                },
            }"""
        )

        val merged = mergeIconSets(set1, set2)
        assertEquals(lastModified1, merged.lastModified)

        assertJsObject(
            """{
                prefix: 'bar',
                lastModified: 123456,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                },
            }""",
            merged.export(),
        )
        assertEquals(1, merged.count())
    }

    @Test
    fun handlesDifferentAliasOrder() {
        val lastModified = 234567.0
        val set1 = iconSet(
            """{
                prefix: 'foo',
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                    alias2: {parent: 'alias1'},
                },
            }"""
        )
        val set2 = iconSet(
            """{
                prefix: 'bar',
                lastModified: 234567,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'alias2'},
                    alias2: {parent: 'icon1'},
                },
            }"""
        )

        val merged = mergeIconSets(set1, set2)
        assertEquals(lastModified, merged.lastModified)

        assertJsObject(
            """{
                prefix: 'bar',
                lastModified: 234567,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'alias2'},
                    alias2: {parent: 'icon1'},
                },
            }""",
            merged.export(),
        )
        assertEquals(1, merged.count())
    }

    @Test
    fun handlesSwappedIconAndAliasWithHiddenVariation() {
        val lastModified = 234567.0
        val set1 = iconSet(
            """{
                prefix: 'foo',
                lastModified: 234567,
                icons: {
                    icon1: {body: '<g/>'},
                },
                aliases: {
                    alias1: {parent: 'icon1'},
                },
            }"""
        )
        val set2 = iconSet(
            """{
                prefix: 'bar',
                icons: {
                    alias1: {body: '<g/>'},
                },
                aliases: {
                    icon1: {parent: 'alias1', hidden: true},
                },
            }"""
        )

        val merged = mergeIconSets(set1, set2)
        assertEquals(lastModified, merged.lastModified)

        assertJsObject(
            """{
                prefix: 'bar',
                lastModified: 234567,
                icons: {
                    alias1: {body: '<g/>'},
                },
                aliases: {
                    icon1: {parent: 'alias1', hidden: true},
                },
            }""",
            merged.export(),
        )
        assertEquals(1, merged.count())
    }
}
