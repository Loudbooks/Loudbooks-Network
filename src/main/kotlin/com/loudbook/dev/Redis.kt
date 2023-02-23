package com.loudbook.dev

import com.loudbook.dev.api.ServerInfo
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class Redis() {
    val client: RedissonClient
    private var uri: String? = null

    init {
        try {
            FileInputStream("./extensions/config.properties").use { input ->
                val prop = Properties()
                prop.load(input)
                this.uri = prop.getProperty("uri")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val config = Config()
        config.useSingleServer()
            .address = uri
        this.client = Redisson.create(config)
    }

    fun pushInstanceInfo(instance: GameInstance, shuttingDown: Boolean) {
        this.client.getTopic("server-info").publish(
            ServerInfo(
                instance.id,
                instance.gameType.toString(),
                instance.maxPlayers,
                instance.instanceContainer.players.size,
                instance.maxPlayers - instance.instanceContainer.players.size,
                shuttingDown)
        )
    }

    fun pushServerInfo(gameInstanceManager: GameInstanceManager) {
        for (gameInstance in gameInstanceManager.gameInstances) {
            pushInstanceInfo(gameInstance, false)
        }
    }
}