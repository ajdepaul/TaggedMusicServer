/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.*
import java.time.LocalDateTime


/** [LibrarySource] stored in memory *for development use only*. */
class InMemoryLibrarySource : LibrarySource {

    private var version = "1.0"

    /** key: user id, value: the user */
    private val users = mutableMapOf<Int, User>()
    /** key: user id, value: song map that maps the song id to the [Song] */
    private val songs = mutableMapOf<Int, MutableMap<Int, Song>>()
    /** key: user id, value: tag map that maps the tag name to the [Tag] */
    private val tags = mutableMapOf<Int, MutableMap<String, Tag>>()
    /** key: user id, value: tag type map that maps the tag type name to the [TagType] */
    private val tagTypes = mutableMapOf<Int, MutableMap<String, TagType>>()
    /** key: user id, value: the data map the maps the data key to the data entry */
    private val dataEntries = mutableMapOf<Int, MutableMap<String, DataEntry>>()

/* ---------------------------------------- Data Classes ---------------------------------------- */

    // These data classes don't include id/key values, so that they can better fit the maps they are
    // stored in.

    private data class User(val username: String, val passHash: String, val admin: Boolean)
    private fun User.toModel(id: Int): UserModel = UserModel(id, username, admin)
    private fun UserModel.toMapValue(passHash: String): User = User(username, passHash, admin)

    private data class Song(
        val fileName: String,
        val title: String,
        val duration: Int,
        val trackNum: Int? = null,
        val releaseDate: LocalDateTime? = null,
        val createDate: LocalDateTime = LocalDateTime.now(),
        val modifyDate: LocalDateTime = LocalDateTime.now(),
        val playCount: Int = 0,
        val tags: Set<String> = setOf()
    )
    private fun Song.toModel(id: Int): SongModel = SongModel(
        id, fileName, title, duration, trackNum, releaseDate, createDate, modifyDate, playCount,
        tags
    )
    private fun SongModel.toMapValue(): Song = Song(
        fileName, title, duration, trackNum, releaseDate, createDate, modifyDate, playCount, tags
    )

    private data class Tag(val type: String? = null, val description: String? = null)
    private fun Tag.toModel(name: String): TagModel = TagModel(name, type, description)
    private fun TagModel.toMapValue(): Tag = Tag(type, description)

    private data class TagType(val color: Int)
    private fun TagType.toModel(name: String): TagTypeModel = TagTypeModel(name, color)
    private fun TagTypeModel.toMapValue(): TagType = TagType(color)

    private data class DataEntry(val value: String)
    private fun DataEntry.toModel(key: String): DataEntryModel = DataEntryModel(key, value)
    private fun DataEntryModel.toMapValue(): DataEntry = DataEntry(value)

/* ----------------------------------------- Retrieving ----------------------------------------- */

    override fun getVersion(): Response<String> = Response(version, Response.Status.SUCCESS)

    override fun getDefaultTagType(userId: Int): Response<TagTypeModel?> =
        Response(tagTypes[userId]?.get("")?.toModel(""), Response.Status.SUCCESS)

    override fun hasSong(userId: Int, songId: Int): Response<Boolean> =
        Response(songs[userId]?.containsKey(songId) ?: false, Response.Status.SUCCESS)

    override fun getSong(userId: Int, songId: Int): Response<SongModel?> =
        Response(songs[userId]?.get(songId)?.toModel(songId), Response.Status.SUCCESS)

    override fun getAllSongs(userId: Int): Response<Set<SongModel>> {
        return Response(
            songs[userId]?.map { it.value.toModel(it.key) }?.toSet() ?: setOf(),
            Response.Status.SUCCESS
        )
    }

    override fun getSongsByTags(
        userId: Int,
        includeTags: Set<String>,
        excludeTags: Set<String>
    ): Response<Set<SongModel>> {
        return Response(
            songs[userId]
                ?.filter { it.value.tags.containsAll(includeTags) }
                ?.filterNot { it.value.tags.any { tag -> excludeTags.contains(tag) } }
                ?.map { it.value.toModel(it.key) }
                ?.toSet() ?: setOf(),
            Response.Status.SUCCESS
        )
    }

    override fun hasTag(userId: Int, tagName: String): Response<Boolean> =
        Response(tags[userId]?.containsKey(tagName) ?: false, Response.Status.SUCCESS)

    override fun getTag(userId: Int, tagName: String): Response<TagModel?> =
        Response(tags[userId]?.get(tagName)?.toModel(tagName), Response.Status.SUCCESS)

    override fun getAllTags(userId: Int): Response<Set<TagModel>> {
        return Response(
            tags[userId]?.map { it.value.toModel(it.key) }?.toSet() ?: setOf(),
            Response.Status.SUCCESS
        )
    }

    override fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean> =
        Response(tagTypes[userId]?.containsKey(tagTypeName) ?: false, Response.Status.SUCCESS)

    override fun getTagType(userId: Int, tagTypeName: String): Response<TagTypeModel?> =
        Response(tagTypes[userId]?.get(tagTypeName)?.toModel(tagTypeName), Response.Status.SUCCESS)

