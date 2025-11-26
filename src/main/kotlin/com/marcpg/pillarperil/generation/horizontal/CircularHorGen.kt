package com.marcpg.pillarperil.generation.horizontal

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.generation.HorGenCompanion
import com.marcpg.pillarperil.generation.HorizontalGen
import com.marcpg.pillarperil.util.Configuration
import org.bukkit.Location
import kotlin.math.cos
import kotlin.math.sin

class CircularHorGen(game: Game) : HorizontalGen(game) {
    companion object : HorGenCompanion<CircularHorGen> {
        override val genConstructor: (Game) -> CircularHorGen = { CircularHorGen(it) }
        override val namespace: String = "circular"
    }

    val players = game.players.size

    override fun generate(): List<Location> {
        val radius = players * Configuration.platformDistanceFactor / Math.TAU

        val locations = mutableListOf<Location>()
        for (i in 0 until players) {
            val angle = Math.TAU * i / players
            locations += location(
                game.center.x + radius * cos(angle),
                game.center.z + radius * sin(angle)
            )
        }
        return locations
    }
}
