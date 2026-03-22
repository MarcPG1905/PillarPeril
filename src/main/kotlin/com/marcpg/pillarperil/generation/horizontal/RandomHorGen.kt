package com.marcpg.pillarperil.generation.horizontal

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.HorGenCompanion
import com.marcpg.pillarperil.generation.HorizontalGen
import org.bukkit.Location

class RandomHorGen(game: Game) : HorizontalGen(game) {
    companion object : HorGenCompanion<RandomHorGen> {
        override val namespace: String = "random"

        override fun constructGen(game: Game): RandomHorGen = RandomHorGen(game)
    }

    val players = game.players.size

    override fun generate(): List<Location> {
        game.radius *= 1.2

        val candidates = mutableSetOf<Location>()
        for (x in -game.radius.toInt() until game.radius.toInt()) {
            for (z in -game.radius.toInt() until game.radius.toInt()) {
                val loc = game.center.clone().add(x.toDouble(), 0.0, z.toDouble())
                if (game.center.distance(loc) <= game.radius)
                    candidates += loc
            }
        }

        val locations = mutableListOf<Location>()
        (0 until players).forEach { _ ->
            val loc = candidates.random()
            candidates -= loc
            locations += location(loc.x, loc.z)
        }
        return locations
    }
}
