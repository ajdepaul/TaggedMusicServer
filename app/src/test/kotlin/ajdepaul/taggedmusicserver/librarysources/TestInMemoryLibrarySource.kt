/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType
import ajdepaul.taggedmusicserver.models.User
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class TestInMemoryLibrarySource {

    // test tags
    private val tag1 = Tag("tagType0", null)
    private val tag2 = Tag("tagType0", null)
    private val tag3 = Tag("tagType0", null)
    private val tag4 = Tag(null, "description1")
    private val tag5 = Tag(null, "description2")
    private val tag6 = Tag(null, "description3")

    // test songs
    private val song1 = Song(
        "title1",
        1,
        1,
        LocalDateTime.parse("0001-01-01T00:00"),
        LocalDateTime.parse("0001-02-01T00:00"),
        LocalDateTime.parse("0001-03-01T00:00"),
        1,
        setOf("tag0")
    )
    private val song2 = Song(
        "title2",
        2,
        2,
        LocalDateTime.parse("0002-01-01T00:00"),
        LocalDateTime.parse("0002-02-01T00:00"),
        LocalDateTime.parse("0002-03-01T00:00"),
        2,
        setOf("tag0")
    )
    private val song3 = Song(
        "title3",
        3,
        3,
        LocalDateTime.parse("0003-01-01T00:00"),
        LocalDateTime.parse("0003-02-01T00:00"),
        LocalDateTime.parse("0003-03-01T00:00"),
        3,
        setOf("tag0")
    )
    private val song4 = Song(
        "title4",
        4,
        null,
        LocalDateTime.parse("0004-01-01T00:00"),
        LocalDateTime.parse("0004-02-01T00:00"),
        LocalDateTime.parse("0004-03-01T00:00"),
        4,
        setOf("tag0", "tag1")
    )
    private val song5 = Song(
        "title5",
        5,
        null,
        LocalDateTime.parse("0005-01-01T00:00"),
        LocalDateTime.parse("0005-02-01T00:00"),
        LocalDateTime.parse("0005-03-01T00:00"),
        5,
        setOf("tag0", "tag2")
    )
    private val song6 = Song(
        "title6",
        6,
        null,
        LocalDateTime.parse("0006-01-01T00:00"),
        LocalDateTime.parse("0006-02-01T00:00"),
        LocalDateTime.parse("0006-03-01T00:00"),
        6,
        setOf("tag0", "tag3")
    )

    /** Creates a new [InMemoryLibrarySource] with multiple user libraries for testing. */
    private fun createTestLibrarySource(): InMemoryLibrarySource {
        val librarySource = InMemoryLibrarySource()

        // users
        librarySource.addUser(User(1, "usernameA", "salt1", "passHash1"), TagType(1))
        librarySource.addUser(User(2, "usernameB", "salt2", "passHash2"), TagType(2))
        librarySource.addUser(User(3, "usernameC", "salt3", "passHash3"), TagType(3))

        // data
        librarySource.putData(1, "data0", "value1")
        librarySource.putData(2, "data0", "value2")
        librarySource.putData(3, "data0", "value3")

        librarySource.putData(1, "data1", "value4")
        librarySource.putData(2, "data2", "value5")
        librarySource.putData(3, "data3", "value6")

        // tag types
        librarySource.putTagType(1, "tagType0", TagType(1))
        librarySource.putTagType(2, "tagType0", TagType(2))
        librarySource.putTagType(3, "tagType0", TagType(3))

        librarySource.putTagType(1, "tagType1", TagType(4))
        librarySource.putTagType(2, "tagType2", TagType(5))
        librarySource.putTagType(3, "tagType3", TagType(6))

        // tags
        librarySource.putTag(1, "tag0", tag1)
        librarySource.putTag(2, "tag0", tag2)
        librarySource.putTag(3, "tag0", tag3)

        librarySource.putTag(1, "tag1", tag4)
        librarySource.putTag(2, "tag2", tag5)
        librarySource.putTag(3, "tag3", tag6)

        // songs
        librarySource.putSong(1, "fileName0.mp3", song1)
        librarySource.putSong(2, "fileName0.mp3", song2)
        librarySource.putSong(3, "fileName0.mp3", song3)

        librarySource.putSong(1, "fileName1.mp3", song4)
        librarySource.putSong(2, "fileName2.mp3", song5)
        librarySource.putSong(3, "fileName3.mp3", song6)

        return librarySource
    }

/* ----------------------------------------- Retrieving ----------------------------------------- */

    @Test
    fun testGetVersion() {
        val librarySource = createTestLibrarySource()

        librarySource.getVersion().let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("1.0", response.result)
        }
    }

    @Test
    fun testGetDefaultTagType() {
        val librarySource = createTestLibrarySource()

        librarySource.getDefaultTagType(1).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(TagType(1), response.result)
        }
        librarySource.getDefaultTagType(2).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(TagType(2), response.result)
        }
        librarySource.getDefaultTagType(3).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(TagType(3), response.result)
        }

        // expected failure
        librarySource.getDefaultTagType(4).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
    }

    @Test
    fun testHasSong() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.hasSong(1, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasSong(2, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasSong(3, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // key not shared between users
        librarySource.hasSong(1, "fileName1.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasSong(2, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasSong(3, "fileName3.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // expected failure
        librarySource.hasSong(1, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
        librarySource.hasSong(4, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
    }

    @Test
    fun testGetSong() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.getSong(1, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song1, response.result)
        }
        librarySource.getSong(2, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song2, response.result)
        }
        librarySource.getSong(3, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song3, response.result)
        }
        librarySource.getSong(4, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }

        // key not shared between users
        librarySource.getSong(1, "fileName1.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song4, response.result)
        }
        librarySource.getSong(2, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song5, response.result)
        }
        librarySource.getSong(3, "fileName3.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(song6, response.result)
        }

        // expected failure
        librarySource.getSong(1, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
        librarySource.hasSong(4, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
    }

    @Test
    fun testGetAllSongs() {
        val librarySource = createTestLibrarySource()

        librarySource.getAllSongs(1).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it.value == song1 })
            assertTrue(songs.any { it.key == "fileName1.mp3" && it.value == song4 })
        }
        librarySource.getAllSongs(2).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it.value == song2 })
            assertTrue(songs.any { it.key == "fileName2.mp3" && it.value == song5 })
        }
        librarySource.getAllSongs(3).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it.value == song3 })
            assertTrue(songs.any { it.key == "fileName3.mp3" && it.value == song6 })
        }

        // expected failure
        librarySource.getAllSongs(4).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result.isEmpty())
        }
    }

    @Test
    fun testGetSongsByTags() {
        val librarySource = createTestLibrarySource()

        // includes a tag
        librarySource.getSongsByTags(1, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song1 })
            assertTrue(songs.any { it.key == "fileName1.mp3" && it .value == song4 })
        }
        librarySource.getSongsByTags(2, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song2 })
            assertTrue(songs.any { it.key == "fileName2.mp3" && it .value == song5 })
        }
        librarySource.getSongsByTags(3, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song3 })
            assertTrue(songs.any { it.key == "fileName3.mp3" && it .value == song6 })
        }

        // includes 2 tags
        librarySource.getSongsByTags(1, setOf("tag0", "tag1")).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(1, songs.size)
            assertTrue(songs.any { it.key == "fileName1.mp3" && it .value == song4 })
        }
        librarySource.getSongsByTags(2, setOf("tag0", "tag2")).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(1, songs.size)
            assertTrue(songs.any { it.key == "fileName2.mp3" && it .value == song5 })
        }
        librarySource.getSongsByTags(3, setOf("tag0", "tag3")).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(1, songs.size)
            assertTrue(songs.any { it.key == "fileName3.mp3" && it .value == song6 })
        }

        // excludes a tag
        librarySource.getSongsByTags(1, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song1 })
            assertTrue(songs.any { it.key == "fileName1.mp3" && it .value == song4 })
        }
        librarySource.getSongsByTags(2, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song2 })
            assertTrue(songs.any { it.key == "fileName2.mp3" && it .value == song5 })
        }
        librarySource.getSongsByTags(3, setOf("tag0"), setOf()).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "fileName0.mp3" && it .value == song3 })
            assertTrue(songs.any { it.key == "fileName3.mp3" && it .value == song6 })
        }

        // expected empty
        librarySource.getSongsByTags(1, setOf("tag2")).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result.isEmpty())
        }

        // expected failure
        librarySource.getSongsByTags(4, setOf("tag0")).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result.isEmpty())
        }
    }

    @Test
    fun testHasTag() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.hasTag(1, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasTag(2, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasTag(3, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // key not shared between users
        librarySource.hasTag(1, "tag1").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasTag(2, "tag2").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasTag(3, "tag3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // expected failure
        librarySource.hasTag(1, "tag4").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
        librarySource.hasTag(4, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
    }

    @Test
    fun testGetTag() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.getTag(1, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag1, response.result)
        }
        librarySource.getTag(2, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag2, response.result)
        }
        librarySource.getTag(3, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag3, response.result)
        }

        // key not shared between users
        librarySource.getTag(1, "tag1").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag4, response.result)
        }
        librarySource.getTag(2, "tag2").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag5, response.result)
        }
        librarySource.getTag(3, "tag3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(tag6, response.result)
        }

        // expected failure
        librarySource.getTag(1, "tag4").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
        librarySource.getTag(4, "tag0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
    }

    @Test
    fun testGetAllTags() {
        val librarySource = createTestLibrarySource()

        librarySource.getAllTags(1).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "tag0" && it.value == tag1 })
            assertTrue(songs.any { it.key == "tag1" && it.value == tag4 })
        }
        librarySource.getAllTags(2).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "tag0" && it.value == tag2 })
            assertTrue(songs.any { it.key == "tag2" && it.value == tag5 })
        }
        librarySource.getAllTags(3).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "tag0" && it.value == tag3 })
            assertTrue(songs.any { it.key == "tag3" && it.value == tag6 })
        }

        // expected failure
        librarySource.getAllTags(4).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result.isEmpty())
        }
    }

    @Test
    fun testHasData() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.hasData(1, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasData(2, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasData(3, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // key not shared between users
        librarySource.hasData(1, "data1").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasData(2, "data2").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }
        librarySource.hasData(3, "data3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result)
        }

        // expected failure
        librarySource.hasData(1, "data4").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
        librarySource.hasData(4, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
    }

    @Test
    fun testGetData() {
        val librarySource = createTestLibrarySource()

        // key shared between users
        librarySource.getData(1, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value1", response.result)
        }
        librarySource.getData(2, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value2", response.result)
        }
        librarySource.getData(3, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value3", response.result)
        }

        // key not shared between users
        librarySource.getData(1, "data1").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value4", response.result)
        }
        librarySource.getData(2, "data2").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value5", response.result)
        }
        librarySource.getData(3, "data3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("value6", response.result)
        }

        // expected failure
        librarySource.getData(1, "data4").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
        librarySource.getData(4, "data0").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertNull(response.result)
        }
    }

    @Test
    fun testGetAllData() {
        val librarySource = createTestLibrarySource()

        librarySource.getAllData(1).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "data0" && it.value == "value1" })
            assertTrue(songs.any { it.key == "data1" && it.value == "value4" })
        }
        librarySource.getAllData(2).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "data0" && it.value == "value2" })
            assertTrue(songs.any { it.key == "data2" && it.value == "value5" })
        }
        librarySource.getAllData(3).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            val songs = response.result
            assertEquals(2, songs.size)
            assertTrue(songs.any { it.key == "data0" && it.value == "value3" })
            assertTrue(songs.any { it.key == "data3" && it.value == "value6" })
        }

        // expected failure
        librarySource.getAllData(4).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertTrue(response.result.isEmpty())
        }
    }

