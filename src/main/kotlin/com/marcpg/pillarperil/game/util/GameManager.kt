package com.marcpg.pillarperil.game.util

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.player.PillarPlayer
import org.bukkit.entity.Player

object GameManager {
    val games = mutableMapOf<String, Game>()

    val gamesStartedSinceLastFlush = mutableListOf<String>()

    fun add(game: Game) {
        games[game.id] = game
        gamesStartedSinceLastFlush.add(game.info.namespace)
    }

    fun remove(game: Game) {
        games.remove(game.id)
    }

    operator fun get(id: String): Game? = games[id]

    fun player(player: Player, onlyAlive: Boolean = true): PillarPlayer? =
        games.firstNotNullOfOrNull { it.value.player(player, onlyAlive) }

    fun isInGame(player: Player, onlyAlive: Boolean = true): Boolean =
        games.any { it.value.player(player, onlyAlive) != null }

    fun getClosestGame(location: Location): Game? {
        val world = location.world
        return games.values
            .filter { it.world == world }
            .associateBy { it.center.distance(location) }
            .filter { it.key < it.value.buildings.placedRadius * 2 }
            .minByOrNull { it.key }?.value
    }
}
