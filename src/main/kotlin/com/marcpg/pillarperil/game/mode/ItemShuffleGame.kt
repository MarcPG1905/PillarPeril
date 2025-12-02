package com.marcpg.pillarperil.game.mode

import com.marcpg.libpg.data.time.Time
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class ItemShuffleGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<ItemShuffleGame> {
        override val gameConstructor: (String, Location, List<Player>) -> ItemShuffleGame = { id, c, p -> ItemShuffleGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "item-shuffle") }
    }

    override val info: GameInfo = gameInfo

    init {
        addTickEvent(Time(gameInfo.itemCountdown())) {
            players.forEach { p ->
                p.player.inventory.clear()
                p.giveItems(items, differentItems = 10)
            }
        }

        players.forEach { it.giveItems(items) }
    }
}