    override fun getAllTagTypes(userId: Int): Response<Set<TagTypeModel>> {
        return Response(
            tagTypes[userId]?.map { it.value.toModel(it.key) }?.toSet() ?: setOf(),
            Response.Status.SUCCESS
        )
    }

    override fun hasData(userId: Int, key: String): Response<Boolean> =
        Response(dataEntries[userId]?.any { it.key == key } ?: false, Response.Status.SUCCESS)

    override fun getData(userId: Int, key: String): Response<DataEntryModel?> =
        Response(dataEntries[userId]?.get(key)?.toModel(key), Response.Status.SUCCESS)

    override fun getAllData(userId: Int): Response<Set<DataEntryModel>> {
        return Response(
            dataEntries[userId]?.map { it.value.toModel(it.key) }?.toSet() ?: setOf(),
            Response.Status.SUCCESS
        )
    }

/* ------------------------------------------ Updating ------------------------------------------ */

    override fun setDefaultTagType(userId: Int, tagType: TagTypeModel): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tagTypes[userId]!![""] = tagType.toMapValue()

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putSong(userId: Int, song: SongModel): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        songs[userId]!![song.id] = song.toMapValue()

        // add new tags
        val tagsToAdd = song.tags.filterNot { tag -> tags[userId]!!.containsKey(tag) }
        tags[userId]!!.putAll(tagsToAdd.associateWith { Tag(null) })

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeSong(userId: Int, songId: Int): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        songs[userId]!!.remove(songId)

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putTag(userId: Int, tag: TagModel): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tags[userId]!![tag.name] = tag.toMapValue()

        // add new tag type
        if (tag.type != null && !tagTypes[userId]!!.keys.contains(tag.type)) {
            tagTypes[userId]!![tag.type] = tagTypes[userId]!![""]!!
        }

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTag(userId: Int, tagName: String): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tags[userId]!!.remove(tagName)

        // remove tag from songs
        for (entry in songs[userId]!!) {
            val song = entry.value

            if (song.tags.contains(tagName)) {
                val newSong = Song(
                    song.fileName,
                    song.title,
                    song.duration,
                    song.trackNum,
                    song.releaseDate,
                    song.createDate,
                    song.modifyDate,
                    song.playCount,
                    song.tags.filterNot { it == tagName }.toSet()
                )

                songs[userId]!![entry.key] = newSong
            }
        }

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putTagType(userId: Int, tagType: TagTypeModel): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tagTypes[userId]!![tagType.name] = tagType.toMapValue()

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTagType(userId: Int, tagTypeName: String): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tagTypes[userId]!!.remove(tagTypeName)

        // remove tag type from tags
        for (entry in tags[userId]!!) {
            val tag = entry.value
            if (tag.type == tagTypeName) {
                tags[userId]!![entry.key] = Tag(null, tag.description)
            }
        }

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putData(userId: Int, dataEntry: DataEntryModel): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        dataEntries[userId]!![dataEntry.key] = dataEntry.toMapValue()

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeData(userId: Int, key: String): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        dataEntries[userId]!!.remove(key)

        return Response(Unit, Response.Status.SUCCESS)
    }

/* -------------------------------------------- Users ------------------------------------------- */

    override fun getUser(userId: Int): Response<UserModel?> =
        Response(users[userId]?.toModel(userId), Response.Status.SUCCESS)

    override fun getUser(username: String): Response<UserModel?> =
        Response(
            users.entries.find { it.value.username == username }.let { it?.value?.toModel(it.key) },
            Response.Status.SUCCESS
        )

    override fun getPassHash(userId: Int): Response<String?> =
        Response(users[userId]?.passHash, Response.Status.SUCCESS)

    override fun getAllUsers(): Response<Set<UserModel>> =
        Response(users.map { it.value.toModel(it.key) }.toSet(), Response.Status.SUCCESS)

    override fun addUser(user: UserModel, passHash: String, defaultTagType: TagTypeModel): Response<Unit> {
        if (userExists(user.id)) return Response(Unit, Response.Status.BAD_REQUEST)

        users[user.id] = user.toMapValue(passHash)
        songs[user.id] = mutableMapOf()
        tags[user.id] = mutableMapOf()
        tagTypes[user.id] = mutableMapOf("" to defaultTagType.toMapValue())
        dataEntries[user.id] = mutableMapOf()

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun updateUserName(userId: Int, username: String): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        val oldUser = users[userId]!!
        users[userId] = User(username, oldUser.passHash, oldUser.admin)

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun updatePassword(userId: Int, passHash: String): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        val oldUser = users[userId]!!
        users[userId] = User(oldUser.username, passHash, oldUser.admin)

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun updatePrivileges(userId: Int, admin: Boolean): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        val oldUser = users[userId]!!
        users[userId] = User(oldUser.username, oldUser.passHash, admin)

        return Response(Unit, Response.Status.SUCCESS)
    }
    override fun removeUser(userId: Int): Response<Unit> {
        if (!userExists(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        users.remove(userId)
        songs.remove(userId)
        tags.remove(userId)
        tagTypes.remove(userId)
        dataEntries.remove(userId)

        return Response(Unit, Response.Status.SUCCESS)
    }

/* -------------------------------------------- Util -------------------------------------------- */

    private fun userExists(id: Int): Boolean = users.containsKey(id)
}
