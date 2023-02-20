package com.loudbook.dev.api

import com.loudbook.dev.GameType
import java.util.*

data class ServerInfo(val id: UUID, val gameType: GameType, val maxPlayers: Int, val currentPlayers: Int, val spotsOpen: Int)
