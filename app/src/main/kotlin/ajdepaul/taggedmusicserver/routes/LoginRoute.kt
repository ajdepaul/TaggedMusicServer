/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import ajdepaul.taggedmusicserver.librarysources.LibrarySource
import ajdepaul.taggedmusicserver.librarysources.Response
import ajdepaul.taggedmusicserver.models.CredentialsModel
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
            call.receive<CredentialsModel>()
        } catch (e: SerializationException) {
            return@post call.respondText("Malformed JSON", status = HttpStatusCode.BadRequest)
        }

        // get id
        val user = librarySource.getUser(credentials.username).let { response ->

            // check library source response
            if (response.status != Response.Status.SUCCESS) {
                return@post call.respondText(
                    "Could not check credentials",
                    status = HttpStatusCode.BadGateway
                )
            }

            response.result
                ?: return@post call.respondText("Bad login", status = HttpStatusCode.Unauthorized)
        }

        // get password hash
        val userPassHash = librarySource.getPassHash(user.id).let { response ->

            // check library source response
            if (response.status != Response.Status.SUCCESS) {
                return@post call.respondText(
                    "Could not check credentials",
                    status = HttpStatusCode.BadGateway
                )
            }

            // errors out if the id retrieved no longer points to a user
            response.result ?: return@post call.respond(HttpStatusCode.InternalServerError)
        }

        // check password
        if (!Bcrypt.verify(credentials.password, userPassHash.toByteArray())) {
            return@post call.respondText("Bad login", status = HttpStatusCode.Unauthorized)
        }

        // create jwt
        val token = JWT.create()
            .withClaim("id", user.id)
            .withClaim("admin", user.admin)
            .sign(Algorithm.HMAC256(secret))

        @Serializable
        data class Response(val token: String, val id: Int)
        call.respond(Response(token, user.id))
    }
}