/* ------------------------------------------ Updating ------------------------------------------ */

    @Test
    fun testSetDefaultTagType() {
        val librarySource = createTestLibrarySource()

        librarySource.setDefaultTagType(1, TagType(7))
        assertEquals(TagType(7), librarySource.getDefaultTagType(1).result)
        assertEquals(TagType(2), librarySource.getDefaultTagType(2).result)
        assertEquals(TagType(3), librarySource.getDefaultTagType(3).result)

        librarySource.setDefaultTagType(2, TagType(8))
        assertEquals(TagType(7), librarySource.getDefaultTagType(1).result)
        assertEquals(TagType(8), librarySource.getDefaultTagType(2).result)
        assertEquals(TagType(3), librarySource.getDefaultTagType(3).result)

        librarySource.setDefaultTagType(3, TagType(9))
        assertEquals(TagType(7), librarySource.getDefaultTagType(1).result)
        assertEquals(TagType(8), librarySource.getDefaultTagType(2).result)
        assertEquals(TagType(9), librarySource.getDefaultTagType(3).result)
    }

    @Test
    fun testPutSong() {
        val librarySource = createTestLibrarySource()

        // update song
        val song7 = Song(
            "title7",
            7,
            7,
            LocalDateTime.parse("0007-01-01T00:00"),
            LocalDateTime.parse("0007-02-01T00:00"),
            LocalDateTime.parse("0007-03-01T00:00"),
            7,
            setOf("tag7")
        )
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putSong(1, "fileName0.mp3", song7).status
        )

        assertEquals(song7, librarySource.getSong(1, "fileName0.mp3").result)
        assertEquals(song2, librarySource.getSong(2, "fileName0.mp3").result)
        assertEquals(song3, librarySource.getSong(3, "fileName0.mp3").result)

        assertEquals(Tag(null, null), librarySource.getTag(1, "tag7").result)
        assertFalse(librarySource.hasTag(2, "tag7").result)
        assertFalse(librarySource.hasTag(3, "tag7").result)

        // new song
        val song8 = Song(
            "title8",
            8,
            8,
            LocalDateTime.parse("0008-01-01T00:00"),
            LocalDateTime.parse("0008-02-01T00:00"),
            LocalDateTime.parse("0008-03-01T00:00"),
            8,
            setOf("tag8", "tag9")
        )
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putSong(2, "fileName4.mp3", song8).status
        )

        assertFalse(librarySource.hasSong(1, "fileName4.mp3").result)
        assertEquals(song8, librarySource.getSong(2, "fileName4.mp3").result)
        assertFalse(librarySource.hasSong(3, "fileName4.mp3").result)

        assertFalse(librarySource.hasTag(1, "tag8").result)
        assertEquals(Tag(null, null), librarySource.getTag(2, "tag8").result)
        assertFalse(librarySource.hasTag(3, "tag8").result)

        assertFalse(librarySource.hasTag(1, "tag9").result)
        assertEquals(Tag(null, null), librarySource.getTag(2, "tag9").result)
        assertFalse(librarySource.hasTag(3, "tag9").result)
    }

    @Test
    fun testRemoveSong() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.removeSong(1, "fileName0.mp3").status
        )

        assertFalse(librarySource.hasSong(1, "fileName0.mp3").result)
        assertTrue(librarySource.hasSong(2, "fileName0.mp3").result)
        assertTrue(librarySource.hasSong(3, "fileName0.mp3").result)
    }

    @Test
    fun testPutTag() {
        val librarySource = createTestLibrarySource()

        // update tag
        val tag7 = Tag("tagType4", "description4")
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putTag(1, "tag0", tag7).status
        )

        assertEquals(tag7, librarySource.getTag(1, "tag0").result)
        assertEquals(tag2, librarySource.getTag(2, "tag0").result)
        assertEquals(tag3, librarySource.getTag(3, "tag0").result)

        assertEquals(
            librarySource.getDefaultTagType(1).result,
            librarySource.getTagType(1, "tagType4").result
        )
        assertFalse(librarySource.hasTagType(2, "tagType4").result)
        assertFalse(librarySource.hasTagType(3, "tagType4").result)

        // new tag
        val tag8 = Tag("tagType5", null)
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putTag(2, "tag4", tag8).status
        )

        assertFalse(librarySource.hasTag(1, "tag4").result)
        assertEquals(tag8, librarySource.getTag(2, "tag4").result)
        assertFalse(librarySource.hasTag(3, "tag4").result)

        assertFalse(librarySource.hasTagType(1, "tagType5").result)
        assertEquals(
            librarySource.getDefaultTagType(2).result,
            librarySource.getTagType(2, "tagType5").result
        )
        assertFalse(librarySource.hasTagType(3, "tagType5").result)
    }

    @Test
    fun testRemoveTag() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.removeTag(1, "tag0").status
        )

        assertFalse(librarySource.hasTag(1, "tag0").result)
        assertTrue(librarySource.hasTag(2, "tag0").result)
        assertTrue(librarySource.hasTag(3, "tag0").result)

        assertTrue(librarySource.getAllSongs(1).result.values.none { it.tags.contains("tag0") })
        assertEquals(
            2,
            librarySource.getAllSongs(2).result.values.count { it.tags.contains("tag0") }
        )
        assertEquals(
            2,
            librarySource.getAllSongs(3).result.values.count { it.tags.contains("tag0") }
        )
    }

    @Test
    fun testPutTagType() {
        val librarySource = createTestLibrarySource()

        // update tag type
        val tagType1 = TagType(99)
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putTagType(1, "tagType0", tagType1).status
        )

        assertEquals(tagType1, librarySource.getTagType(1, "tagType0").result)
        assertEquals(TagType(2), librarySource.getTagType(2, "tagType0").result)
        assertEquals(TagType(3), librarySource.getTagType(3, "tagType0").result)

        // new tag type
        val tagType2 = TagType(999)
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putTagType(2, "tagType4", tagType2).status
        )

        assertFalse(librarySource.hasTagType(1, "tagType4").result)
        assertEquals(tagType2, librarySource.getTagType(2, "tagType4").result)
        assertFalse(librarySource.hasTagType(3, "tagType4").result)
    }

    @Test
    fun testRemoveTagType() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.removeTagType(1, "tagType0").status
        )

        assertFalse(librarySource.hasTagType(1, "tagType0").result)
        assertTrue(librarySource.hasTagType(2, "tagType0").result)
        assertTrue(librarySource.hasTagType(3, "tagType0").result)

        assertTrue(librarySource.getAllTags(1).result.values.none { it.type == "tagType0" })
        assertEquals(1, librarySource.getAllTags(2).result.values.count { it.type == "tagType0" })
        assertEquals(1, librarySource.getAllTags(3).result.values.count { it.type == "tagType0" })
    }

    @Test
    fun testPutData() {
        val librarySource = createTestLibrarySource()

        // update data
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putData(1, "data0", "value7").status
        )

        assertEquals("value7", librarySource.getData(1, "data0").result)
        assertEquals("value2", librarySource.getData(2, "data0").result)
        assertEquals("value3", librarySource.getData(3, "data0").result)

        // new data
        assertEquals(
            Response.Status.SUCCESS,
            librarySource.putData(2, "data4", "value8").status
        )

        assertFalse(librarySource.hasData(1, "data4").result)
        assertEquals("value8", librarySource.getData(2, "data4").result)
        assertFalse(librarySource.hasData(3, "data4").result)
    }

    @Test
    fun testRemoveData() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.removeData(1, "data0").status
        )

        assertFalse(librarySource.hasData(1, "data0").result)
        assertTrue(librarySource.hasData(2, "data0").result)
        assertTrue(librarySource.hasData(3, "data0").result)
    }

