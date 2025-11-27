package com.marcpg.pillarperil.event

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.game.util.QueueManager
import com.marcpg.pillarperil.util.Ticking
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent

object GameEvents : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onServerTickEnd(event: ServerTickEndEvent) {
        val tick = Ticking.Tick(event.tickNumber)

        GameManager.games.values.toList().forEach { it.tick(tick) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        modify(event.player, event.blockPlaced.location, event.blockReplacedState.blockData)
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockMultiPlace(event: BlockMultiPlaceEvent) {
        event.replacedBlockStates.forEach { modify(event.player, it.location, it.blockData) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        modify(event.player, event.block.location, event.block.blockData)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        modify(event.player, event.block.location, event.block.blockData)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBucketFill(event: PlayerBucketFillEvent) {
        modify(event.player, event.block.location, event.block.blockData)
    }

    private fun modify(player: Player, location: Location, data: BlockData) {
        GameManager.player(player)?.game?.buildings?.place(location, data)
    }
}
