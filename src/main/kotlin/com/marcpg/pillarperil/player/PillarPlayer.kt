package com.marcpg.pillarperil.player

import com.marcpg.libpg.display.PlayerMinecraftReceiver
import com.marcpg.libpg.display.SimpleActionBar
import com.marcpg.libpg.display.SimpleScoreboard
import com.marcpg.libpg.display.start
import com.marcpg.libpg.util.bukkitRunLater
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.util.QueueManager
import com.marcpg.pillarperil.util.Configuration
import com.marcpg.pillarperil.util.QueueMethod
import com.marcpg.pillarperil.util.playSoundSafe
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemType

class PillarPlayer(player: Player, val game: Game) : PlayerMinecraftReceiver(player) {
    var simpleScoreboard: SimpleScoreboard? = null
    var simpleActionBar: SimpleActionBar? = null

    var kills: Int = 0
    var deathTime: Int? = null

    val initialSnapshot = PlayerSnapshot(player)

    init {
        if (game.info.showScoreboard()) {
            try {
                simpleScoreboard = game.scoreboard?.invoke(this)
                simpleScoreboard!!.start()
            } catch (e: Exception) {
                game.error("Could not create and initialize scoreboard for $this.", e)
            }
        }

        if (game.info.showActionBar()) {
            try {
                simpleActionBar = game.actionBar?.invoke(this)
                simpleActionBar!!.start()
            } catch (e: Exception) {
                game.error("Could not create and initialize action bar for $this.", e)
            }
        }
    }

    fun giveItems(available: Collection<ItemType>, differentItems: Int = 1) {
        repeat(differentItems) {
            var item = available.random().createItemStack()
            for (modifier in game.modifiers) {
                item = modifier.onItemReceive(item)
            }

            player.inventory.addItem(item)
        }
        player.playSoundSafe(Sound.ENTITY_ITEM_PICKUP, 0.75f) { Configuration.soundEffectsItem }
    }

    fun clear(display: Boolean = false) {
        if (display) {
            simpleScoreboard?.stop()
            simpleActionBar?.stop()

            player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        }

        player.closeInventory()
        player.inventory.clear()
        player.clearActivePotionEffects()
        initialSnapshot.set(player, restoreGameMode = false, restoreLocation = false)

        player.gameMode = Configuration.spawnGameMode
        player.teleport(Configuration.spawnLocation)

        if (Configuration.queueMethod == QueueMethod.AUTO)
            bukkitRunLater(60L) { QueueManager.add(player) } // Wait 3 seconds before rejoining queue.
    }

    fun eliminate() = game.eliminate(this)
}
