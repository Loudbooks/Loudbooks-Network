package com.loudbook.dev

import net.minestom.server.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class Party(private val owner: Player, private val members: MutableList<Player> = ArrayList()) {
    val id = owner.uuid

    fun addMember(player: Player) {
        if (members.contains(player)) return
        members.add(player)
    }

    fun removeMember(player: Player) {
        members.remove(player)
    }

    fun isMember(player: Player): Boolean {
        return members.contains(player)
    }

    fun isOwner(player: Player): Boolean {
        return owner == player
    }

    fun getMembers(): List<Player> {
        return members
    }

    fun getMemberCount(): Int {
        return members.size
    }
}