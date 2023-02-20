package com.loudbook.dev.api

import net.kyori.adventure.text.format.NamedTextColor

enum class TeamColor(val color: NamedTextColor) {
    RED(NamedTextColor.RED),
    BLUE(NamedTextColor.BLUE),
    YELLOW(NamedTextColor.YELLOW),
    GREEN(NamedTextColor.GREEN),
    AQUA(NamedTextColor.AQUA),
    WHITE(NamedTextColor.WHITE),
    PINK(NamedTextColor.LIGHT_PURPLE),
    PURPLE(NamedTextColor.DARK_PURPLE);
}