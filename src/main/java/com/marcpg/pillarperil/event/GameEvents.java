package com.marcpg.pillarperil.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class GameEvents implements Listener {
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        GameManager.GAMES.forEach(game -> game.tick(event.getTickNumber()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Game game = GameManager.game(event.getPlayer(), true);
        if (game != null)
            game.addBlock(event.getBlockPlaced().getLocation(), event.getBlockReplacedState().getBlockData());
    }
}
