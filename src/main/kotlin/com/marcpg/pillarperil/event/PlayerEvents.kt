package com.marcpg.pillarperil.event

import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.util.Configuration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent

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
}
