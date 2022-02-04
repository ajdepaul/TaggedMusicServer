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

    private val song1 = Song(
        "title1",
        1,
        1,
        LocalDateTime.parse("0001-01-01T00:00"),    // release date
        LocalDateTime.parse("0001-02-01T00:00"),    // create date
        LocalDateTime.parse("0001-03-01T00:00"),    // modify date
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
    private fun createLibrarySource(): InMemoryLibrarySource {
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
        librarySource.putTag(1, "tag0", Tag("tagType0", null))
        librarySource.putTag(2, "tag0", Tag("tagType0", null))
        librarySource.putTag(3, "tag0", Tag("tagType0", null))

        librarySource.putTag(1, "tag1", Tag(null, "description1"))
        librarySource.putTag(2, "tag2", Tag(null, "description2"))
        librarySource.putTag(3, "tag3", Tag(null, "description3"))

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
        val librarySource = createLibrarySource()

        librarySource.getVersion().let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals("1.0", response.result)
        }
    }

    @Test
    fun testGetDefaultTagType() {
        val librarySource = createLibrarySource()

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
    }

    @Test
    fun testHasSong() {
        val librarySource = createLibrarySource()

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
        librarySource.hasSong(4, "fileName0.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }

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

        librarySource.hasSong(1, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertFalse(response.result)
        }
    }

    @Test
    fun testGetSong() {
        val librarySource = createLibrarySource()

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
            assertEquals(null, response.result)
        }

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

        librarySource.getSong(1, "fileName2.mp3").let { response ->
            assertEquals(Response.Status.SUCCESS, response.status)
            assertEquals(null, response.result)
        }
    }

    @Test
    fun testGetAllSongs() {
        val librarySource = createLibrarySource()

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
    }

    @Test
    fun testGetSongsByTags() {

    }

    @Test
    fun testHasTag() {

    }

    @Test
    fun testGetTag() {

    }

    @Test
    fun testGetAllTags() {

    }

    @Test
    fun testHasData() {

    }

    @Test
    fun testGetData() {

    }

    @Test
    fun testGetAllData() {

    }

/* ------------------------------------------ Updating ------------------------------------------ */

    @Test
    fun testSetDefaultTagType() {

    }

    @Test
    fun testPutSong() {

    }

    @Test
    fun testRemoveSong() {

    }

    @Test
    fun testPutTag() {

    }

    @Test
    fun testRemoveTag() {

    }

    @Test
    fun testPutTagType() {

    }

    @Test
    fun testRemoveTagType() {

    }

    @Test
    fun testPutData() {

    }

    @Test
    fun testRemoveData() {

    }

/* -------------------------------------------- Users ------------------------------------------- */

    @Test
    fun testGetUserById() {

    }

    @Test
    fun testUserByUsername() {

    }

    @Test
    fun testGetAllUsers() {

    }

    @Test
    fun testAddUser() {

    }

    @Test
    fun testUpdateUserName() {

    }

    @Test
    fun testUpdatePassword() {

    }

    @Test
    fun testRemoveUser() {

    }
}
