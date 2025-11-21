package com.marcpg.pillarperil

import com.marcpg.libpg.init.KotlinPlugin
import com.marcpg.libpg.init.KotlinPluginCompanion
import com.marcpg.libpg.util.ServerUtils
import com.marcpg.pillarperil.util.Configuration
import java.net.URI

class PillarPeril : KotlinPlugin(Companion) {
    companion object : KotlinPluginCompanion() {
        lateinit var PLUGIN: PillarPeril

        override val VERSION: String = "0.2.0"
    }

    @Suppress("UnstableApiUsage")
    override fun enable() {
        PLUGIN = this

        loadTranslations(URI("https://marcpg.com/pillar-peril/lang/all"))

        Registry.load()
        Configuration.init()

        addCommands(
            ServerUtils.Cmd(Commands.ppConfig, "Manage the PillarPeril configuration.", "pillar-peril-config", "pp-settings"),
        )
    }

    override fun disable() {
        Configuration.save()
    }
}
