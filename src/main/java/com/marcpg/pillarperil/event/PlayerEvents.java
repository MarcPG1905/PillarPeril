package com.marcpg.pillarperil.event;

import com.marcpg.pillarperil.PillarPlayer;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameManager;
import com.marcpg.pillarperil.generation.Generator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerEvents implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        PillarPlayer player = GameManager.player(event.getPlayer(), true);
        if (player != null) {
            if (player.player.getKiller() != null) {
                PillarPlayer killer = player.game.player(player.player.getKiller(), false);
                if (killer != null) killer.addKill();
            }

            player.game.eliminate(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo().y() < Generator.deathHeight) {
            Game game = GameManager.game(event.getPlayer(), true);
            if (game != null)
                event.getPlayer().setHealth(0.0);
        }
    }
}
