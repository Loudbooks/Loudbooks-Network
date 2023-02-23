package com.loudbook.dev

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.TitlePart
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule





class Countdown(private val gameInstance: GameInstance) : Runnable {
    private val time = 10
    private var timeLeft = time
    var isRunning = false

    override fun run() {
        isRunning = true
        val scheduler: Scheduler = MinecraftServer.getSchedulerManager()
        scheduler.submitTask {
            if (gameInstance.players.size < gameInstance.requiredPlayers) {
                gameInstance.players.forEach { player ->
                    player.player.sendTitlePart(TitlePart.TITLE, Component.text("Not enough players!").color(
                        NamedTextColor.RED).decorate(TextDecoration.BOLD));
                }
                timeLeft = time
                isRunning = false
                TaskSchedule.stop()
            }

            if (timeLeft == 0) {
                gameInstance.startGame()
                timeLeft = time
                isRunning = false
                TaskSchedule.stop()
            }

            val color = if (timeLeft <= 3) {
                NamedTextColor.RED
            } else if (timeLeft <= 5) {
                NamedTextColor.YELLOW
            } else {
                NamedTextColor.GREEN
            }

            gameInstance.players.forEach { player ->
                player.player.sendTitlePart(TitlePart.TITLE, Component.text(timeLeft.toString()).color(
                    color).decorate(TextDecoration.BOLD));
            }

            timeLeft--

            TaskSchedule.seconds(1)
        }
    }
}