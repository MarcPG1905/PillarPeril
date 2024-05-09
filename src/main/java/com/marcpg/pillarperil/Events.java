package com.marcpg.pillarperil;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.marcpg.pillarperil.game.modes.ChaosMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.marcpg.pillarperil.PillarPeril.currentGame;

public class Events implements Listener {
    @EventHandler
    public void onServerTickStart(ServerTickEndEvent event) {
        if (currentGame != null) {
            currentGame.onTick();
            if (event.getTickNumber() % 20 == 0) {
                currentGame.onSecond();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (currentGame != null) {
            currentGame.onDeath(event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!(currentGame instanceof ChaosMode) && event.getBlock().getY() >= 215) {
            event.setCancelled(true);
            return;
        }

        if (currentGame != null) {
            currentGame.onBlockPlace(event.getBlock());
        }
    }
}
