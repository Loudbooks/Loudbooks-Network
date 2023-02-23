package com.loudbook.dev.api

import com.loudbook.dev.GameType
import java.util.*

data class ServerInfo(val id: UUID = UUID.randomUUID(), val gameType: String = "null", val maxPlayers: Int = 8, val currentPlayers: Int = 0, val spotsOpen: Int = 8, val shuttingDown: Boolean = false)
