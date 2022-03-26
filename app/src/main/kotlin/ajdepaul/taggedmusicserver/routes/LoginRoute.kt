/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import ajdepaul.taggedmusicserver.librarysources.LibrarySource
import ajdepaul.taggedmusicserver.librarysources.Response
import ajdepaul.taggedmusicserver.models.Credentials
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/** Route for creating JSON web tokens. */
fun Route.loginRouting(secret: String, librarySource: LibrarySource) {
    post("/login") {
        val credentials = call.receive<Credentials>()

        val userResponse = librarySource.getUser(credentials.username)

        // check library source response
        if (userResponse.status != Response.Status.SUCCESS) {
            return@post call.respondText(
                "Could not check credentials",
                status = HttpStatusCode.BadGateway
            )
        }

        // check username
        val user = userResponse.result
            ?: return@post call.respondText("Bad login", status = HttpStatusCode.Unauthorized)

        // check password
        if (!Bcrypt.verify(credentials.password, user.passHash.toByteArray())) {
            return@post call.respondText("Bad login", status = HttpStatusCode.Unauthorized)
        }

        val token = JWT.create()
            .withClaim("id", user.id)
            .sign(Algorithm.HMAC256(secret))

        call.respond(hashMapOf("token" to token))
    }
}
