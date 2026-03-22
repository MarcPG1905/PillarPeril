package com.marcpg.pillarperil.generation.vertical

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.VertGenCompanion
import com.marcpg.pillarperil.generation.VerticalGen
import com.marcpg.pillarperil.util.Configuration

class BlockVertGen(game: Game) : VerticalGen(game) {
    companion object : VertGenCompanion<BlockVertGen> {
        override val namespace: String = "block"

        override fun constructGen(game: Game): BlockVertGen = BlockVertGen(game)
    }

    override fun generate(x: Double, z: Double) = execPlace(x, Configuration.platformHeight, z)
}
