package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.util.Configuration

abstract class VerticalGen(val game: Game) {
    abstract fun generate(x: Double, z: Double)

    protected fun execPlace(x: Double, y: Double, z: Double) {
        game.buildings.placeBlock(x, y, z, Configuration.platformMaterial)
    }
}

interface VertGenCompanion<T : VerticalGen> {
    val genConstructor: (Game) -> T
    val namespace: String
}
