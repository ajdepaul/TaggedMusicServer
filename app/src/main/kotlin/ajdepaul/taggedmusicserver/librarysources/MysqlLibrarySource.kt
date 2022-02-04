/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType
import ajdepaul.taggedmusicserver.models.User
import java.sql.*
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import java.io.Closeable

class MysqlLibrarySource(
    dataSource: MysqlDataSource
) : LibrarySource, Closeable { // TODO figure out error return type

    private val connection = dataSource.connection

    override fun close() {
        connection.close()
    }

/* ----------------------------------------- Retrieving ----------------------------------------- */

    override fun getVersion(): Response<String> {
        TODO("Not yet implemented")
    }

    override fun getDefaultTagType(userId: Int): Response<TagType?> {
        TODO("Not yet implemented")
    }

    override fun hasSong(userId: Int, fileName: String): Response<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getSong(userId: Int, fileName: String): Response<Song?> {
        TODO("Not yet implemented")
    }

    override fun getAllSongs(userId: Int): Response<Map<String, Song>> {
        TODO("Not yet implemented")
    }

    override fun getSongsByTags(
        userId: Int,
        includeTags: Set<String>,
        excludeTags: Set<String>
    ): Response<Map<String, Song>> {
        TODO("Not yet implemented")
    }

    override fun hasTag(userId: Int, tagName: String): Response<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getTag(userId: Int, tagName: String): Response<Tag?> {
        TODO("Not yet implemented")
    }

    override fun getAllTags(userId: Int): Response<Map<String, Tag>> {
        TODO("Not yet implemented")
    }

    override fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getTagType(userId: Int, tagTypeName: String): Response<TagType?> {
        TODO("Not yet implemented")
    }

    override fun getAllTagTypes(userId: Int): Response<Map<String, TagType>> {
        TODO("Not yet implemented")
    }

    override fun hasData(userId: Int, key: String): Response<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getData(userId: Int, key: String): Response<String?> {
        TODO("Not yet implemented")
    }

    override fun getAllData(userId: Int): Response<Map<String, String>> {
        TODO("Not yet implemented")
    }

/* ------------------------------------------ Updating ------------------------------------------ */

    override fun setDefaultTagType(userId: Int, tagType: TagType): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun putSong(userId: Int, fileName: String, song: Song): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun removeSong(userId: Int, fileName: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun putTag(userId: Int, tagName: String, tag: Tag): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun removeTag(userId: Int, tagName: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun putTagType(userId: Int, tagTypeName: String, tagType: TagType): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun removeTagType(userId: Int, tagTypeName: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun putData(userId: Int, key: String, value: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun removeData(userId: Int, key: String, value: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun getUser(userId: Int): Response<User?> {
        TODO("Not yet implemented")
    }

    override fun getUser(username: String): Response<User?> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): Response<Set<User>> {
        TODO("Not yet implemented")
    }

    override fun addUser(user: User, defaultTagType: TagType): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun updateUserName(userId: Int, username: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun updatePassword(userId: Int, salt: String, passHash: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override fun removeUser(userId: Int): Response<Unit> {
        TODO("Not yet implemented")
    }

///* ------------------------------------- Callable Statements ------------------------------------ */
//
//    /* ----- Retrieving Procedures -----*/
//
//    /** Result: the version of the library. */
//    val libraryGetVersion: CallableStatement =
//        connection.prepareCall("{call Library_get_version()}")
//
//    /** Result: the default tag type. */
//    val tagTypesGetDefault: CallableStatement =
//        connection.prepareCall("{call TagTypes_get_default()}")
//
//    /** Result: the `file_name` song. */
//    val songsSelect: CallableStatement = connection.prepareCall("{call Songs_select(?)}")
//
//    /** Result: all the songs. */
//    val songsSelectAll: CallableStatement = connection.prepareCall("{call Songs_select_all()}")
//
//    /** Result: the `name` tag. */
//    val tagsSelect: CallableStatement = connection.prepareCall("{call Tags_select(?)}")
//
//    /** Result: all the tags. */
//    val tagsSelectAll: CallableStatement = connection.prepareCall("{call Tags_select_all()}")
//
//    /** Result: the `name` tag type. */
//    val tagTypesSelect: CallableStatement = connection.prepareCall("{call TagTypes_select(?)}")
//
//    /** Result: all the tag types. */
//    val tagTypesSelectAll: CallableStatement =
//        connection.prepareCall("{call TagTypes_select_all()}")
//
//    /** Result: all the tags that `file_name` song has. */
//    val songHasTagSelectSongTags: CallableStatement =
//        connection.prepareCall("{call SongHasTag_select_song_tags(?)}")
//
//    /** Result: all song has tags relationships. */
//    val songHasTagSelectAll: CallableStatement =
//        connection.prepareCall("{call SongHasTag_select_all()}")
//
//    /** Result: the `k` data entry. */
//    val dataSelect: CallableStatement = connection.prepareCall("{call Data_select(?)}")
//
//    /** Result: all the data entries. */
//    val dataSelectAll: CallableStatement = connection.prepareCall("{call Data_select_all()}")
//
//    /* ----- Updating Procedures -----*/
//
//    /** Inserts/updates a song. */
//    val songsPut: CallableStatement =
//        connection.prepareCall("{call Songs_put(?, ?, ?, ?, ?, ?, ?, ?)}")
//
//    /** Removes a song. */
//    val songsRemove: CallableStatement = connection.prepareCall("{call Songs_remove(?)}")
//
//    /** Inserts/updates a tag. */
//    val tagsPut: CallableStatement = connection.prepareCall("{call Tags_put(?, ?, ?)}")
//
//    /** Removes a tag. */
//    val tagsRemove: CallableStatement = connection.prepareCall("{call Tags_remove(?)}")
//
//    /** Inserts/updates a tag type.  */
//    val tagTypesPut: CallableStatement = connection.prepareCall("{call TagTypes_put(?, ?)}")
//
//    /** Removes a tag type. */
//    val tagTypesRemove: CallableStatement = connection.prepareCall("{call TagTypes_remove(?)}")
//
//    /** Inserts a new song has tag relationship. */
//    val songHasTagPut: CallableStatement = connection.prepareCall("{call SongHasTag_put(?, ?)}")
//
//    /** Removes all song has tag relationships for a song. */
//    val songHasTagRemoveAllForSong: CallableStatement =
//        connection.prepareCall("{call SongHasTag_remove_all_for_song(?)}")
//
//    /** Inserts/updates a data entry. */
//    val dataPut: CallableStatement = connection.prepareCall("{call Data_put(?, ?)}")
//
//    /** Removes a data entry. */
//    val dataRemove: CallableStatement = connection.prepareCall("{call Data_remove(?)}")
}
