package com.marcpg.pillarperil.game

import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

interface GameCompanion<T : Game> {
    val gameConstructor: (String, Location, List<Player>) -> T
    val gameInfo: GameInfo
}
