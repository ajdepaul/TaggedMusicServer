/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

data class Response<R, E>(val response: R, val error: E?) {
    fun success() = error == null
    fun fail() = error != null
}
