package com.marcpg.pillarperil.generation.horizontal

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.HorGenCompanion
import com.marcpg.pillarperil.generation.HorizontalGen
import com.marcpg.pillarperil.util.Configuration
import org.bukkit.Location

class RandomHorGen(game: Game) : HorizontalGen(game) {
    companion object : HorGenCompanion<RandomHorGen> {
        override val genConstructor: (Game) -> RandomHorGen = { RandomHorGen(it) }
        override val namespace: String = "random"
    }

    val players = game.players.size

    override fun generate(): List<Location> {
        val radius = players * Configuration.platformDistanceFactor / Math.TAU * 1.2

        val candidates = mutableSetOf<Location>()

        for (x in -radius.toInt() until radius.toInt()) {
            for (z in -radius.toInt() until radius.toInt()) {
                val loc = game.center.clone().add(x.toDouble(), 0.0, z.toDouble())
                if (game.center.distance(loc) <= radius)
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
