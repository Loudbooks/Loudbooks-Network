package com.loudbook.dev.api

import com.loudbook.dev.Party
import net.minestom.server.entity.Player
import java.util.*

data class PlayerSendInfo(val player: Player, val targetInstanceID: UUID, val party: Party? = null)