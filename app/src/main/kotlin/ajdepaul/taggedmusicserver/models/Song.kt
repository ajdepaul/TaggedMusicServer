package ajdepaul.taggedmusicserver.models


import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

val songs = mutableListOf(Song("title", 3))

@Serializable
data class Song constructor(
    val title: String,
    val duration: Int,
    val trackNum: Int? = null,
//    @Contextual
//    val releaseDate: LocalDateTime? = null,
//    @Contextual
//    val createDate: LocalDateTime = LocalDateTime.now(),
//    @Contextual
//    val modifyDate: LocalDateTime = LocalDateTime.now(),
    val playCount: Int = 0,
    val tags: Set<String> = setOf()
)
