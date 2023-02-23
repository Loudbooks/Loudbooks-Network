package com.loudbook.dev

import com.loudbook.dev.api.PlayerSendInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerLoginEvent
import java.util.*


class JoinListener(private val redis: Redis, private val playerManager: PlayerManager, private val gameInstanceManager: GameInstanceManager) : EventListener<PlayerLoginEvent> {
    override fun eventType(): Class<PlayerLoginEvent> {
        return PlayerLoginEvent::class.java
    }

    override fun run(event: PlayerLoginEvent): EventListener.Result {


        val player = event.player
        val map = this.redis.client.getMap<UUID, PlayerSendInfo>("player-send-info")
        val playerSendInfo = map[player.uuid]

        if (playerSendInfo == null) {
            player.kick(Component.text("Could not find your player data!").color(NamedTextColor.RED))
            return EventListener.Result.EXCEPTION
        }

        val skin = PlayerSkin.fromUsername(event.player.username)
        event.player.skin = skin

        val gamePlayer = this.playerManager.addPlayer(player)

        val gameInstance = this.gameInstanceManager.getInstance(playerSendInfo.targetInstanceID)

        gamePlayer.gameInstance = gameInstance
        event.setSpawningInstance(gameInstance!!.instanceContainer)
        event.player.respawnPoint = (Pos(0.0, 100.0, 0.0))

        if (playerSendInfo.party != null) {
            if (playerManager.partyByID(playerSendInfo.party.id) == null) {
                this.playerManager.parties.add(playerSendInfo.party)
                gamePlayer.party = playerSendInfo.party
                playerSendInfo.party.addMember(player)
            } else {
                gamePlayer.party = playerManager.partyByID(playerSendInfo.party.id)
                gamePlayer.party!!.addMember(player)
            }
        }

        gamePlayer.hideFromServer()

        gameInstance.players.add(gamePlayer)

        redis.pushInstanceInfo(gameInstance, false)

        if (gameInstance.players.size >= gameInstance.requiredPlayers && !gameInstance.countdown.isRunning) {
            gameInstance.countdown.run()
        }

        if (gameInstance.maxPlayers - gameInstance.players.size == 3) {
            gameInstanceManager.createInstance()
        }

        return EventListener.Result.SUCCESS
    }
}