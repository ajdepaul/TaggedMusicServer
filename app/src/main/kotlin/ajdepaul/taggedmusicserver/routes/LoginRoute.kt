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
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

/** Route for creating JSON web tokens. */
fun Route.loginRouting(secret: String, librarySource: LibrarySource) {

    /* Used to retrieve a JSON web token according to user credentials. */
    post("/login") {

        // check json format
        val credentials = try {
            call.receive<Credentials>()
        } catch (e: SerializationException) {
            return@post call.respondText("Malformed JSON", status = HttpStatusCode.BadRequest)
        }

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

        // create jwt
        val token = JWT.create()
            .withClaim("id", user.id)
            .sign(Algorithm.HMAC256(secret))

        @Serializable
        data class Response(val token: String, val id: Int)
        call.respond(Response(token, user.id))
    }
}
