package com.marcpg.pillarperil.game.util

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.player.PillarPlayer
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object GameManager {
    val games = mutableMapOf<String, Game>()

    /*
     * Suggestions for further Metrics:
     *
     * - Death Causes
     * - Player Death Times
     * - Items:
     *   - Items Received:Dropped Ratio
     *   - Item Types Dropped
     */
    val gamesStartedSinceLastFlush = mutableListOf<String>()
    val modifiersUsedSinceLastFlush = mutableListOf<String>()
    val playersPerGameSinceLastFlush = mutableListOf<Int>()

    fun add(game: Game) {
        games[game.id] = game

        gamesStartedSinceLastFlush.add(game.info.namespace)
        modifiersUsedSinceLastFlush.addAll(game.modifiers.map { it.info.namespace })
        playersPerGameSinceLastFlush.add(game.initialPlayers.size)
    }

    fun remove(game: Game) {
        games.remove(game.id)
    }

    operator fun get(id: String): Game? = games[id]

    fun player(player: Player, onlyAlive: Boolean = true): PillarPlayer? =
        games.firstNotNullOfOrNull { it.value.player(player, onlyAlive) }

    fun isInGame(player: Player, onlyAlive: Boolean = true): Boolean =
        games.any { it.value.player(player, onlyAlive) != null }

    fun isPartOfGame(entity: Entity): Boolean {
        return if (entity is Player) {
            isInGame(entity)
        } else {
            games.any { entity in it.value.buildings.spawnedEntities }
        }
    }

    fun isWithinGame(location: Location): Boolean {
        val world = location.world
        return games.any { it.value.world == world && it.value.center.distance(location) < it.value.buildings.placedRadius * 2 }
    }

    fun getClosestGame(location: Location): Game? {
        val world = location.world
        return games.values
            .filter { it.world == world }
            .associateBy { it.center.distance(location) }
            .filter { it.key < it.value.buildings.placedRadius * 2 }
            .minByOrNull { it.key }?.value
    }
}
