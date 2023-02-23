package com.loudbook.dev

import com.loudbook.dev.api.Team
import net.kyori.adventure.text.Component
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket

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
                val removePlayer = PlayerInfoRemovePacket(player.uuid)
                viewer.sendPacket(removePlayer)
                false
            } else {
                true
            }
        }
    }

    fun hide() {
        this.player.updateViewableRule { viewer ->
            val removePlayer = PlayerInfoRemovePacket(player.uuid)
            viewer.sendPacket(removePlayer)
            false
        }
        this.isHidden = true
    }

    fun show(username: String) {
        player.updateViewableRule { viewer: Player ->
            val prop =
                PlayerInfoUpdatePacket.Property(
                    "textures", player.skin!!
                        .textures(), player.skin!!.signature()
                )
            val propList = listOf(prop)
            val info = PlayerInfoUpdatePacket(
                PlayerInfoUpdatePacket.Action.ADD_PLAYER,
                PlayerInfoUpdatePacket.Entry(
                    player.uuid,
                    player.username,
                    propList,
                    true,
                    player.latency,
                    GameMode.SURVIVAL,
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