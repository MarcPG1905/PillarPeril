package com.marcpg.pillarperil.game

import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

interface GameCompanion<T : Game> {
    val gameInfo: GameInfo

    fun constructGame(id: String, center: Location, bukkitPlayers: List<Player>, modifiers: List<GameModifier>): T
}
