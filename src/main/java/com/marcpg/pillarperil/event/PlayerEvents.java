package com.marcpg.pillarperil.event;

import com.marcpg.pillarperil.Config;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerEvents implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Game game = GameManager.game(event.getPlayer(), true);
        if (game != null) return;
        // TODO: game.eliminate(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo().y() < Config.deathHeight) {
            Game game = GameManager.game(event.getPlayer(), true);
            if (game != null)
                event.getPlayer().setHealth(0.0);
        }
    }
}
