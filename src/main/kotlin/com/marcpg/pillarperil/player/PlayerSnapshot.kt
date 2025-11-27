package com.marcpg.pillarperil.player

import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Scoreboard
import java.util.*

data class PlayerSnapshot(
    val uuid: UUID,
    val displayName: Component,
    val inventory: List<ItemStack?>,
    val inventoryArmor: List<ItemStack?>,
    val inventoryExtra: List<ItemStack?>,
    val exp: Float,
    val totalExperience: Int,
    val level: Int,
    val location: Location,
    val respawnLocation: Location?,
    val gameMode: GameMode,
    val scoreboard: Scoreboard,
    val health: Double,
    val foodLevel: Int,
    val saturation: Float,
) {
    constructor(player: Player) : this(
        player.uniqueId,
        player.displayName(),
        player.inventory.contents.toList(),
        player.inventory.armorContents.toList(),
        player.inventory.extraContents.toList(),
        player.exp,
        player.totalExperience,
        player.level,
        player.location.clone(),
        player.respawnLocation?.clone(),
        player.gameMode,
        player.scoreboard,
        player.health,
        player.foodLevel,
        player.saturation,
    )

    fun set(
        player: Player,
        restoreName: Boolean = false,
        restoreLocation: Boolean = true,
        restoreInventory: Boolean = true,
        restoreExperience: Boolean = true,
        restoreRespawnLocation: Boolean = true,
        restoreGameMode: Boolean = true,
        restoreScoreboard: Boolean = true,
        restoreHealth: Boolean = true,
        restoreHunger: Boolean = true,
    ) {
        if (restoreName)
            player.displayName(displayName)

        if (restoreInventory) {
            player.inventory.contents = inventory.toTypedArray()
            player.inventory.armorContents = inventoryArmor.toTypedArray()
            player.inventory.extraContents = inventoryExtra.toTypedArray()
        }

        if (restoreExperience) {
            player.exp = exp
            player.totalExperience = totalExperience
            player.level = level
        }

        if (restoreLocation)
            player.teleport(location)

        if (restoreRespawnLocation)
            player.respawnLocation = respawnLocation

        if (restoreGameMode)
            player.gameMode = gameMode

        if (restoreScoreboard)
            player.scoreboard = scoreboard

        if (restoreHealth)
            player.health = health

        if (restoreHunger) {
            player.foodLevel = foodLevel
            player.saturation = saturation
        }
    }
}
