package com.loudbook.dev.api

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

class Map(val mapPath: String, mapName: String, gameInstance: GameInstance) {
    val spawnPoints: MutableMap<Team, Pos> = HashMap()
    private var maxPlayers: Int = 8
    private var playerPerTeam: Int = 1
    private var numberOfTeams: Int = 8

    init {

        val parsed: JsonElement? = try {
            JsonParser.parseReader(FileReader("./extensions/config/$mapName.json"))
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        val parsedObject = parsed!!.asJsonObject

        for (value in TeamColor.values()) {
            val stringPos: String = parsedObject.get("spawnpoints")
                .asJsonObject
                .get(value.color.toString().uppercase()).asString

            val split = stringPos.split(",")
            val spawnPos = Pos(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
            for (team in gameInstance.teams) {
                if (team.color == value) {
                    team.spawnPoint = spawnPos
                    spawnPoints[team] = spawnPos
                }
            }
        }

        this.playerPerTeam = parsedObject.get("playersPerTeam").asInt
        this.numberOfTeams = parsedObject.get("teams").asInt
        this.maxPlayers = parsedObject.get("playersPerTeam").asInt * parsedObject.get("teams").asInt
    }
}