/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.filterByTags
import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType
import ajdepaul.taggedmusicserver.models.User

/** [LibrarySource] stored in memory *for development use only*. */
class InMemoryLibrarySource : LibrarySource {

    private var version = "1.0"

    private val users = mutableSetOf<User>()
    private fun MutableSet<User>.containsUser(userId: Int) = this.contains(User(userId, "", "", ""))

    /** key: user id, value: song map that maps the file name to the [Song] */
    private val songs = mutableMapOf<Int, MutableMap<String, Song>>()
    /** key: user id, value: tag map that maps the tag name to the [Tag] */
    private val tags = mutableMapOf<Int, MutableMap<String, Tag>>()
    /** key: user id, value: tag type map that maps the tag type name to the [TagType] */
    private val tagTypes = mutableMapOf<Int, MutableMap<String, TagType>>()
    /** key: user id, value: the data map the maps the data key to the data string */
    private val data = mutableMapOf<Int, MutableMap<String, String>>()

/* ----------------------------------------- Retrieving ----------------------------------------- */

    override fun getVersion(): Response<String> = Response(version, Response.Status.SUCCESS)

    override fun getDefaultTagType(userId: Int): Response<TagType?> =
        Response(tagTypes[userId]?.get(""), Response.Status.SUCCESS)

    override fun hasSong(userId: Int, fileName: String): Response<Boolean> =
        Response(songs[userId]?.contains(fileName) ?: false, Response.Status.SUCCESS)

    override fun getSong(userId: Int, fileName: String): Response<Song?> =
        Response(songs[userId]?.get(fileName), Response.Status.SUCCESS)

    override fun getAllSongs(userId: Int): Response<Map<String, Song>> =
        Response(songs[userId]?.toMap() ?: mapOf(), Response.Status.SUCCESS)

    override fun getSongsByTags(
        userId: Int,
        includeTags: Set<String>,
        excludeTags: Set<String>
    ): Response<Map<String, Song>> =
        Response(
            songs[userId]?.filterByTags(includeTags, excludeTags) ?: mapOf(),
            Response.Status.SUCCESS
        )

    override fun hasTag(userId: Int, tagName: String): Response<Boolean> =
        Response(tags[userId]?.contains(tagName) ?: false, Response.Status.SUCCESS)

    override fun getTag(userId: Int, tagName: String): Response<Tag?> =
        Response(tags[userId]?.get(tagName), Response.Status.SUCCESS)

    override fun getAllTags(userId: Int): Response<Map<String, Tag>> =
        Response(tags[userId] ?: mapOf(), Response.Status.SUCCESS)

    override fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean> =
        Response(tagTypes[userId]?.contains(tagTypeName) ?: false, Response.Status.SUCCESS)

    override fun getTagType(userId: Int, tagTypeName: String): Response<TagType?> =
        Response(tagTypes[userId]?.get(tagTypeName), Response.Status.SUCCESS)

    override fun getAllTagTypes(userId: Int): Response<Map<String, TagType>> =
        Response(tagTypes[userId] ?: mapOf(), Response.Status.SUCCESS)

    override fun hasData(userId: Int, key: String): Response<Boolean> =
        Response(data[userId]?.contains(key) ?: false, Response.Status.SUCCESS)

    override fun getData(userId: Int, key: String): Response<String?> =
        Response(data[userId]?.get(key), Response.Status.SUCCESS)

    override fun getAllData(userId: Int): Response<Map<String, String>> =
        Response(data[userId] ?: mapOf(), Response.Status.SUCCESS)

/* ------------------------------------------ Updating ------------------------------------------ */

    override fun setDefaultTagType(userId: Int, tagType: TagType): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)
        tagTypes[userId]!![""] = tagType
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putSong(userId: Int, fileName: String, song: Song): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        songs[userId]!![fileName] = song

        // add new tags
        val tagsToAdd = song.tags.filterNot { tag -> tags[userId]!!.containsKey(tag) }
        tags[userId]!!.putAll(tagsToAdd.associateWith { Tag(null) })

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeSong(userId: Int, fileName: String): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)
        songs[userId]!!.remove(fileName)
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putTag(userId: Int, tagName: String, tag: Tag): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tags[userId]!![tagName] = tag

        // add new tag type
        if (tag.type != null && !tagTypes[userId]!!.keys.contains(tag.type)) {
            tagTypes[userId]!![tag.type] = tagTypes[userId]!![""]!!
        }

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTag(userId: Int, tagName: String): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        tags[userId]!!.remove(tagName)

        // remove tag from songs
        for (entry in songs[userId]!!) {
            val song = entry.value

            if (song.tags.contains(tagName)) {
                val newSong = Song(
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

    override fun putTagType(userId: Int, tagTypeName: String, tagType: TagType): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)
        tagTypes[userId]!![tagTypeName] = tagType
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTagType(userId: Int, tagTypeName: String): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

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

    override fun putData(userId: Int, key: String, value: String): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)
        data[userId]!![key] = value
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeData(userId: Int, key: String): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)
        data[userId]!!.remove(key)
        return Response(Unit, Response.Status.SUCCESS)
    }

/* -------------------------------------------- Users ------------------------------------------- */

    override fun getUser(userId: Int): Response<User?> =
        Response(users.find { it.id == userId }, Response.Status.SUCCESS)

    override fun getUser(username: String): Response<User?> =
        Response(users.find { it.username == username }, Response.Status.SUCCESS)

    override fun getAllUsers(): Response<Set<User>> = Response(users.toSet(), Response.Status.SUCCESS)

    override fun addUser(user: User, defaultTagType: TagType): Response<Unit> {
        if (users.containsUser(user.id)) return Response(Unit, Response.Status.BAD_REQUEST)

        users.add(user)
        songs[user.id] = mutableMapOf()
        tags[user.id] = mutableMapOf()
        tagTypes[user.id] = mutableMapOf("" to defaultTagType)
        data[user.id] = mutableMapOf()

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun updateUserName(userId: Int, username: String): Response<Unit> {
        val user = users.find { it.id == userId } ?: return Response(Unit, Response.Status.BAD_REQUEST)

        users.remove(user)
        users.add(User(userId, username, user.salt, user.passHash))

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun updatePassword(userId: Int, salt: String, passHash: String): Response<Unit> {
        val user = users.find { it.id == userId } ?: return Response(Unit, Response.Status.BAD_REQUEST)

        users.remove(user)
        users.add(User(userId, user.username, salt, passHash))

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeUser(userId: Int): Response<Unit> {
        if (!users.containsUser(userId)) return Response(Unit, Response.Status.BAD_REQUEST)

        users.remove(User(userId, "", "", ""))
        songs.remove(userId)
        tags.remove(userId)
        tagTypes.remove(userId)
        data.remove(userId)

        return Response(Unit, Response.Status.SUCCESS)
    }
}
