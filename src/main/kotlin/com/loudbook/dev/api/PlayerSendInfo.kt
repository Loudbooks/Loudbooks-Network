package com.loudbook.dev.api

import com.loudbook.dev.Party
import net.minestom.server.entity.Player
import java.util.*

data class PlayerSendInfo(val targetInstanceID: UUID = UUID.randomUUID(), val party: Party? = null)