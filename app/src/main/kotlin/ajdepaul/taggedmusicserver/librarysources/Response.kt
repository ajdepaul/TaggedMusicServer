/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.librarysources

data class Response<R>(val result: R, val status: Status) {
    enum class Status { SUCCESS, BAD_REQUEST, CONNECTION_ISSUE, TIME_OUT }
}
