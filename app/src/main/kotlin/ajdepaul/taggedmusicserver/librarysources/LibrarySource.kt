/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

/** The [LIBRARY_VERSION] indicates what format and features are expected. */
const val LIBRARY_VERSION = "1.0"

/**
 * Used to retrieve data about [User]s' libraries.
 *
 * Retrieval requests made with an invalid id will return a [Response] with
 * [SUCCESS][Response.Status.SUCCESS] [status][Response.Status] and either a null or empty
 * [result][Response.result].
 *
 * Updating requests made with an invalid [User] id will return a [Response] with
 * [BAD_REQUEST][Response.Status.BAD_REQUEST] [status][Response.Status].
 *
 * Removal requests with a valid [User] id, but invalid data id (e.g. song id, tag name) will return
 * a [Response] with [SUCCESS][Response.Status.SUCCESS] [status][Response.Status].
 */
interface LibrarySource {

/* ----------------------------------------- Retrieving ----------------------------------------- */

    /** Retrieve the [LIBRARY_VERSION] of this [LibrarySource]. */
    fun getVersion(): Response<String>

    /**
     * Retrieves the [TagType] to use when a [Tag] has no [TagType].
     * @param userId specifies which [User]'s library to use
     */
    fun getDefaultTagType(userId: Int): Response<TagType?>

    /**
     * Checks if there is a [Song] with the id [songId].
     * @param userId specifies which [User]'s library to use
     */
    fun hasSong(userId: Int, songId: Int): Response<Boolean>

    /**
     * Retrieves the [Song] with the id [songId].
     * @param userId specifies which [User]'s library to use
     */
    fun getSong(userId: Int, songId: Int): Response<Song?>

    /**
     * Retrieves a map of all [Song]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllSongs(userId: Int): Response<Map<Int, Song>>

    /**
     * Retrieves a map of [Song]s that satisfy the provided filters.
     * @param userId specifies which [User]'s library to use
     * @param includeTags [Song]s must have all of these [Tag]s (if empty, includes all tags)
     * @param excludeTags [Song]s cannot have any of these [Tag]s (if empty, excludes no tags)
     */
    fun getSongsByTags(
        userId: Int,
        includeTags: Set<String> = setOf(),
        excludeTags: Set<String> = setOf()
    ): Response<Map<Int, Song>>

    /**
     * Checks if there is a [Tag] with the id [tagName].
     * @param userId specifies which [User]'s library to use
     */
    fun hasTag(userId: Int, tagName: String): Response<Boolean>

    /**
     * Retrieves the [Tag] with the id [tagName].
     * @param userId specifies which [User]'s library to use
     */
    fun getTag(userId: Int, tagName: String): Response<Tag?>

    /**
     * Retrieves a map of all [Tag]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllTags(userId: Int): Response<Map<String, Tag>>

    /**
     * Checks if there is a [TagType] with the id [tagTypeName].
     * @param userId specifies which [User]'s library to use
     */
    fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean>

    /**
     * Retrieves the [TagType] with the id [tagTypeName].
     * @param userId specifies which [User]'s library to use
     */
    fun getTagType(userId: Int, tagTypeName: String): Response<TagType?>

    /**
     * Retrieves a map of all [TagType]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllTagTypes(userId: Int): Response<Map<String, TagType>>

    /**
     * Checks if there is a data entry with the id [key].
     * @param userId specifies which [User]'s library to use
     */
    fun hasData(userId: Int, key: String): Response<Boolean>

    /**
     * Retrieves the data entry with the id [key].
     * @param userId specifies which [User]'s library to use
     */
    fun getData(userId: Int, key: String): Response<String?>

    /**
     * Retrieves a map of all data entries in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllData(userId: Int): Response<Map<String, String>>

/* ------------------------------------------ Updating ------------------------------------------ */

    /**
     * Sets the [TagType] to use when a [Tag] has no [TagType].
     * @param userId specifies which [User]'s library to use
     */
    fun setDefaultTagType(userId: Int, tagType: TagType): Response<Unit>

    /**
     * Adds or updates the [Song] with id [songId]. Any new [Tag]s in the
     * [song's tags][Song.tags] are added to the library's total [Tag]s.
     * @param userId specifies which [User]'s library to use
     */
    fun putSong(userId: Int, songId: Int, song: Song): Response<Unit>

    /**
     * Removes the [Song] with id [songId].
     * @param userId specifies which [User]'s library to use
     */
    fun removeSong(userId: Int, songId: Int): Response<Unit>

    /**
     * Adds or updates the [Tag] with id [tagName]. If the [tag's tag type][Tag.type] is new, it is
     * added to the library's total [TagType]s.
     * @param userId specifies which [User]'s library to use
     */
    fun putTag(userId: Int, tagName: String, tag: Tag): Response<Unit>

    /**
     * Removes the [Tag] with id [tagName]. Any [Song]s that have this [Tag] will have it removed.
     * @param userId specifies which [User]'s library to use
     */
    fun removeTag(userId: Int, tagName: String): Response<Unit>

    /**
     * Adds or updates the [TagType] with id [tagTypeName].
     * @param userId specifies which [User]'s library to use
     */
    fun putTagType(userId: Int, tagTypeName: String, tagType: TagType): Response<Unit>

    /**
     * Removes the [TagType] with id [tagTypeName]. Any [Tag]s that have this tag type will have
     * their [TagType] set to null.
     * @param userId specifies which [User]'s library to use
     */
    fun removeTagType(userId: Int, tagTypeName: String): Response<Unit>

    /**
     * Adds or updates the data entry with id [key].
     * @param userId specifies which [User]'s library to use
     */
    fun putData(userId: Int, key: String, value: String): Response<Unit>

    /**
     * Removes the data entry with id [key].
     * @param userId specifies which [User]'s library to use
     */
    fun removeData(userId: Int, key: String): Response<Unit>

/* -------------------------------------------- Users ------------------------------------------- */

    /** Retrieves the [User] with id [userId]. */
    fun getUser(userId: Int): Response<User?>

    /** Retrieves the [User] with username [username]. */
    fun getUser(username: String): Response<Map.Entry<Int, User>?>

    /** Retrieves a map of all [User] information. */
    fun getAllUsers(): Response<Map<Int, User>>

    /** Retrieves a [User]'s password hash with id [userId]. */
    fun getPassHash(userId: Int): Response<String?>

    /**
     * Adds a new user. [defaultTagType] is used as the [TagType] when a [Tag] has no [TagType].
     * @return [Response] containing the id of the new [User]
     */
    fun addUser(user: User, defaultTagType: TagType): Response<Int>

    /** Updates the username for the [User] with id [userId]. */
    fun updateUserName(userId: Int, username: String): Response<Unit>

    /** Updates the password for the [User] with id [userId]. */
    fun updatePassword(userId: Int, passHash: String): Response<Unit>

    /** Updates the admin privileges for the [User] with id [userId]. */
    fun updatePrivileges(userId: Int, admin: Boolean): Response<Unit>

    /** Removes all library data for the [User] with id [userId]. */
    fun removeUser(userId: Int): Response<Unit>
}

data class Response<R>(val result: R, val status: Status) {
    enum class Status { SUCCESS, BAD_REQUEST, CONNECTION_ISSUE, TIME_OUT }
}
