package com.marcpg.pillarperil.game.mode;

import com.marcpg.pillarperil.game.BossBarGame;
import com.marcpg.pillarperil.game.util.GameInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OriginalMode extends BossBarGame {
    private static final GameInfo INFO = new GameInfo("Original", "original", m -> true);

    public OriginalMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
