package com.loudbook.dev.api

import com.loudbook.dev.GamePlayer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos

class Team(val color: TeamColor, private val teamSize: Int, var spawnPoint: Pos) {
    private val players = mutableListOf<GamePlayer>()
    private var playerCount = 0

    fun addPlayer(player: GamePlayer) {
        players.add(player)
        playerCount++
    }

    fun removePlayer(player: GamePlayer) {
        players.remove(player)
        playerCount--
    }

    fun spawnPlayers() {
        for (player in players) {
            player.player.teleport(this.spawnPoint)
            player.player.heal()
            player.player.inventory.clear()
        }
    }

    fun isFull(): Boolean {
        return playerCount >= teamSize
    }
}