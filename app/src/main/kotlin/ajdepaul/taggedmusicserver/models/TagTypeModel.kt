/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.models

import kotlinx.serialization.Serializable

@Serializable
data class TagTypeModel(
    val name: String,
    val color: Int
) {
    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other !is TagTypeModel) false
        else name == other.name
    }
}
