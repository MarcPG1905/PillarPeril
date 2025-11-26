package com.marcpg.pillarperil.generation.vertical

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.VertGenCompanion
import com.marcpg.pillarperil.generation.VerticalGen
import com.marcpg.pillarperil.util.Configuration

class PillarVertGen(game: Game) : VerticalGen(game) {
    companion object : VertGenCompanion<PillarVertGen> {
        override val genConstructor: (Game) -> PillarVertGen = { PillarVertGen(it) }
        override val namespace: String = "pillar"
    }

    override fun generate(x: Double, z: Double) {
        for (y in game.world.minHeight..Configuration.platformHeight.toInt())
            execPlace(x, y.toDouble(), z)
    }
}
