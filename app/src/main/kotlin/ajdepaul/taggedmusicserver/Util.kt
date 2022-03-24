/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver

import ajdepaul.taggedmusicserver.models.Song
import java.security.MessageDigest
import java.util.*

// TODO remove
fun main() {
    println(sha256("a;ry8f4").length)
}

fun sha256(str: String): String {
    val hashedBytes = MessageDigest.getInstance("SHA-256")
        .digest(str.toByteArray())
    return Base64.getEncoder().encodeToString(hashedBytes)
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
