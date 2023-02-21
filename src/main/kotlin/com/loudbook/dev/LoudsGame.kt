package com.loudbook.dev

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.utils.NamespaceID

import net.minestom.server.world.DimensionType





class LoudsGame : Extension() {
    override fun initialize() {
        MinecraftServer.setBrandName("Loudbook's Minigames")

        val gameNode: EventNode<Event> = EventNode.all("game")
        val survivalGamesNode: EventNode<Event> = EventNode.all("survival-games")

        val redis = Redis()

        val playerManager = PlayerManager()
        val gameInstanceManager = GameInstanceManager(GameType.SURVIVAL, playerManager, redis)

        gameNode.addListener(JoinListener(redis, playerManager, gameInstanceManager))
        gameNode.addListener(LeaveListener(playerManager, gameInstanceManager))

        for (i in 0..2){
            gameInstanceManager.createInstance()
        }
    }

    override fun terminate() {

    }
}