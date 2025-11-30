package com.marcpg.pillarperil.event

import com.marcpg.libpg.util.bukkitRunLater
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.game.util.QueueManager
import com.marcpg.pillarperil.util.Configuration
import com.marcpg.pillarperil.util.QueueMethod
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerEvents : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = GameManager.player(event.player) ?: return

        if (event.player.killer != null)
            player.game.player(event.player.killer!!, false)?.kills++

        player.game.eliminate(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.to.y < Configuration.deathHeight) {
            GameManager.player(event.player) ?: return
            event.player.health = 0.0
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (Configuration.queueMethod == QueueMethod.AUTO)
            bukkitRunLater(20L) { if (event.player.isOnline) QueueManager.add(event.player) } // Wait 1 second before rejoining queue.
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        QueueManager.remove(event.player)
    }
}
