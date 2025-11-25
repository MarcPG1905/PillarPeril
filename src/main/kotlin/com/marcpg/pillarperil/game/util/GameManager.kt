package com.marcpg.pillarperil.game.util

import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.player.PillarPlayer
import org.bukkit.entity.Player

object GameManager {
    val games = mutableMapOf<String, Game>()

    fun add(game: Game) {
        games[game.id] = game
    }

    fun remove(game: Game) {
        games.remove(game.id)
    }

    operator fun get(id: String): Game? = games[id]

    fun player(player: Player, onlyAlive: Boolean = true): PillarPlayer? =
        games.firstNotNullOfOrNull { it.value.player(player, onlyAlive) }

    fun isInGame(player: Player, onlyAlive: Boolean = true): Boolean =
        games.any { it.value.player(player, onlyAlive) != null }
}
