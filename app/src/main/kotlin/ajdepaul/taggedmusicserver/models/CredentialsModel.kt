/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.models

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsModel(val username: String, val password: String)
