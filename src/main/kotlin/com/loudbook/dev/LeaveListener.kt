package com.loudbook.dev

import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerDisconnectEvent

class LeaveListener(private val playerManager: PlayerManager, private val gameInstanceManager: GameInstanceManager) : EventListener<PlayerDisconnectEvent> {
    override fun eventType(): Class<PlayerDisconnectEvent> {
        return PlayerDisconnectEvent::class.java
    }

    override fun run(event: PlayerDisconnectEvent): EventListener.Result {
        this.gameInstanceManager.getInstance(event.player.instance!!)!!.removePlayer(this.playerManager.getGamePlayer(event.player)!!)
        return EventListener.Result.SUCCESS
    }
}