/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType

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
     * Retrieves the [TagType] to use when a [Tag] has no [TagType].
     * @param userId specifies which [User]'s library to use
     */
    fun getDefaultTagType(userId: Int): Response<TagType?>

    /**
     * Checks if [fileName] is a [Song] that exists in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun hasSong(userId: Int, fileName: String): Response<Boolean>

    /**
     * Retrieves the [Song] that corresponds to the key [fileName].
     * @param userId specifies which [User]'s library to use
     */
    fun getSong(userId: Int, fileName: String): Response<Song?>

    /**
     * Retrieves all [Song]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllSongs(userId: Int): Response<Map<String, Song>>

    /**
     * Retrieves a map of songs according to the provided filters.
     * @param userId specifies which [User]'s library to use
     * @param includeTags songs must have all of these tags (if empty, includes all tags)
     * @param excludeTags songs cannot have any of these tags (if empty, excludes no tags)
     */
    fun getSongsByTags(
        userId: Int,
        includeTags: Set<String> = setOf(),
        excludeTags: Set<String> = setOf()
    ): Response<Map<String, Song>>

    /**
     * Checks if [tagName] is a [Tag] that exists in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun hasTag(userId: Int, tagName: String): Response<Boolean>

    /**
     * Retrieves the [Tag] that corresponds to the key [tagName].
     * @param userId specifies which [User]'s library to use
     */
    fun getTag(userId: Int, tagName: String): Response<Tag?>

    /**
     * Retrieves all [Tag]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllTags(userId: Int): Response<Map<String, Tag>>

    /**
     * Checks if [tagTypeName] is a [TagType] that exists in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean>

    /**
     * Retrieves the [TagType] that corresponds to the key [tagTypeName].
     * @param userId specifies which [User]'s library to use
     */
    fun getTagType(userId: Int, tagTypeName: String): Response<TagType?>

    /**
     * Retrieves all [TagType]s in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun getAllTagTypes(userId: Int): Response<Map<String, TagType>>

    /**
     * Checks if [key] is a data entry that exists in the library.
     * @param userId specifies which [User]'s library to use
     */
    fun hasData(userId: Int, key: String): Response<Boolean>

    /**
     * Retrieves the data entry that corresponds to [key].
     * @param userId specifies which [User]'s library to use
     */
    fun getData(userId: Int, key: String): Response<String?>

    /**
     * Retrieves all data entries in the library.
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
     * Adds or updates [song] to the library and adds any new [Tag]s in [song]'s [Tag]s to the [Tag]
     * map.
     * @param userId specifies which [User]'s library to use
     */
    fun putSong(userId: Int, fileName: String, song: Song): Response<Unit>

    /**
     * Removes a [Song] from the library.
     * @param userId specifies which [User]'s library to use
     */
    fun removeSong(userId: Int, fileName: String): Response<Unit>

    /**
     * Adds or updates [tag] to the library. If [tag]'s [TagType] is new, it is added to the
     * library.
     * @param userId specifies which [User]'s library to use
     */
    fun putTag(userId: Int, tagName: String, tag: Tag): Response<Unit>

    /**
     * Removes a [Tag] from the library. Any [Song]s in the library that have this [Tag] will have
     * the [Tag] removed.
     * @param userId specifies which [User]'s library to use
     */
    fun removeTag(userId: Int, tagName: String): Response<Unit>

    /**
     * Adds or updates [tagType] to the library.
     * @param userId specifies which [User]'s library to use
     */
    fun putTagType(userId: Int, tagTypeName: String, tagType: TagType): Response<Unit>

    /**
     * Removes a [TagType] from the library. Any [Tag]s in the library that have this [TagType] will
     * have their [TagType]s set to null.
     * @param userId specifies which [User]'s library to use
     */
    fun removeTagType(userId: Int, tagTypeName: String): Response<Unit>

    /**
     * Adds or updates a data entry to the library.
     * @param userId specifies which [User]'s library to use
     */
    fun putData(userId: Int, key: String, value: String): Response<Unit>

    /**
     * Removes a data entry from the library.
     * @param userId specifies which [User]'s library to use
     */
    fun removeData(userId: Int, key: String): Response<Unit>

/* -------------------------------------------- Users ------------------------------------------- */

    /** Retrieves credentials for a [User]. */
    fun getUser(userId: Int): Response<User?>

    /** Retrieves credentials for a [User]. */
    fun getUser(username: String): Response<User?>

    /** Retrieves credentials for all [User]s. */
    fun getAllUsers(): Response<Set<User>>

    /** Adds a new [User]. */
    fun addUser(user: User, defaultTagType: TagType): Response<Unit>

    /** Updates the username for a [User]. */
    fun updateUserName(userId: Int, username: String): Response<Unit>

    /** Updates the password salt and password hash for a [User]. */
    fun updatePassword(userId: Int, salt: String, passHash: String): Response<Unit>

    /** Removes all library data for a [User]. */
    fun removeUser(userId: Int): Response<Unit>
}

/**
 * Returns the result of removing all the entries in this map that do not match the given filters.
 * @param includeTags songs must have all of these tags (if empty, includes all tags)
 * @param excludeTags songs cannot have any of these tags (if empty, excludes no tags)
 * @return a new [PersistentMap] containing only the entries from [this] that match the given
 * filters.
 */
fun Map<String, Song>.filterByTags(
    includeTags: Set<String> = setOf(),
    excludeTags: Set<String> = setOf()
): Map<String, Song> {
    return this.filter { it.value.tags.containsAll(includeTags) }
        .filterNot { it.value.tags.any { tag -> excludeTags.contains(tag) } }
}
