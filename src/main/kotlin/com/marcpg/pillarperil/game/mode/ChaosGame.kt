package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.Registry
import org.bukkit.entity.Player

class ChaosGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<ChaosGame> {
        override val gameConstructor: (String, Location, List<Player>) -> ChaosGame = { id, c, p -> ChaosGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "chaos") }
    }

    override val info: GameInfo = gameInfo

    init {
        items = Registry.ITEM.toList()
    }
}
