package com.marcpg.pillarperil.generation.vertical

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.VertGenCompanion
import com.marcpg.pillarperil.generation.VerticalGen
import com.marcpg.pillarperil.util.Configuration

class BlockVertGen(game: Game) : VerticalGen(game) {
    companion object : VertGenCompanion<BlockVertGen> {
        override val genConstructor: (Game) -> BlockVertGen = { BlockVertGen(it) }
        override val namespace: String = "block"
    }

    override fun generate(x: Double, z: Double) = execPlace(x, Configuration.platformHeight, z)
}
