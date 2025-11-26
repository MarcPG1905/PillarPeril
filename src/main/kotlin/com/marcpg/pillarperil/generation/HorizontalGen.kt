package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.util.Configuration
import org.bukkit.Location

abstract class HorizontalGen(val game: Game) {
    abstract fun generate(): List<Location>

    protected fun location(x: Double, z: Double): Location = Location(game.world, x, Configuration.platformHeight, z)
}

interface HorGenCompanion<T : HorizontalGen> {
    val genConstructor: (Game) -> T
    val namespace: String
}
