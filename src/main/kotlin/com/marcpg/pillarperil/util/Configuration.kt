package com.marcpg.pillarperil.util

import com.google.common.base.Optional
import com.marcpg.libpg.config.*
import com.marcpg.libpg.storing.Cord
import com.marcpg.libpg.util.toLocation
import com.marcpg.pillarperil.PillarPeril
import com.marcpg.pillarperil.Registry
import com.marcpg.pillarperil.game.mode.OriginalGame
import org.bukkit.GameMode
import org.bukkit.World

object Configuration : Config(PaperConfigProvider()) {
    override val versionHistory: List<ConfigVersion> = listOf(
        ConfigVersion(id = 2)
    )
    override val version: Int = 2

    var platformHeight by double("platform-height", 200.0)
    var maxFall by double("max-fall", 25.0)
    var platformDistanceFactor by double("platform-distance-factor", 10.0)
    var enableDraws by boolean("enable-draws")
    var endingCommands by custom("ending-commands", PPEntryTypes.placeholder.list, listOf())
    var respawnAtConfig by boolean("respawn-at-config")

    var spawnGameMode by enum<GameMode>("player-spawn.game-mode", GameMode.ADVENTURE)
    var spawnWorld by custom("player-spawn.world", PaperEntryTypes.world, Optional.absent())
    var spawnCord by custom("player-spawn.location", PaperEntryTypes.cord, Cord(0.0, -64.0, 0.0))

    var queueEnabled by boolean("queue.enabled")
    var queueMinPlayers by int("queue.min-players", 3)
    var queueMaxPlayers by int("queue.max-players", 16)
    var queueCheckIntervalSecs by int("queue.check-interval", 30)
    var queueMethod by enum<QueueMethod>("queue.method", QueueMethod.COMMAND)
    var queueMode by custom("queue.mode", PPEntryTypes.registry { Registry.modes }, OriginalGame)
    var queueWorldName by custom("queue.world", PPEntryTypes.placeholder, PlaceholderNameGetter("game-{id}"))
    var queueCord by custom("queue.location", PaperEntryTypes.cord, Cord(0.0, -64.0, 0.0))
    var queuePreCommands by custom("queue.pre-commands", PPEntryTypes.placeholder.list, listOf())
    var queuePostCommands by custom("queue.post-commands", PPEntryTypes.placeholder.list, listOf())

    val deathHeight get() = platformHeight - maxFall
    val spawnLocation get() = if (spawnCord.y == -64.0) spawnWorld.get().spawnLocation else spawnCord.toLocation(spawnWorld.get())
    val queueCheckInterval get() = queueCheckIntervalSecs * 20
    fun queueLocation(world: World) = if (queueCord.y == -64.0) world.spawnLocation else queueCord.toLocation(world)

    fun init() {
        val result = loadChecking()

        result.second.forEach { PillarPeril.LOG.error(it) }

        when (result.first) {
            ConfigLoadResult.LOADED -> PillarPeril.LOG.info("Configuration loaded.")
            ConfigLoadResult.CREATED -> PillarPeril.LOG.info("Configuration has been created.")
            ConfigLoadResult.UPDATED -> {
                PillarPeril.LOG.warn("============================= ! NOTE ! =========================")
                PillarPeril.LOG.warn("| The config has been updated and may need to be reconfigured. |")
                PillarPeril.LOG.warn("|    The old config has been backed up as 'config.yml.old'.    |")
                PillarPeril.LOG.warn("================================================================")
            }
            ConfigLoadResult.UPDATED_AND_MIGRATED -> {
                PillarPeril.LOG.info("============================= ! NOTE ! =======================")
                PillarPeril.LOG.info("| The config has been updated and was successfully migrated. |")
                PillarPeril.LOG.info("|   The old config has been backed up as 'config.yml.old'.   |")
                PillarPeril.LOG.info("==============================================================")
            }
        }

        save()
    }

    fun loadChecking(): Pair<ConfigLoadResult, List<String>> {
        val result = load() to mutableListOf<String>()

        if (queueCheckIntervalSecs < 1 && queueCheckIntervalSecs != -1)
            result.second += "Invalid value $queueCheckIntervalSecs for configuration key 'queue.check-interval'."

        return result
    }
}

object PPEntryTypes {
    val placeholder = CustomEntryType(
        BaseEntryTypes.string,
        { PlaceholderNameGetter(it) },
        { it.base }
    )

    fun <T> registry(entries: () -> Map<String, T>) = CustomEntryType(
        BaseEntryTypes.string,
        { entries()[it]!! },
        { entries().entries.first { e -> e.value == it }.key }
    )
}

enum class QueueMethod { COMMAND, AUTO }
