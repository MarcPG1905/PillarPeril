package com.marcpg.pillarperil

import com.marcpg.libpg.init.KotlinPlugin
import com.marcpg.libpg.init.KotlinPluginCompanion
import com.marcpg.libpg.util.ServerUtils
import com.marcpg.pillarperil.event.GameEvents
import com.marcpg.pillarperil.event.PlayerEvents
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.util.Configuration
import org.bukkit.Bukkit
import java.net.URI

class PillarPeril : KotlinPlugin(Companion) {
    companion object : KotlinPluginCompanion() {
        lateinit var PLUGIN: PillarPeril

        override val VERSION: String = "0.2.0"

        fun sendCommand(cmd: String) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
        }
    }

    @Suppress("UnstableApiUsage")
    override fun enable() {
        PLUGIN = this

        loadTranslations(URI("https://marcpg.com/pillar-peril/lang/all"))

        Registry.load()
        Configuration.init()

        addListeners(GameEvents, PlayerEvents)
        addCommands(
            ServerUtils.Cmd(Commands.game, "Utilities for managing the Pillar Peril games or starting new ones.", "pillar-peril", "match", "round"),
            ServerUtils.Cmd(Commands.queue, "Join, leave, and manage the Pillar Peril game queue if enabled."),
            ServerUtils.Cmd(Commands.ppConfig, "Manage the PillarPeril configuration.", "pillar-peril-config", "pp-settings"),
        )
    }

    override fun disable() {
        GameManager.games.values.toList().forEach { it.end(Game.EndingCause.FORCE) }
        Configuration.save()
    }
}
