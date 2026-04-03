package com.marcpg.pillarperil.game.util

import com.marcpg.libpg.lang.string
import com.marcpg.pillarperil.game.GameModifierCompanion
import java.util.*

@Suppress("UnstableApiUsage")
data class GameModifierInfo(
    val mode: GameModifierCompanion<*>,
    val namespace: String,
) {
    fun name(locale: Locale) = locale.string("modifier.$namespace.name")
    fun description(locale: Locale) = locale.string("modifier.$namespace.description")
}
