package com.marcpg.pillarperil.util

import com.marcpg.pillarperil.PillarPeril
import org.bukkit.GameRule
import org.bukkit.World

fun Throwable.trackToFastStats() {
    if (Configuration.disableFastStats) return

    PillarPeril.PLUGIN.errorTracker?.trackError(this)
}

fun <T : Any> World.setGameRuleSafe(oldName: String, newName: String, value: T) {
    val field = try {
        // New game-rule system from 1.21.9, 1.21.10, or 1.21.11, not sure when exactly it got added:
        Class.forName("org.bukkit.GameRules").getField(newName)
    } catch (_: Exception) {
        Class.forName("org.bukkit.GameRule").getField(oldName)
    }
    @Suppress("UNCHECKED_CAST")
    setGameRule(field.get(null) as GameRule<T>, value)
}
