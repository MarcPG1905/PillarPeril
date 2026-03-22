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
    val simpleScoreboard: SimpleScoreboard? = if (game.info.showScoreboard()) game.scoreboard?.invoke(this) else null
    val simpleActionBar: SimpleActionBar? = if (game.info.showActionBar()) game.actionBar?.invoke(this) else null

    var kills: Int = 0
    var deathTime: Int? = null

    val initialSnapshot = PlayerSnapshot(player)

    init {
        simpleScoreboard?.start()
        simpleActionBar?.start()
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
