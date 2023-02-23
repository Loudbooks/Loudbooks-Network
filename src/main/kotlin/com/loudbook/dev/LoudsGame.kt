package com.loudbook.dev

import com.loudbook.dev.commands.StopCommand
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.NamespaceID

import net.minestom.server.world.DimensionType





class LoudsGame : Extension() {
    var redis: Redis? = null
    var gameInstanceManager: GameInstanceManager? = null

    private var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
        .ambientLight(2.0f)
        .build()
    override fun initialize() {
        MinecraftServer.getCommandManager().register(StopCommand())

        MinecraftServer.setBrandName("Loud's Network")

        val gameNode: EventNode<Event> = EventNode.all("game")
        val survivalGamesNode: EventNode<Event> = EventNode.all("survival-games")

        MinecraftServer.getDimensionTypeManager().addDimension(fullbright)

        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer(fullbright)

        instanceContainer.setGenerator { unit ->
            unit.modifier().fillHeight(0, 1, Block.AIR)
        }

        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(
            PlayerLoginEvent::class.java
        ) { event: PlayerLoginEvent ->
            event.setSpawningInstance(
                instanceContainer
            )
            event.player.respawnPoint = Pos(0.5, 60.0, 0.5, -90f, 0f)
            event.player.gameMode = GameMode.ADVENTURE
            val skin = PlayerSkin.fromUsername(event.player.username)
            event.player.skin = skin
        }

        this.redis = Redis()

        val playerManager = PlayerManager()
        this.gameInstanceManager = GameInstanceManager(GameType.SURVIVAL, playerManager, redis!!)

        MinecraftServer.getGlobalEventHandler().addListener(JoinListener(redis!!, playerManager, gameInstanceManager!!))
        gameNode.addListener(LeaveListener(playerManager, gameInstanceManager!!))

        for (i in 0..0){
            gameInstanceManager!!.createInstance()
            println("Created instance $i")
        }

        MinecraftServer.getSchedulerManager().submitTask {
            redis!!.pushServerInfo(gameInstanceManager!!)
            TaskSchedule.seconds(3)
        }

        BungeeCordProxy.enable()
    }

    override fun terminate() {
        for (gameInstance in gameInstanceManager!!.gameInstances) {
            println("Saving instance ${gameInstance.id}")
            redis!!.pushInstanceInfo(gameInstance, true)
        }
        redis!!.client.shutdown()
    }
}