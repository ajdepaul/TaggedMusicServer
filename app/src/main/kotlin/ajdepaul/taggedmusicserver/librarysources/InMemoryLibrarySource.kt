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

class InMemoryLibrarySource : LibrarySource {

    private data class Key(val userId: Int, val str: String)

    private var version = "1.0"

    private val users = mutableSetOf<User>()
    private val songs = mutableMapOf<Key, Song>()
    private val tags = mutableMapOf<Key, Tag>()
    private val tagTypes = mutableMapOf<Key, TagType>()
    private val data = mutableMapOf<Key, String>()

/* ----------------------------------------- Retrieving ----------------------------------------- */

    override fun getVersion(): Response<String> = Response(version, Response.Status.SUCCESS)

    override fun getDefaultTagType(userId: Int): Response<TagType?> =
        Response(tagTypes[Key(userId, "")], Response.Status.SUCCESS)

    override fun hasSong(userId: Int, fileName: String): Response<Boolean> =
        Response(songs.contains(Key(userId, fileName)), Response.Status.SUCCESS)

    override fun getSong(userId: Int, fileName: String): Response<Song?> =
        Response(songs[Key(userId, fileName)], Response.Status.SUCCESS)

    override fun getAllSongs(userId: Int): Response<Map<String, Song>> =
        Response(
            songs.filter { it.key.userId == userId }.mapKeys { it.key.str },
            Response.Status.SUCCESS
        )

    override fun getSongsByTags(
        userId: Int,
        includeTags: Set<String>,
        excludeTags: Set<String>
    ): Response<Map<String, Song>> =
        Response(
            songs.filter { it.key.userId == userId }.mapKeys { it.key.str }
                .filterByTags(includeTags, excludeTags),
            Response.Status.SUCCESS
        )

    override fun hasTag(userId: Int, tagName: String): Response<Boolean> =
        Response(tags.contains(Key(userId, tagName)), Response.Status.SUCCESS)

    override fun getTag(userId: Int, tagName: String): Response<Tag?> =
        Response(tags[Key(userId, tagName)], Response.Status.SUCCESS)

    override fun getAllTags(userId: Int): Response<Map<String, Tag>> =
        Response(
            tags.filter { it.key.userId == userId }.mapKeys { it.key.str },
            Response.Status.SUCCESS
        )

    override fun hasTagType(userId: Int, tagTypeName: String): Response<Boolean> =
        Response(tagTypes.contains(Key(userId, tagTypeName)), Response.Status.SUCCESS)

    override fun getTagType(userId: Int, tagTypeName: String): Response<TagType?> =
        Response(tagTypes[Key(userId, tagTypeName)], Response.Status.SUCCESS)

    override fun getAllTagTypes(userId: Int): Response<Map<String, TagType>> =
        Response(
            tagTypes.filter { it.key.userId == userId }.mapKeys { it.key.str },
            Response.Status.SUCCESS
        )

    override fun hasData(userId: Int, key: String): Response<Boolean> =
        Response(data.contains(Key(userId, key)), Response.Status.SUCCESS)

    override fun getData(userId: Int, key: String): Response<String?> =
        Response(data[Key(userId, key)], Response.Status.SUCCESS)

    override fun getAllData(userId: Int): Response<Map<String, String>> =
        Response(
            data.filter { it.key.userId == userId}.mapKeys { it.key.str },
            Response.Status.SUCCESS
        )

/* ------------------------------------------ Updating ------------------------------------------ */

    override fun setDefaultTagType(userId: Int, tagType: TagType): Response<Unit> {
        tagTypes[Key(userId, "")] = tagType
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putSong(userId: Int, fileName: String, song: Song): Response<Unit> {
        songs[Key(userId, fileName)] = song

        val usersTags = tags.filterKeys { it.userId == userId }.mapKeys { it.key.str }
        val tagsToAdd = song.tags.filterNot { tag -> usersTags.containsKey(tag) }

        tags.putAll(tagsToAdd.associate { Key(userId, it) to Tag(null) })

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeSong(userId: Int, fileName: String): Response<Unit> {
        songs.remove(Key(userId, fileName))
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putTag(userId: Int, tagName: String, tag: Tag): Response<Unit> {
        tags[Key(userId, tagName)] = tag

        if (tag.type != null && tagTypes.none { it.key.userId == userId && it.key.str == tagName }) {
            val defaultTagType = tagTypes[Key(userId, "")]
            if (defaultTagType != null) {
                tagTypes[Key(userId, tag.type)] = defaultTagType
            }
        }

        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTag(userId: Int, tagName: String): Response<Unit> {
        tags.remove(Key(userId, tagName))
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putTagType(userId: Int, tagTypeName: String, tagType: TagType): Response<Unit> {
        tagTypes[Key(userId, tagTypeName)] = tagType
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeTagType(userId: Int, tagTypeName: String): Response<Unit> {
        tagTypes.remove(Key(userId, tagTypeName))
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun putData(userId: Int, key: String, value: String): Response<Unit> {
        data[Key(userId, key)] = value
        return Response(Unit, Response.Status.SUCCESS)
    }

    override fun removeData(userId: Int, key: String, value: String): Response<Unit> {
        data.remove(Key(userId, key))
        return Response(Unit, Response.Status.SUCCESS)
    }

/* -------------------------------------------- Users ------------------------------------------- */

    override fun getUser(userId: Int): Response<User?> =
        Response(users.find { it.id == userId }, Response.Status.SUCCESS)

    override fun getUser(username: String): Response<User?> =
        Response(users.find { it.username == username }, Response.Status.SUCCESS)

    override fun getAllUsers(): Response<Set<User>> = Response(users.toSet(), Response.Status.SUCCESS)

    override fun addUser(user: User, defaultTagType: TagType): Response<Unit> {
        users.add(user)
        tagTypes[Key(user.id, "")] = defaultTagType
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
        users.remove(User(userId, "", "", ""))

        fun <E> MutableMap<Key, E>.removeAllForUser(userId: Int) {
            val toRemove = this.filterKeys { it.userId == userId }.keys
            for (k in toRemove) this.remove(k)
        }

        songs.removeAllForUser(userId)
        tags.removeAllForUser(userId)
        tagTypes.removeAllForUser(userId)
        data.removeAllForUser(userId)

        return Response(Unit, Response.Status.SUCCESS)
    }
}
