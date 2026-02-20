package com.marcpg.pillarperil.game.util

import com.marcpg.libpg.config.ExtendedEntryTypes
import com.marcpg.libpg.data.time.Time
import com.marcpg.libpg.lang.string
import com.marcpg.pillarperil.PillarPeril
import com.marcpg.pillarperil.Registry
import com.marcpg.pillarperil.game.GameCompanion
import com.marcpg.pillarperil.generation.HorGenCompanion
import com.marcpg.pillarperil.generation.VertGenCompanion
import com.marcpg.pillarperil.util.Configuration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemType
import java.util.*

@Suppress("UnstableApiUsage")
data class GameInfo(
    val mode: GameCompanion<*>,
    val namespace: String,
    val itemCountdown: () -> Long = { Configuration.provider.getLong("modes.$namespace.cooldown", 5L) },
    val timeLimit: () -> Time = { ExtendedEntryTypes.time.convert(Configuration.provider.getSection("modes.$namespace.time-limit", mapOf("min" to 5))) ?: Time(5, Time.Unit.MINUTES) },

    val accentColor: () -> TextColor = { Configuration.provider.getString("modes.$namespace.visual.color", "#FFFFFF").fromHexToTextColor() },
    val showScoreboard: () -> Boolean = { Configuration.provider.getBoolean("modes.$namespace.visual.show-scoreboard") },
    val showActionBar: () -> Boolean = { Configuration.provider.getBoolean("modes.$namespace.visual.show-actionbar") },
    val showBossBar: () -> Boolean = { Configuration.provider.getBoolean("modes.$namespace.visual.show-bossbar") },

    val horGen: () -> HorGenCompanion<*> = { Configuration.provider.getString("modes.$namespace.generator.horizontal", "circular").toHorGen() },
    val vertGen: () -> VertGenCompanion<*> = { Configuration.provider.getString("modes.$namespace.generator.vertical", "pillar").toVertGen() },

    val additionalFilter: ((ItemType) -> Boolean) = { true },
) {
    val keyStyle: () -> Style = { Style.style(accentColor(), TextDecoration.BOLD) }
    val valueStyle = Style.style(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)

    fun name(locale: Locale) = locale.string("name.$namespace")
}

fun String?.fromHexToTextColor(): TextColor = (if (this == null) null else TextColor.fromHexString(this)) ?: NamedTextColor.WHITE

private fun String?.toHorGen(): HorGenCompanion<*> {
    val horGen = Registry.horizontalGenerators[this]
    if (horGen == null)
        PillarPeril.LOG.error("Configured horizontal generator '$this' does not exist!")
    return horGen ?: Registry.horizontalGenerators.values.first()
}

private fun String?.toVertGen(): VertGenCompanion<*> {
    val vertGen = Registry.verticalGenerators[this]
    if (vertGen == null)
        PillarPeril.LOG.error("Configured vertical generator '$this' does not exist!")
    return vertGen ?: Registry.verticalGenerators.values.first()
}
