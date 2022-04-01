/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.*

/** The [LIBRARY_VERSION] indicates what format and features is expected. */
const val LIBRARY_VERSION = "1.0"

/**
 * Used to retrieve data about users' libraries. Requests made with an invalid user id, may respond
 * with either in either a [Response.Status.BAD_REQUEST] or a [Response.Status.SUCCESS] with a null
 * or empty result.
 */
interface LibrarySource {

/* ----------------------------------------- Retrieving ----------------------------------------- */

    /** Retrieve the [LIBRARY_VERSION] of this [LibrarySource]. */
    fun getVersion(): Response<String>

    /**
     * Retrieves the tag type to use when a tag has no tag type.
     * @param userId specifies which user's library to use
     */
    fun getDefaultTagType(userId: Int): Response<TagTypeModel?>

    /**
     * Checks if there is a song in the library with the id [songId].
     * @param userId specifies which user's library to use
     */
    fun hasSong(userId: Int, songId: Int): Response<Boolean>

    /**
     * Retrieves the song that corresponds to the key [songId].
     * @param userId specifies which user's library to use
     */
    fun getSong(userId: Int, songId: Int): Response<SongModel?>

    /**
     * Retrieves all songs in the library.
     * @param userId specifies which user's library to use
     */
    fun getAllSongs(userId: Int): Response<Set<SongModel>>

    /**
     * Retrieves a set of songs that satisfy the provided filters.
     * @param userId specifies which user's library to use
     * @param includeTags songs must have all of these tags (if empty, includes all tags)
     * @param excludeTags songs cannot have any of these tags (if empty, excludes no tags)
     */
    fun getSongsByTags(
        userId: Int,
        includeTags: Set<String> = setOf(),
        excludeTags: Set<String> = setOf()
    ): Response<Set<SongModel>>

    /**
     * Checks if there is a tag in the library with the id [tagName].
     * @param userId specifies which user's library to use
     */
    fun hasTag(userId: Int, tagName: String): Response<Boolean>

    /**
     * Retrieves the tag that corresponds to the key [tagName].
     * @param userId specifies which user's library to use
     */
    fun getTag(userId: Int, tagName: String): Response<TagModel?>

    /**
     * Retrieves all tags in the library.
     * @param userId specifies which user's library to use
     */
    fun getAllTags(userId: Int): Response<Set<TagModel>>

    /**
     * Checks if there is a tag type in the library with the key [tagTypeName].
     * @param userId specifies which user's library to use
     */
    fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean>

    /**
     * Retrieves the tag type that corresponds to the key [tagTypeName].
     * @param userId specifies which user's library to use
     */
    fun getTagType(userId: Int, tagTypeName: String): Response<TagTypeModel?>

    /**
     * Retrieves all tag types in the library.
     * @param userId specifies which user's library to use
     */
    fun getAllTagTypes(userId: Int): Response<Set<TagTypeModel>>

    /**
     * Checks if there is a data entry in the library with the key [key].
     * @param userId specifies which user's library to use
     */
    fun hasData(userId: Int, key: String): Response<Boolean>

    /**
     * Retrieves the data entry that corresponds to the key [key].
     * @param userId specifies which user's library to use
     */
    fun getData(userId: Int, key: String): Response<DataEntryModel?>

    /**
     * Retrieves all data entries in the library.
     * @param userId specifies which user's library to use
     */
    fun getAllData(userId: Int): Response<Set<DataEntryModel>>

/* ------------------------------------------ Updating ------------------------------------------ */

    /**
     * Sets the tag type to use when a tag has no tag type. [tagType]'s name is ignored.
     * @param userId specifies which user's library to use
     */
    fun setDefaultTagType(userId: Int, tagType: TagTypeModel): Response<Unit>

    /**
     * Adds or updates [song] to the library and adds any new tags in the song's tags to the
     * library's total tags.
     * @param userId specifies which user's library to use
     */
    fun putSong(userId: Int, song: SongModel): Response<Unit>

    /**
     * Removes a song from the library.
     * @param userId specifies which user's library to use
     */
    fun removeSong(userId: Int, songId: Int): Response<Unit>

    /**
     * Adds or updates [tag] to the library. If [tag]'s tag type is new, it is added to the
     * library's total tag types.
     * @param userId specifies which user's library to use
     */
    fun putTag(userId: Int, tag: TagModel): Response<Unit>

    /**
     * Removes a tag from the library. Any songs in the library that have this tag will have the tag
     * removed.
     * @param userId specifies which user's library to use
     */
    fun removeTag(userId: Int, tagName: String): Response<Unit>

    /**
     * Adds or updates [tagType] to the library.
     * @param userId specifies which user's library to use
     */
    fun putTagType(userId: Int, tagType: TagTypeModel): Response<Unit>

    /**
     * Removes a tag type from the library. Any tags in the library that have this tag type will
     * have their tag type set to null.
     * @param userId specifies which user's library to use
     */
    fun removeTagType(userId: Int, tagTypeName: String): Response<Unit>

    /**
     * Adds or updates a data entry to the library.
     * @param userId specifies which user's library to use
     */
    fun putData(userId: Int, dataEntry: DataEntryModel): Response<Unit>

    /**
     * Removes a data entry from the library.
     * @param userId specifies which user's library to use
     */
    fun removeData(userId: Int, key: String): Response<Unit>

/* -------------------------------------------- Users ------------------------------------------- */

    /** Retrieves user info from [userId]. */
    fun getUser(userId: Int): Response<UserModel?>

    /** Retrieves user info from [username]. */
    fun getUser(username: String): Response<UserModel?>

    /** Retrieves credentials for all users. */
    fun getAllUsers(): Response<Set<UserModel>>

    /** Retrieves a user's password hash from [userId]. */
    fun getPassHash(userId: Int): Response<String?>

    /** Adds a new user. [defaultTagType]'s name is ignored. */
    fun addUser(user: UserModel, passHash: String, defaultTagType: TagTypeModel): Response<Unit>

    /** Updates a user's username. */
    fun updateUserName(userId: Int, username: String): Response<Unit>

    /** Updates a user's password hash. */
    fun updatePassword(userId: Int, passHash: String): Response<Unit>

    /** Updates a user's privileges. */
    fun updatePrivileges(userId: Int, admin: Boolean): Response<Unit>

    /** Removes all library data for a user. */
    fun removeUser(userId: Int): Response<Unit>
}

data class Response<R>(val result: R, val status: Status) {
    enum class Status { SUCCESS, BAD_REQUEST, CONNECTION_ISSUE, TIME_OUT }
}
