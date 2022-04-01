/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.models

import kotlinx.serialization.Serializable

@Serializable
data class TagModel(
    val name: String,
    val type: String? = null,
    val description: String? = null
) {
    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other !is TagModel) false
        else name == other.name
    }
}
