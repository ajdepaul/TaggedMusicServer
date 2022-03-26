/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver.routes

import ajdepaul.taggedmusicserver.librarysources.LibrarySource
import io.ktor.routing.*

/** Route for managing user song data. */
fun Route.songRouting(librarySource: LibrarySource) {
    route("/songs") {
        get {
            // TODO
//            if (songs.isNotEmpty()) {
//                call.respond(songs)
//            } else {
//                call.respondText("No songs found", status = HttpStatusCode.NotFound)
//            }
        }

        get("{title}") {
            // TODO
//            val title = call.parameters["title"] ?: return@get call.respondText(
//                "Missing or malformed title",
//                status = HttpStatusCode.BadRequest
//            )
//            val song = songs.find { it.title == title } ?: return@get call.respondText(
//                "No song with title $title",
//                status = HttpStatusCode.NotFound
//            )
//            call.respond(song)
        }

        post {
            // TODO
//            val song = call.receive<Song>()
//            songs.add(song)
//            call.respondText("Song stored successfully", status = HttpStatusCode.Created)
        }

        delete("{title}") {
            // TODO
//            val title = call.parameters["title"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (songs.removeIf { it.title == title }) {
//                call.respondText("Song removed successfully", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("No song with title $title", status = HttpStatusCode.NotFound)
//            }
        }
    }
}
