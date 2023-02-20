package com.loudbook.dev

import com.loudbook.dev.api.GameInstance
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import java.util.*

class GameInstanceManager(private val gameType: GameType, private val playerManager: PlayerManager, private val redis: Redis) {
    private val gameInstances: MutableList<GameInstance> = ArrayList()
    private val gameInstanceMap: MutableMap<Instance, GameInstance> = HashMap()
    private var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
        .ambientLight(2.0f)
        .build()

    init {
        MinecraftServer.getDimensionTypeManager().addDimension(fullbright);
    }

    fun getInstance(instance: Instance): GameInstance? {
        return gameInstanceMap[instance]
    }

    fun getInstance(id: UUID): GameInstance? {
        return gameInstances.find { it.id == id }
    }

    fun removeInstance(instance: GameInstance) {
        gameInstances.remove(instance)
        gameInstanceMap.remove(instance.instanceContainer)
    }

    fun createInstance(): GameInstance {
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer(fullbright)
        instanceContainer.setGenerator { unit: GenerationUnit ->
            unit.modifier().fillHeight(0, 1, Block.AIR)
        }

        val gameInstance = GameInstance(instanceContainer, gameType, playerManager = this.playerManager, redis = this.redis)

        gameInstances.add(gameInstance)
        gameInstanceMap[gameInstance.instanceContainer] = gameInstance
        return gameInstance
    }
}