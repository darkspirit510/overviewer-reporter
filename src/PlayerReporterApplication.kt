package de.darkspirit510.overviewer

import br.com.gamemods.nbtmanipulator.NbtDouble
import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.nbtmanipulator.NbtList
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import java.io.File

private const val MINECRAFT_DIR = "/minecraft"
private const val DATE_UNKNOWN = 1522694775000

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
        }
    }
    routing {
        get("/") {
            call.respond(players())
        }
    }
}

fun players(): List<Player> {
    val cachedPlayers = cachedPlayers()

    return relevantPlayerFiles(cachedPlayers).map {
        val input = NbtIO.readNbtFile(it)

        Player(
                uuid(it).replace("-", ""),
                cachedPlayers.find { p -> p.uuid == uuid(it) }!!.name,
                it.lastModified(),
                pos(input)[0].value.toInt(),
                pos(input)[1].value.toInt(),
                pos(input)[2].value.toInt()
        )
    }.toList()
}

private fun relevantPlayerFiles(cachedPlayers: List<CachePlayer>): Sequence<File> =
        File("$MINECRAFT_DIR/world/playerdata").walk()
                .filter { !it.isDirectory }
                .filter { it.lastModified() != DATE_UNKNOWN }
                .filter { cachedPlayers.any { p -> p.uuid == uuid(it) } }

private fun pos(input: NbtFile) = input.compound["Pos"] as NbtList<NbtDouble>

private fun cachedPlayers() = jacksonObjectMapper().readValue<List<CachePlayer>>(File("$MINECRAFT_DIR/usercache.json"))

private fun uuid(it: File): String = it.name.split(".").first()

data class Player(
        val uuid: String,
        val name: String,
        val lastSeen: Long,
        val x: Int,
        val y: Int,
        val z: Int
)

data class CachePlayer(
        val name: String,
        val uuid: String,
        val expiresOn: String
)
