/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

/** Route for serving audio files. */
fun Route.audioRouting() {
    get("audio/{id}/{file}") { // audio/{id}

        val principal = call.principal<JWTPrincipal>()!!
        val jwtId = principal.payload.getClaim("id").asInt()

        // check id in path
        val pathId = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

        // check file in path
        val pathFile = call.parameters["file"]
            ?: return@get call.respondText(
                "Missing or malformed file",
                status = HttpStatusCode.BadRequest
            )

        // check access to this path id
        if (jwtId != pathId) {
            return@get call.respond(HttpStatusCode.Forbidden)
        }

        val file = File("audio/$pathId/$pathFile")

        // check file exists
        if (!file.exists()) {
            return@get call.respond(HttpStatusCode.NotFound)
        }

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                file.name
            ).toString()
        )
        call.respondFile(file)
    }
}
