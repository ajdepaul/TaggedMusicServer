/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import ajdepaul.taggedmusicserver.models.Song
import ajdepaul.taggedmusicserver.models.songs
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.songRouting() {
    route("/song") {
        get {
            if (songs.isNotEmpty()) {
                call.respond(songs)
            } else {
                call.respondText("No songs found", status = HttpStatusCode.NotFound)
            }
        }

        get("{title}") {
            val title = call.parameters["title"] ?: return@get call.respondText(
                "Missing or malformed title",
                status = HttpStatusCode.BadRequest
            )
            val song = songs.find { it.title == title } ?: return@get call.respondText(
                "No song with title $title",
                status = HttpStatusCode.NotFound
            )
            call.respond(song)
        }

        post {
            val song = call.receive<Song>()
            songs.add(song)
            call.respondText("Song stored successfully", status = HttpStatusCode.Created)
        }

        delete("{title}") {
            val title = call.parameters["title"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (songs.removeIf { it.title == title }) {
                call.respondText("Song removed successfully", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("No song with title $title", status = HttpStatusCode.NotFound)
            }
        }
    }
}
