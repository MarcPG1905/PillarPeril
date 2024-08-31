package com.marcpg.pillarperil;

import com.marcpg.pillarperil.event.GameEvents;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PillarPeril extends JavaPlugin {
    public static PillarPeril PLUGIN;
    public static Logger LOG;
    public static FileConfiguration CONFIG;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PLUGIN = this;
        LOG = getSLF4JLogger();
        CONFIG = getConfig();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(Commands.games(), "Utilities for managing the Pillar Peril games or starting new ones.", List.of("pillarperil", "matches", "game-manager"));
        });

        getServer().getPluginManager().registerEvents(new GameEvents(), this);
    }

    @Override
    public void onDisable() {
        GameManager.GAMES.forEach(game -> game.end(Game.EndingCause.FORCE, List.of()));
    }
}
