/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File
import java.lang.IllegalStateException

/** Route for interacting with a user's audio files. */
fun Route.audioRouting() {

    /* Used to retrieve an audio file from a user's library. */
    get("/audio/{id}/{file}") {

        // read jwt
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
            return@get call.respondText("Not in library", status = HttpStatusCode.NotFound)
        }

        // TODO may need to change this for proper streaming
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                file.name
            ).toString()
        )
        call.respondFile(file)
    }

    // TODO move to song route
    /* Used to upload a song to a user's library. */
    post("/audio/{id}/{file}") {

        // read jwt
        val principal = call.principal<JWTPrincipal>()!!
        val jwtId = principal.payload.getClaim("id").asInt()

        // check id in path
        val pathId = call.parameters["id"]?.toIntOrNull()
            ?: return@post call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

        // check file in path
        val pathFile = call.parameters["file"]
            ?: return@post call.respondText(
                "Missing or malformed file",
                status = HttpStatusCode.BadRequest
            )

        // check access to this path id
        if (jwtId != pathId) {
            return@post call.respond(HttpStatusCode.Forbidden)
        }

        val file = File("audio/$pathId/$pathFile")

        // check file exists
        if (file.exists()) {
            return@post call.respondText("File already exists", status = HttpStatusCode.BadRequest)
        }

        // check content-type
        val multipart = try {
            call.receiveMultipart()
        } catch (e: IllegalStateException) {
            return@post call.respondText(
                "Form-data content-type required",
                status = HttpStatusCode.BadRequest
            )
        }

        var receivedFile = false

        // receive file
        multipart.forEachPart { part ->

            if (part.name == "file" && part is PartData.FileItem) {
                val fileBytes = part.streamProvider().readBytes()
                File("audio/$pathId/$pathFile").writeBytes(fileBytes)
                receivedFile = true
            }

            part.dispose()
        }

        // did not receive file
        if (!receivedFile) {
            return@post call.respondText("Malformed form-data", status = HttpStatusCode.BadRequest)
        }

        return@post call.respondText("File uploaded")
    }
}
