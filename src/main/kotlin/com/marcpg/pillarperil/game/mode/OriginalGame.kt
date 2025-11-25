package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class OriginalGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<OriginalGame> {
        override val gameConstructor: (String, Location, List<Player>) -> OriginalGame = { id, c, p -> OriginalGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "original") }
    }

    override val info: GameInfo = gameInfo
}
