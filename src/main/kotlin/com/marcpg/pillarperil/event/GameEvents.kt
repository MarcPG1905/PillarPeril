package com.marcpg.pillarperil.event

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent
import com.destroystokyo.paper.event.server.ServerTickEndEvent
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.game.util.QueueManager
import com.marcpg.pillarperil.util.Ticking
import io.papermc.paper.event.entity.EntityPortalReadyEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.world.PortalCreateEvent

object GameEvents : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onServerTickEnd(event: ServerTickEndEvent) {
        val tick = Ticking.Tick(event.tickNumber)

        QueueManager.tick(tick)
        GameManager.games.values.toList().forEach { it.tick(tick) }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntitySpawn(event: EntitySpawnEvent) {
        GameManager.getClosestGame(event.location)?.buildings?.registerSpawn(event.entity)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerSetSpawn(event: PlayerSetSpawnEvent) {
        if (event.cause == PlayerSetSpawnEvent.Cause.BED || event.cause == PlayerSetSpawnEvent.Cause.RESPAWN_ANCHOR)
            event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (event.block.type == Material.END_PORTAL_FRAME && GameManager.isWithinGame(event.block.location))
            event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPortalCreate(event: PortalCreateEvent) {
        if (event.reason != PortalCreateEvent.CreateReason.FIRE) return

        if ((event.entity != null && GameManager.isPartOfGame(event.entity!!)) || GameManager.isWithinGame(event.blocks.first().location))
            event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityPortalReady(event: EntityPortalReadyEvent) {
        if (GameManager.isPartOfGame(event.entity))
            event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPortal(event: PlayerPortalEvent) {
        if (GameManager.isInGame(event.player))
            event.isCancelled = true
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
        GameManager.player(player)?.game?.buildings?.registerPlace(location, data)
    }
}
