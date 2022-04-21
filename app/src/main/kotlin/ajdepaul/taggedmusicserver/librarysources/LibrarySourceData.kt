/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

import java.time.LocalDateTime

data class User(
    val username: String,
    val passHash: String,
    val admin: Boolean
)

data class Song(
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

data class Tag(
    val type: String? = null,
    val description: String? = null
)

data class TagType(
    val color: Int
)
