package com.marcpg.pillarperil.game.mode

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class ClassicGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<ClassicGame> {
        override val gameConstructor: (String, Location, List<Player>) -> ClassicGame = { id, c, p -> ClassicGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "classic") { "boat" !in it.translationKey() && (!it.hasBlockType() || it.blockType.isSolid) } }
    }

    override val info: GameInfo = gameInfo
}
