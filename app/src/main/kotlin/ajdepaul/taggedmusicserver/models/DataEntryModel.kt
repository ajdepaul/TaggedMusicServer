/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.models

import kotlinx.serialization.Serializable

@Serializable
data class DataEntryModel(
    val key: String,
    val value: String
) {
    override fun hashCode(): Int = key.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other !is DataEntryModel) false
        else value == other.value
    }
}
