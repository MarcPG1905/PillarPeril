package com.marcpg.pillarperil.game.mode

import com.marcpg.libpg.display.location
import com.marcpg.libpg.display.teleport
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.GameModifier
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerShuffleGame(id: String, center: Location, bukkitPlayers: List<Player>, modifiers: List<GameModifier>) : Game(id, center, bukkitPlayers, modifiers) {
    companion object : GameCompanion<PlayerShuffleGame> {
        override val gameInfo: GameInfo by lazy { GameInfo(this, "player-shuffle") }

        override fun constructGame(id: String, center: Location, bukkitPlayers: List<Player>, modifiers: List<GameModifier>): PlayerShuffleGame {
            return PlayerShuffleGame(id, center, bukkitPlayers, modifiers)
        }
    }

    override val info: GameInfo = gameInfo

    init {
        addItemEvent {
            val players = players.shuffled()

            if (players.isEmpty()) return@addItemEvent

            val temp: Location = players.first().location().clone()
            for (i in 0..<players.size - 1) {
                players[i].teleport(players[i + 1].location())
            }
            players.last().teleport(temp)
        }
    }
}
