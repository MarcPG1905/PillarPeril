package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class CubeCraftGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<CubeCraftGame> {
        override val gameConstructor: (String, Location, List<Player>) -> CubeCraftGame = { id, c, p -> CubeCraftGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "cubecraft") { "boat" in it.translationKey() && (!it.hasBlockType() || it.blockType.isSolid) } }
    }

    override val info: GameInfo = gameInfo
}
