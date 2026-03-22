package com.marcpg.pillarperil.game

import com.marcpg.pillarperil.game.util.GameModifierInfo
import com.marcpg.pillarperil.player.PillarPlayer
import com.marcpg.pillarperil.util.Ticking
import org.bukkit.inventory.ItemStack

abstract class GameModifier(
    val game: Game,
) : Ticking {
    abstract val info: GameModifierInfo

    open fun init() {}
    open fun customBuild() {}
    override fun tick(tick: Ticking.Tick) {}

    open fun onItemCycle() {}
    open fun onItemReceive(item: ItemStack): ItemStack = item
    open fun onPlayerDeath(player: PillarPlayer) {}
    open fun onPostPlayerDeath(player: PillarPlayer) {}
}
