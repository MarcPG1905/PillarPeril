package com.marcpg.pillarperil.util

import com.marcpg.pillarperil.PillarPeril
import com.marcpg.pillarperil.game.util.GameManager
import dev.faststats.bukkit.BukkitMetrics
import dev.faststats.core.ErrorTracker
import dev.faststats.core.SimpleMetrics
import dev.faststats.core.data.Metric

object Metrics {
    private var errorTracker: ErrorTracker? = null
    private var metrics: BukkitMetrics? = null

    fun start() {
        if (Configuration.disableFastStats) return

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
            .create(PillarPeril.PLUGIN)

        metrics?.ready()
    }

    fun logError(e: Throwable) {
        if (Configuration.disableFastStats) return

        errorTracker?.trackError(e)
    }

    fun forceSubmit() {
        if (Configuration.disableFastStats) return

        (metrics as SimpleMetrics?)?.submit()
    }

    fun shutdown() {
        metrics?.shutdown()
    }
}
