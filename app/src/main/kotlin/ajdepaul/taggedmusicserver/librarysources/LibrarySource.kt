/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.Tag
import ajdepaul.taggedmusicserver.models.TagType

interface LibrarySource<E> {

    /* ----------------------------------------- Retrieving ----------------------------------------- */

    fun getVersion(): Response<String, E?>

    fun getDefaultTagType(): Response<TagType, E?>

    fun hasSong(fileName: String): Response<Boolean, E?>

    fun getSong(fileName: String): Response<Song?, E?>

    fun getAllSongs(): Response<Map<String, Song>, E?>

    fun getSongsByTags(
        includeTags: Set<String> = setOf(),
        excludeTags: Set<String> = setOf()
    ): Response<Map<String, Song>, E?>

    fun hasTag(tagName: String): Response<Boolean, E?>

    fun getTag(tagName: String): Response<Tag?, E?>

    fun getAllTags(): Response<Map<String, Tag>, E?>

    fun hasTagType(tagTypeName: String): Response<Boolean, E?>

    fun getTagType(tagTypeName: String): Response<TagType?, E?>

    fun getAllTagTypes(): Response<Map<String, TagType>, E?>

    fun hasData(key: String): Response<Boolean, E?>

    fun getData(key: String): Response<String?, E?>

    fun getAllData(): Response<Map<String, String>, E?>

/* ------------------------------------------ Updating ------------------------------------------ */

    fun setDefaultTagType(tagType: TagType): Response<TagType, E?>

    fun putSong(fileName: String, song: Song): Response<Song?, E?>

    fun removeSong(fileName: String): Response<Song?, E?>

    fun putTag(tagName: String, tag: Tag): Response<Tag?, E?>

    fun removeTag(tagName: String): Response<Tag?, E?>

    fun putTagType(tagTypeName: String, tagType: TagType): Response<TagType?, E?>

    fun removeTagType(tagTypeName: String): Response<TagType?, E?>

    fun putData(key: String, value: String): Response<String?, E?>

    fun removeData(key: String, value: String): Response<String?, E?>
}
