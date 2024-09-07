package com.marcpg.pillarperil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcpg.libpg.lang.Translation;
import com.marcpg.pillarperil.event.GameEvents;
import com.marcpg.pillarperil.event.PlayerEvents;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameManager;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class PillarPeril extends JavaPlugin {
    public static PillarPeril PLUGIN;
    public static Logger LOG;
    public static FileConfiguration CONFIG;

    @Override
    public void onEnable() {
        Locale.setDefault(Locale.of("en", "US"));
        saveDefaultConfig();

        PLUGIN = this;
        LOG = getSLF4JLogger();
        CONFIG = getConfig();

        try {
            translations();
        } catch (Exception e) {
            LOG.error("Could not load translations: {}", e.getMessage());
        }

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(Commands.games(), "Utilities for managing the Pillar Peril games or starting new ones.", List.of("pillarperil", "matches", "game-manager")));

        getServer().getPluginManager().registerEvents(new GameEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        Platform.platformHeight = CONFIG.getInt("platform-height");
        Platform.deathHeight = Platform.platformHeight - CONFIG.getInt("max-fall");
        Generator.platformDistanceFactor = CONFIG.getDouble("platform-distance-factor");
    }

    @Override
    public void onDisable() {
        // Need to create copy, because you can't loop over a list while removing values from it.
        List.copyOf(GameManager.GAMES).forEach(game -> game.end(Game.EndingCause.FORCE, List.of()));
    }

    void translations() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI("https://marcpg.com/pillar-peril/lang/all")).GET().build();
        String response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
        Translation.loadMaps(new Gson().fromJson(response, new TypeToken<Map<Locale, Map<String, String>>>(){}.getType()));
    }

    public static Locale locale(Audience a) {
        return a instanceof Player p ? p.locale() : a instanceof PillarPlayer pp ? pp.locale() : Locale.getDefault();
    }
}
