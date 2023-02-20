package com.loudbook.dev

import net.minestom.server.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class Party(private val owner: GamePlayer, private val members: MutableList<GamePlayer> = ArrayList()) {
    val id = owner.player.uuid

    fun addMember(player: GamePlayer) {
        if (members.contains(player)) return
        members.add(player)
    }

    fun removeMember(player: GamePlayer) {
        members.remove(player)
    }

    fun isMember(player: GamePlayer): Boolean {
        return members.contains(player)
    }

    fun isOwner(player: GamePlayer): Boolean {
        return owner == player
    }

    fun getMembers(): List<GamePlayer> {
        return members
    }

    fun getMemberCount(): Int {
        return members.size
    }
}