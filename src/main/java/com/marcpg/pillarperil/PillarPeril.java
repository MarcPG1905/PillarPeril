package com.marcpg.pillarperil;

import com.marcpg.pillarperil.mode.Game;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class PillarPeril extends JavaPlugin {
    public static JavaPlugin PLUGIN;
    public static Logger LOG;
    public static Game currentGame;

    @Override
    public void onEnable() {
        PLUGIN = this;
        LOG = getLogger();
        Objects.requireNonNull(getCommand("game")).setExecutor(new GameCommand());
        getServer().getPluginManager().registerEvents(new Events(), this);
        LOG.info("Started Pillar Peril!");
    }

    @Override
    public void onDisable() {
        if (currentGame != null) {
            LOG.warning("Forcefully stopping the current game!");
            currentGame.forceEnd();
        }
        LOG.info("Stopped Pillar Peril!");
    }
}
