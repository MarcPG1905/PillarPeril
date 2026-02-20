package com.marcpg.pillarperil.game.util

import com.marcpg.libpg.lang.string
import com.marcpg.libpg.util.component
import com.marcpg.pillarperil.PillarPeril
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.util.Configuration
import com.marcpg.pillarperil.util.Ticking
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.min

object QueueManager : Ticking {
    const val RED_COLORS = "#CC2222:#FF8888"
    const val GREEN_COLORS = "#22CC22:#88FF88"

    val queue = ArrayDeque<Player>()

    private var phase = 0.0

    fun add(player: Player) {
        if (!Configuration.queueEnabled || player in queue || GameManager.isInGame(player)) return

        queue.addLast(player)

        if (Configuration.queueCheckIntervalSecs == -1)
            check()
    }

    fun remove(player: Player) {
        if (!Configuration.queueEnabled) return

        queue.remove(player)

        if (Configuration.queueCheckIntervalSecs == -1)
            check()
    }

    override fun tick(tick: Ticking.Tick) {
        if (!Configuration.queueEnabled) return

        if (Configuration.queueCheckIntervalSecs >= 1) {
            if (tick.isInInterval(0, Configuration.queueCheckInterval))
                check()
        }

        queue.forEach { it.sendActionBar(it.locale().component(
            "queue.actionbar",
            queue.size.toString(), Configuration.queueMinPlayers.toString(),
            color = if (queue.size >= Configuration.queueMinPlayers) NamedTextColor.GREEN else NamedTextColor.RED
        )) }

        queue.forEach { it.sendActionBar(MiniMessage.miniMessage().deserialize("<gradient:${if (queue.size >= Configuration.queueMinPlayers) GREEN_COLORS else RED_COLORS}:$phase>${it.locale().string("queue.actionbar", queue.size.toString(), Configuration.queueMinPlayers.toString())}</gradient>")) }
    }

    private fun check() {
        if (queue.size < Configuration.queueMinPlayers)
            return

        val count = min(Configuration.queueMaxPlayers, queue.size)
        startGame(MutableList(count) { queue.removeFirst() })
    }

    private fun startGame(players: List<Player>) {
        val id = Game.generateId()
        val map = mutableMapOf(
            "id" to id,
            "mode" to Configuration.queueMode.gameInfo.namespace,
            "players" to players.size,
        )

        Configuration.queuePreCommands.forEach { PillarPeril.sendCommand(it(map)) }

        val worldName = Configuration.queueWorldName(map)
        val world = Bukkit.getWorld(worldName)
        if (world == null) {
            players.forEach {
                it.sendMessage(component("Configured world \"$worldName\" does not exist, which means the game cannot start.", NamedTextColor.RED))
                it.sendMessage(component("Please notify an admin of the server.", NamedTextColor.RED))
            }
            return
        }

        val location = Configuration.queueLocation(world)

        map += mapOf(
            "world" to location.world.name,
            "x" to location.x,
            "y" to location.y,
            "z" to location.z,
        )
        Configuration.queuePostCommands.forEach { PillarPeril.sendCommand(it(map)) }

        // Actually start the game after doing like 20 other things:
        Configuration.queueMode.gameConstructor(id, location, players).init()
    }
}