/* -------------------------------------------- Users ------------------------------------------- */

    @Test
    fun testGetUserById() {
        val librarySource = createTestLibrarySource()

        val user1 = librarySource.getUser(1).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(1, user1.id)
        assertEquals("usernameA", user1.username)
        assertEquals("salt1", user1.salt)
        assertEquals("passHash1", user1.passHash)

        val user2 = librarySource.getUser(2).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(2, user2.id)
        assertEquals("usernameB", user2.username)
        assertEquals("salt2", user2.salt)
        assertEquals("passHash2", user2.passHash)

        val user3 = librarySource.getUser(3).let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(3, user3.id)
        assertEquals("usernameC", user3.username)
        assertEquals("salt3", user3.salt)
        assertEquals("passHash3", user3.passHash)
    }

    @Test
    fun testUserByUsername() {
        val librarySource = createTestLibrarySource()

        val user1 = librarySource.getUser("usernameA").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(1, user1.id)
        assertEquals("usernameA", user1.username)
        assertEquals("salt1", user1.salt)
        assertEquals("passHash1", user1.passHash)

        val user2 = librarySource.getUser("usernameB").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(2, user2.id)
        assertEquals("usernameB", user2.username)
        assertEquals("salt2", user2.salt)
        assertEquals("passHash2", user2.passHash)

        val user3 = librarySource.getUser("usernameC").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(3, user3.id)
        assertEquals("usernameC", user3.username)
        assertEquals("salt3", user3.salt)
        assertEquals("passHash3", user3.passHash)
    }

    @Test
    fun testGetAllUsers() {
        val librarySource = createTestLibrarySource()

        val users = librarySource.getAllUsers().let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            response.result!!
        }

        assertEquals(3, users.size)

        val user1 = users.find { it.id == 1 }!!
        assertEquals(1, user1.id)
        assertEquals("usernameA", user1.username)
        assertEquals("salt1", user1.salt)
        assertEquals("passHash1", user1.passHash)

        val user2 = users.find { it.id == 2 }!!
        assertEquals(2, user2.id)
        assertEquals("usernameB", user2.username)
        assertEquals("salt2", user2.salt)
        assertEquals("passHash2", user2.passHash)

        val user3 = users.find { it.id == 3 }!!
        assertEquals(3, user3.id)
        assertEquals("usernameC", user3.username)
        assertEquals("salt3", user3.salt)
        assertEquals("passHash3", user3.passHash)
    }

    @Test
    fun testAddUser() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.BAD_REQUEST,
            librarySource.addUser(User(1, "usernameE", "salt5", "passHash5"), TagType(5)).status
        )

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.addUser(User(4, "usernameD", "salt4", "passHash4"), TagType(4)).status
        )

        val user4 = librarySource.getAllUsers().result.find { it.id == 4 }!!
        assertEquals(4, user4.id)
        assertEquals("usernameD", user4.username)
        assertEquals("salt4", user4.salt)
        assertEquals("passHash4", user4.passHash)

        assertEquals(TagType(4), librarySource.getDefaultTagType(4).result)
        val tagTypes = librarySource.getAllTagTypes(4).result
        assertEquals(1, tagTypes.size)
        assertEquals(TagType(4), tagTypes[""])
        assertTrue(librarySource.getAllTags(4).result.isEmpty())
        assertTrue(librarySource.getAllSongs(4).result.isEmpty())
        assertTrue(librarySource.getAllData(4).result.isEmpty())
    }

    @Test
    fun testUpdateUserName() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.BAD_REQUEST,
            librarySource.updateUserName(4, "username").status
        )

        assertEquals(Response.Status.SUCCESS, librarySource.updateUserName(1, "usernameD").status)

        assertEquals("usernameD", librarySource.getUser(1).result!!.username)
        assertEquals(
            "usernameD",
            librarySource.getAllUsers().result.find { it.id == 1 }!!.username
        )
    }

    @Test
    fun testUpdatePassword() {
        val librarySource = createTestLibrarySource()

        assertEquals(
            Response.Status.BAD_REQUEST,
            librarySource.updatePassword(4, "salt", "passHash").status
        )

        assertEquals(
            Response.Status.SUCCESS,
            librarySource.updatePassword(1, "salt4", "passHash4").status
        )

        assertEquals("salt4", librarySource.getUser(1).result!!.salt)
        assertEquals("passHash4", librarySource.getUser(1).result!!.passHash)
        assertEquals("salt4", librarySource.getAllUsers().result.find { it.id == 1 }!!.salt)
        assertEquals(
            "passHash4",
            librarySource.getAllUsers().result.find { it.id == 1 }!!.passHash
        )
    }

    @Test
    fun testRemoveUser() {
        val librarySource = createTestLibrarySource()

        assertEquals(Response.Status.BAD_REQUEST, librarySource.removeUser(4).status)

        assertEquals(Response.Status.SUCCESS, librarySource.removeUser(1).status)

        assertNull(librarySource.getUser(1).result)
        assertTrue(librarySource.getAllTagTypes(1).result.isEmpty())
        assertTrue(librarySource.getAllTags(1).result.isEmpty())
        assertTrue(librarySource.getAllSongs(1).result.isEmpty())
        assertTrue(librarySource.getAllData(1).result.isEmpty())

        assertNotNull(librarySource.getUser(2).result)
        assertTrue(librarySource.getAllTagTypes(2).result.isNotEmpty())
        assertTrue(librarySource.getAllTags(2).result.isNotEmpty())
        assertTrue(librarySource.getAllSongs(2).result.isNotEmpty())
        assertTrue(librarySource.getAllData(2).result.isNotEmpty())

        assertNotNull(librarySource.getUser(3).result)
        assertTrue(librarySource.getAllTagTypes(3).result.isNotEmpty())
        assertTrue(librarySource.getAllTags(3).result.isNotEmpty())
        assertTrue(librarySource.getAllSongs(3).result.isNotEmpty())
        assertTrue(librarySource.getAllData(3).result.isNotEmpty())
    }
}
