package de.darkspirit510.overviewer

import br.com.gamemods.nbtmanipulator.NbtDouble
import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.nbtmanipulator.NbtList
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.t9t.minecraftrconclient.RconClient
import java.io.File

fun main() {
    while (true) {
        println("Writing fresh data...")
        sendSaveAllCommand()
        writePlayerPositionsJson()
        sleepFiveMinutes()
    }
}

private fun sleepFiveMinutes() {
    Thread.sleep(envInt("SLEEP_MINUTES") * 60000L)
}

fun envLong(name: String): Long = env(name).toLong()

private fun writePlayerPositionsJson() {
    outputFile().writeText(json(players()))
}

private fun sendSaveAllCommand() {
    RconClient.open(env("RCON_SERVER"), envInt("RCON_PORT"), env("RCON_PASSWORD")).use {
        it.sendCommand("save-all")
    }
}

fun envInt(name: String): Int = env(name).toInt()

fun env(name: String): String = System.getenv(name)

private fun json(values: List<Player>) = jacksonObjectMapper().writeValueAsString(values)

fun outputFile() = File("${env("DESTINATION_DIR")}/player_positions.json")

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
        File("${env("SOURCE_DIR")}/world/playerdata").walk()
                .filter { !it.isDirectory }
                .filter { it.lastModified() > loginDate() }
                .filter { cachedPlayers.any { p -> p.uuid == uuid(it) } }

private fun loginDate() = envLong("LOGIN_DATE")

private fun pos(input: NbtFile) = input.compound["Pos"] as NbtList<NbtDouble>

private fun cachedPlayers() = jacksonObjectMapper().readValue<List<CachePlayer>>(File("${env("SOURCE_DIR")}/usercache.json"))

private fun uuid(it: File): String = it.name.split(".").first()
