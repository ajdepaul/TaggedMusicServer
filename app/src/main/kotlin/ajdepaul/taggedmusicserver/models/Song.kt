/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.models

import ajdepaul.taggedmusicserver.models.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Song(
    val title: String,
    val duration: Int,
    val trackNum: Int? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val releaseDate: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createDate: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val modifyDate: LocalDateTime = LocalDateTime.now(),
    val playCount: Int = 0,
    val tags: Set<String> = setOf()
)
