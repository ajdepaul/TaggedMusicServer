/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType

class TestLibrarySource : LibrarySource<Unit> {

    private var version = "1.0"
    private var defaultTagType = TagType(0)

    private val songs = mutableMapOf<String, Song>()
    private val tags = mutableMapOf<String, Tag>()
    private val tagTypes = mutableMapOf<String, TagType>()
    private val data = mutableMapOf<String, String>()

/* ----------------------------------------- Retrieving ----------------------------------------- */

    override fun getVersion(): Response<String, Unit?> = Response(version, null)

    override fun getDefaultTagType(): Response<TagType, Unit?> = Response(defaultTagType, null)

    override fun hasSong(fileName: String): Response<Boolean, Unit?> =
        Response(songs.contains(fileName), null)

    override fun getSong(fileName: String): Response<Song?, Unit?> = Response(songs[fileName], null)

    override fun getAllSongs(): Response<Map<String, Song>, Unit?> = Response(songs, null)

    override fun getSongsByTags(
        includeTags: Set<String>,
        excludeTags: Set<String>
    ): Response<Map<String, Song>, Unit?> {
        TODO("Not yet implemented")
    }

    override fun hasTag(tagName: String): Response<Boolean, Unit?> =
        Response(tags.contains(tagName), null)

    override fun getTag(tagName: String): Response<Tag?, Unit?> = Response(tags[tagName], null)

    override fun getAllTags(): Response<Map<String, Tag>, Unit?> = Response(tags, null)

    override fun hasTagType(tagTypeName: String): Response<Boolean, Unit?> =
        Response(tagTypes.contains(tagTypeName), null)

    override fun getTagType(tagTypeName: String): Response<TagType?, Unit?> =
        Response(tagTypes[tagTypeName], null)

    override fun getAllTagTypes(): Response<Map<String, TagType>, Unit?> = Response(tagTypes, null)

    override fun hasData(key: String): Response<Boolean, Unit?> = Response(data.contains(key), null)

    override fun getData(key: String): Response<String?, Unit?> = Response(data[key], null)

    override fun getAllData(): Response<Map<String, String>, Unit?> = Response(data, null)

/* ------------------------------------------ Updating ------------------------------------------ */

    override fun setDefaultTagType(tagType: TagType): Response<TagType, Unit?> {
        val prevVal = defaultTagType
        defaultTagType = tagType
        return Response(prevVal, null)
    }

    override fun putSong(fileName: String, song: Song): Response<Song?, Unit?> {
        val prevVal = songs[fileName]
        songs[fileName] = song
        return Response(prevVal, null)
    }

    override fun removeSong(fileName: String): Response<Song?, Unit?> {
        val prevVal = songs[fileName]
        songs.remove(fileName)
        return Response(prevVal, null)
    }

    override fun putTag(tagName: String, tag: Tag): Response<Tag?, Unit?> {
        val prevVal = tags[tagName]
        tags[tagName] = tag
        return Response(prevVal, null)
    }

    override fun removeTag(tagName: String): Response<Tag?, Unit?> {
        val prevVal = tags[tagName]
        tags.remove(tagName)
        return Response(prevVal, null)
    }

    override fun putTagType(tagTypeName: String, tagType: TagType): Response<TagType?, Unit?> {
        val prevVal = tagTypes[tagTypeName]
        tagTypes[tagTypeName] = tagType
        return Response(prevVal, null)
    }

    override fun removeTagType(tagTypeName: String): Response<TagType?, Unit?> {
        val prevVal = tagTypes[tagTypeName]
        tagTypes.remove(tagTypeName)
        return Response(prevVal, null)
    }

    override fun putData(key: String, value: String): Response<String?, Unit?> {
        val prevVal = data[key]
        data[key] = value
        return Response(prevVal, null)
    }

    override fun removeData(key: String, value: String): Response<String?, Unit?> {
        val prevVal = data[key]
        data.remove(key)
        return Response(prevVal, null)
    }
}
