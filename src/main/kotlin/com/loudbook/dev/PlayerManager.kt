package com.loudbook.dev

import net.minestom.server.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlayerManager {
    private val players: MutableList<GamePlayer> = ArrayList()
    private val playerMap: MutableMap<Player, GamePlayer> = HashMap()
    val parties: MutableList<Party> = ArrayList()

    fun addPlayer(player: Player): GamePlayer {
        val gamePlayer = GamePlayer(player)
        players.add(gamePlayer)
        playerMap[player] = gamePlayer
        return gamePlayer
    }

    fun removePlayer(player: Player) {
        players.remove(playerMap[player])
        playerMap.remove(player)
    }

    fun removePlayer(gamePlayer: GamePlayer) {
        players.remove(gamePlayer)
        playerMap.remove(gamePlayer.player)
    }

    fun getGamePlayer(player: Player): GamePlayer? {
        return playerMap[player]
    }

    fun partyByID(id: UUID): Party? {
        return parties.find { it.id == id }
    }
}