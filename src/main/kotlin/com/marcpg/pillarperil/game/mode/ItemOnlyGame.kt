package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class ItemOnlyGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<ItemOnlyGame> {
        override val gameConstructor: (String, Location, List<Player>) -> ItemOnlyGame = { id, c, p -> ItemOnlyGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "item-only") { !it.hasBlockType() } }
    }

    override val info: GameInfo = gameInfo
}
