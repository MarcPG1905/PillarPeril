package com.marcpg.pillarperil

import com.marcpg.libpg.init.KotlinPlugin
import com.marcpg.libpg.init.KotlinPluginCompanion
import com.marcpg.libpg.util.ServerUtils
import com.marcpg.pillarperil.event.GameEvents
import com.marcpg.pillarperil.event.PlayerEvents
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.util.Configuration
import dev.faststats.bukkit.BukkitMetrics
import dev.faststats.core.ErrorTracker
import dev.faststats.core.SimpleMetrics
import dev.faststats.core.data.Metric
import org.bukkit.Bukkit
import java.net.URI

class PillarPeril : KotlinPlugin(Companion) {
    companion object : KotlinPluginCompanion() {
        lateinit var PLUGIN: PillarPeril

        override val VERSION: String = "0.2.1"

        fun sendCommand(cmd: String) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
        }
    }

    var errorTracker: ErrorTracker? = null
        private set

    private var metrics: BukkitMetrics? = null

    @Suppress("UnstableApiUsage")
    override fun enable() {
        saveDefaultConfig()

        PLUGIN = this

        loadTranslations(URI("https://marcpg.com/pillar-peril/lang/all"))

        Registry.load()
        Configuration.init()

        if (!Configuration.disableFastStats) {
            errorTracker = ErrorTracker.contextAware()

            metrics = BukkitMetrics.factory()
                .token("7dd02fd8606bfaa736aa6911e94edca7")

                .addMetric(Metric.number("games_running") { GameManager.games.size })
                .addMetric(Metric.stringArray("games_started") { GameManager.gamesStartedSinceLastFlush.toTypedArray() })
                .addMetric(Metric.stringArray("modifiers_used") { GameManager.modifiersUsedSinceLastFlush.toTypedArray() })
                .addMetric(Metric.numberArray("players_per_game") { GameManager.playersPerGameSinceLastFlush.toTypedArray() })

                .onFlush {
                    GameManager.gamesStartedSinceLastFlush.clear()
                    GameManager.modifiersUsedSinceLastFlush.clear()
                    GameManager.playersPerGameSinceLastFlush.clear()
                }

                .errorTracker(errorTracker)
                .create(this)
            metrics?.ready()
        }

        addListeners(GameEvents, PlayerEvents)
        addCommands(
            ServerUtils.Cmd(Commands.game, "Utilities for managing the Pillar Peril games or starting new ones.", "pillar-peril", "match", "round"),
            ServerUtils.Cmd(Commands.queue, "Join, leave, and manage the Pillar Peril game queue if enabled."),
            ServerUtils.Cmd(Commands.ppConfig, "Manage the PillarPeril configuration.", "pillar-peril-config", "pp-settings"),
        )
    }

    override fun disable() {
        (metrics as SimpleMetrics).submit()

        GameManager.games.values.toList().forEach { it.end(Game.EndingCause.FORCE) }
        Configuration.save()

        metrics?.shutdown()
    }
}
