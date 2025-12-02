package com.marcpg.pillarperil.game.mode

import com.marcpg.libpg.data.time.Time
import com.marcpg.libpg.display.location
import com.marcpg.libpg.display.teleport
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.game.util.GameInfo
import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerShuffleGame(id: String, center: Location, bukkitPlayers: List<Player>) : Game(id, center, bukkitPlayers) {
    companion object : GameCompanion<PlayerShuffleGame> {
        override val gameConstructor: (String, Location, List<Player>) -> PlayerShuffleGame = { id, c, p -> PlayerShuffleGame(id, c, p) }
        override val gameInfo: GameInfo by lazy { GameInfo(this, "player-shuffle") }
    }

    override val info: GameInfo = gameInfo

    init {
        addTickEvent(Time(gameInfo.itemCountdown())) {
            val players = players.shuffled()

            val temp: Location = players.first().location().clone()
            for (i in 0 until players.size - 1) {
                players[i].teleport(players[i + 1].location())
            }
            players.last().teleport(temp)
        }
    }
}
