package com.loudbook.dev

import com.loudbook.dev.api.GameInstance
import com.loudbook.dev.api.Team
import net.kyori.adventure.text.Component
import net.minestom.server.color.Color
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.PlayerInfoPacket
import net.minestom.server.network.packet.server.play.PlayerInfoPacket.AddPlayer
import java.util.List

@Suppress("UnstableApiUsage")
class GamePlayer(val player: Player) {
    var party: Party? = null
    var gameInstance: GameInstance? = null
    var team: Team? = null
    var isHidden = false

    fun isInParty(): Boolean {
        return party != null
    }

    fun hideFromServer() {
        player.updateViewableRule { viewer ->
            if (viewer.instance != this.player.instance) {
                val removePlayer = PlayerInfoPacket.RemovePlayer(player.uuid)
                val packet = PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, removePlayer)
                viewer.sendPacket(packet)
                false
            } else {
                true
            }
        }
    }

    fun hide() {
        this.player.updateViewableRule { viewer ->
            val removePlayer = PlayerInfoPacket.RemovePlayer(player.uuid)
            val packet = PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, removePlayer)
            viewer.sendPacket(packet)
            false
        }
        this.isHidden = true
    }

    fun show(username: String) {
        player.updateViewableRule { viewer: Player ->
            val prop =
                if (player.skin != null) listOf(
                    AddPlayer.Property(
                        "textures", player.skin!!
                            .textures(), player.skin!!.signature()
                    )
                ) else listOf()
            val info = PlayerInfoPacket(
                PlayerInfoPacket.Action.ADD_PLAYER,
                AddPlayer(
                    player.uuid,
                    player.username,
                    prop,
                    player.gameMode,
                    player.latency,
                    Component.text(username),
                    null
                )
            )
            viewer.sendPacket(info)
            true
        }
        isHidden = false
    }
}