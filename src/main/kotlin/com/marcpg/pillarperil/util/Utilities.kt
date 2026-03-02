package com.marcpg.pillarperil.util

import com.marcpg.libpg.display.MinecraftReceiver
import com.marcpg.libpg.display.receiver
import com.marcpg.pillarperil.PillarPeril
import org.bukkit.GameRule
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player

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

fun MinecraftReceiver.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: () -> Boolean) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.playSound(Registry.SOUNDS.getKeyOrThrow(sound), volume, pitch)
}

fun List<MinecraftReceiver>.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: () -> Boolean) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.receiver().playSound(Registry.SOUNDS.getKeyOrThrow(sound), volume, pitch)
}

fun Player.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: () -> Boolean) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.playSound(this, sound, volume, pitch)
}
