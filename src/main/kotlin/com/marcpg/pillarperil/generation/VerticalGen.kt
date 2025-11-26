package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.game.Game
import org.bukkit.Location
import org.bukkit.Material

abstract class VerticalGen(val game: Game) {
    abstract fun generate(x: Double, z: Double)

    protected fun execPlace(x: Double, y: Double, z: Double) {
        val loc = Location(game.world, x, y, z)
        game.buildings.place(loc)
        loc.block.type = Material.BEDROCK
    }
}

interface VertGenCompanion<T : VerticalGen> {
    val genConstructor: (Game) -> T
    val namespace: String
}
