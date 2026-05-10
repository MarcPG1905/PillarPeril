package com.marcpg.pillarperil

import com.marcpg.libpg.color.Ansi
import com.marcpg.libpg.init.KotlinPlugin
import com.marcpg.libpg.init.KotlinPluginCompanion
import com.marcpg.libpg.util.MinecraftUpdateChecker
import com.marcpg.libpg.util.ServerUtils
import com.marcpg.pillarperil.event.GameEvents
import com.marcpg.pillarperil.event.PlayerEvents
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.util.Configuration
import com.marcpg.pillarperil.util.Metrics
import io.papermc.paper.ServerBuildInfo
import org.bukkit.Bukkit
import java.net.URI

class PillarPeril : KotlinPlugin(Companion) {
    companion object : KotlinPluginCompanion() {
        lateinit var PLUGIN: PillarPeril

        override val VERSION: String = "0.2.2"

        fun sendCommand(cmd: String) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
        }
    }

    @Suppress("UnstableApiUsage")
    override fun enable() {
        saveDefaultConfig()

        PLUGIN = this

        loadTranslations(URI("https://marcpg.com/pillar-peril/lang/all"))

        checkUpdates()

        Registry.load()
        Configuration.init()
        Metrics.start()

        addListeners(GameEvents, PlayerEvents)
        addCommands(
            ServerUtils.Cmd(Commands.game, "Utilities for managing the Pillar Peril games or starting new ones.", "pillar-peril", "match", "round"),
            ServerUtils.Cmd(Commands.queue, "Join, leave, and manage the Pillar Peril game queue if enabled."),
            ServerUtils.Cmd(Commands.ppConfig, "Manage the PillarPeril configuration.", "pillar-peril-config", "pp-settings"),
        )
    }

    private fun checkUpdates() {
        val currentVersion = MinecraftUpdateChecker.Version(VERSION, "Pillar Peril $VERSION", "release", "ERROR")
        val result = MinecraftUpdateChecker.checkForUpdates(
            source = MinecraftUpdateChecker.Source.MODRINTH,
            projectId = "pillarperil",
            loaders = listOf("paper"),
            gameVersions = listOf(ServerBuildInfo.buildInfo().minecraftVersionId()),
            currentVersion = currentVersion
        )

        when (result.type) {
            MinecraftUpdateChecker.Result.Type.UP_TO_DATE -> {
                LOG.info("You're on the latest Pillar Peril version!")
            }
            MinecraftUpdateChecker.Result.Type.OUTDATED -> {
                LOG.warn("You're ${result.amount} version(s) behind, on $currentVersion!")
                LOG.warn("Latest version is \"${result.latest}\". Update at ${result.latest?.link}")
            }
            MinecraftUpdateChecker.Result.Type.UNAVAILABLE -> {
                LOG.info(Ansi.gray("You're running a version which is not available on Modrinth — cannot check for updates."))
            }
        }
    }

    override fun disable() {
        Metrics.forceSubmit()

        GameManager.games.values.toList().forEach { it.end(Game.EndingCause.FORCE) }
        Configuration.save()

        Metrics.shutdown()
    }
}
