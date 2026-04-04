package com.marcpg.pillarperil.util

import com.marcpg.libpg.display.MinecraftReceiver
import com.marcpg.libpg.display.receiver
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.Player

fun Throwable.trackToFastStats() = Metrics.logError(this)

fun MinecraftReceiver.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: (() -> Boolean) = { true }) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.playSound(Registry.SOUNDS.getKeyOrThrow(sound), volume, pitch)
}

fun List<MinecraftReceiver>.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: (() -> Boolean) = { true }) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.receiver().playSound(Registry.SOUNDS.getKeyOrThrow(sound), volume, pitch)
}

fun Player.playSoundSafe(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f, requirement: (() -> Boolean) = { true }) {
    if (Configuration.soundEffectsEnabled && requirement())
        this.playSound(this, sound, volume, pitch)
}
