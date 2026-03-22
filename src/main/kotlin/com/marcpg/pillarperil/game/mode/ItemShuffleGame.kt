package com.marcpg.pillarperil.game.mode

import com.marcpg.libpg.data.time.Time
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.GameModifier
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class ItemShuffleGame(id: String, center: Location, bukkitPlayers: List<Player>, modifiers: List<GameModifier>) : Game(id, center, bukkitPlayers, modifiers) {
    companion object : GameCompanion<ItemShuffleGame> {
        override val gameInfo: GameInfo by lazy { GameInfo(this, "item-shuffle") }

        override fun constructGame(id: String, center: Location, bukkitPlayers: List<Player>, modifiers: List<GameModifier>): ItemShuffleGame {
            return ItemShuffleGame(id, center, bukkitPlayers, modifiers)
        }
    }

    override val info: GameInfo = gameInfo

    init {
        addTickEvent(Time(gameInfo.itemCountdown())) {
            players.forEach { p ->
                p.player.inventory.clear()
                p.giveItems(items, differentItems = 9)
            }
        }

        players.forEach { it.giveItems(items) }
    }
}
