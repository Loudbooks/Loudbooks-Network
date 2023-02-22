package com.loudbook.dev.api

import com.loudbook.dev.*
import dev.hypera.scaffolding.Scaffolding
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import java.io.File
import java.util.*


class GameInstance(val instanceContainer: InstanceContainer,
                   val gameType: GameType,
                   val requiredPlayers: Int,
                   private var map: Map? = null,
                   private val playerManager: PlayerManager,
                   private val redis: Redis) {
    val id: UUID = UUID.randomUUID()
    val teams: MutableList<Team> = ArrayList()
    val maxPlayers: Int = 8
    val teamSize: Int = 1
    val players: MutableList<GamePlayer> = ArrayList()
    val countdown: Countdown = Countdown(this)

    init {
        val thread = Thread {
            if (map == null) {
                this.map = getRandomMap()
            }

            val schematic = Scaffolding.fromFile(File(map!!.mapPath))
            if (schematic == null) {
                println("Could not load schematic!")
                return@Thread
            }

            for (i in 1..maxPlayers/teamSize) {
                val team = Team(TeamColor.values()[i-1], this.teamSize, map!!.spawnPoints.values.toList()[i-1])
                this.teams.add(team)
            }

            schematic.build(this.instanceContainer, Pos(0.0, 0.0, 0.0)).thenRun {
                println("Loaded map ${map!!.mapPath} for game type ${gameType.name.lowercase()}")
                redis.pushInstanceInfo(this, false)
            }
        }
        thread.start()
    }

    private fun getRandomMap(): Map? {
        val type = gameType.name.lowercase()
        val files = File("./extensions/schematics/$type").listFiles()

        if (files == null) {
            println("Could not find a map!")
            return null
        }

        val path = files.random()

        if (path == null) {
            println("Could not find a map!")
            return null
        }

        val map = Map(
            path.path,
            path.path.replace("./extensions/schematics/$type/", "").replace(".schematic", ""),
            this)

        this.map = map
        return map
    }

    fun startGame() {
        var index = 0
        var currentTeam = this.teams[index]
        val players = mutableListOf<Player>()
        for (party in playerManager.parties) {
            for (player in party.getMembers()) {
                if (players.contains(player)) continue
                players.add(player)
            }
        }

        for (player in this.players) {
            if (!player.isInParty()) {
                players.add(player.player)
            }
        }

        for (player in players) {
            val gamePlayer = playerManager.getGamePlayer(player)
            if (!currentTeam.isFull()) {
                currentTeam.addPlayer(gamePlayer!!)
            } else {
                index++
                currentTeam = this.teams[index]
                currentTeam.addPlayer(gamePlayer!!)
            }
        }

        for (team in this.teams) {
            team.spawnPlayers()
        }
    }

    fun removePlayer(player: GamePlayer) {
        this.players.remove(player)

        for (team in this.teams) {
            team.removePlayer(player)
        }

        for (player1 in this.players) {
            player1.player.sendMessage(
                Component.textOfChildren(
                    Component.text(player.player.username)
                        .color(player.team!!.color.color),
                    Component.text(" left the game!")
                        .color(NamedTextColor.GRAY)
                )
            )
        }

        this.playerManager.removePlayer(player)
    }
}