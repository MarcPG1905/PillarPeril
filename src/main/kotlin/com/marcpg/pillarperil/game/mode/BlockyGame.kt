package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BlockyGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<BlockyGame> {
        override val gameConstructor: (String, Location, List<Player>) -> BlockyGame = { id, c, p -> BlockyGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "blocky") { it.hasBlockType() && it.blockType.isSolid } }

        val attackItems = listOf(
            Material.STICK,         Material.TRIDENT,
            Material.STONE_SWORD,   Material.IRON_SWORD,    Material.GOLDEN_SWORD,      Material.DIAMOND_SWORD,     Material.NETHERITE_SWORD,
            Material.STONE_AXE,     Material.IRON_AXE,      Material.GOLDEN_AXE,        Material.DIAMOND_AXE,       Material.NETHERITE_AXE,
            Material.STONE_PICKAXE, Material.IRON_PICKAXE,  Material.GOLDEN_PICKAXE,    Material.DIAMOND_PICKAXE,   Material.NETHERITE_PICKAXE,
            Material.STONE_SHOVEL,  Material.IRON_SHOVEL,   Material.GOLDEN_SHOVEL,     Material.DIAMOND_SHOVEL,    Material.NETHERITE_SHOVEL
        )
    }

    override val info: GameInfo = gameInfo

    init {
        bukkitPlayers.forEach { it.inventory.addItem(ItemStack.of(attackItems.random())) }
    }
}
