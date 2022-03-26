/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
package ajdepaul.taggedmusicserver

import ajdepaul.taggedmusicserver.librarysources.MysqlLibrarySource
import ajdepaul.taggedmusicserver.librarysources.InMemoryLibrarySource
import ajdepaul.taggedmusicserver.librarysources.LibrarySource
import ajdepaul.taggedmusicserver.librarysources.User
import ajdepaul.taggedmusicserver.models.TagType
import ajdepaul.taggedmusicserver.routes.audioRouting
import ajdepaul.taggedmusicserver.routes.loginRouting
import ajdepaul.taggedmusicserver.routes.songRouting
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val saltRounds = environment.config.property("bcrypt.saltRounds").getString().toInt()

    val librarySource: LibrarySource =
        if (!developmentMode) createMysqlLibrarySource()
        else createTestLibrarySource(saltRounds)

    // load jwt secret
    val configSecret = environment.config.propertyOrNull("jwt.secret")?.getString()
    val jwtSecret = when {
        configSecret != null -> {
            log.info("Loaded JWT secret from the SECRET environment variable.")
            configSecret
        }
        !developmentMode -> {
            log.error("JWT secret must be set using the SECRET environment variable while not in development mode.")
            exitProcess(-1)
        }
        else -> {
            log.warn("JWT secret could not be loaded. Using \"secret\" while in development mode.")
            "secret"
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .build())
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }

    routing {
        loginRouting(jwtSecret, librarySource)
        authenticate("auth-jwt") {
            audioRouting()
            songRouting(librarySource)
        }
    }
}

/** Creates library source from a mysql connection. */
fun Application.createMysqlLibrarySource(): MysqlLibrarySource {
    val mysqlHost = environment.config.propertyOrNull("mysql.host")?.getString()
    if (mysqlHost != null) {
        log.info("Using $mysqlHost as the mysql server host.")
    } else {
        log.error("Mysql host must be set using the MYSQL_HOST environment variable.")
        exitProcess(-1)
    }

    val mysqlPort = environment.config.property("mysql.port").getString().toInt()
    log.info("Using $mysqlPort as the Mysql server port.")

    val mysqlUsername = environment.config.propertyOrNull("mysql.username")?.getString()
    if (mysqlUsername != null) {
        log.info("Using $mysqlUsername as the Mysql server username.")
    } else {
        log.error("Mysql username must be set using the MYSQL_USERNAME environment variable.")
        exitProcess(-1)
    }

    val mysqlPassword = environment.config.propertyOrNull("mysql.password")?.getString()
    if (mysqlPassword != null) {
        log.info("Loaded Mysql server password.")
    } else {
        log.error("Mysql password must be set using the MYSQL_PASSWORD environment variable.")
        exitProcess(-1)
    }

    val mysqlDatabaseName = environment.config.propertyOrNull("mysql.databaseName")?.getString()
    if (mysqlDatabaseName != null) {
        log.info("Using $mysqlDatabaseName as the Mysql server database name.")
    } else {
        log.error("Mysql database name must be set using the MYSQL_DATABASE_NAME environment " +
                "variable.")
        exitProcess(-1)
    }

    val dataSource = MysqlDataSource()
    dataSource.serverName = mysqlHost
    dataSource.port = mysqlPort
    dataSource.user = mysqlUsername
    dataSource.setPassword(mysqlPassword)
    dataSource.databaseName = mysqlDatabaseName
    return MysqlLibrarySource(dataSource)
}

/** Creates an in memory library source for development purposes. */
fun createTestLibrarySource(saltRounds: Int): InMemoryLibrarySource {
    val librarySource = InMemoryLibrarySource()

    librarySource.addUser(
        User(1, "username", String(Bcrypt.hash("password", saltRounds))),
        TagType(0)
    )

    return librarySource
}

// TODO ?
//fun Application.shutdown(statusCode: Int) {
//    val latch = CompletableDeferred<Nothing>()
//    launch {
//        latch.join()
//
//        environment.monitor.raise(ApplicationStopPreparing, environment)
//        if (environment is ApplicationEngineEnvironment) {
//            (environment as ApplicationEngineEnvironment).stop()
//        } else {
//            dispose()
//        }
//
//        exitProcess(statusCode)
//    }
//
//    latch.cancel()
//}
