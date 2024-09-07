package com.marcpg.pillarperil.game.mode;

import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemOnlyMode extends Game {
    private static final GameInfo INFO = new GameInfo("item-only", Material::isItem);

    public ItemOnlyMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
