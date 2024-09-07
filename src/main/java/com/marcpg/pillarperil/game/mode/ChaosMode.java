package com.marcpg.pillarperil.game.mode;

import com.marcpg.pillarperil.game.BossBarGame;
import com.marcpg.pillarperil.game.util.GameInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ChaosMode extends BossBarGame {
    private static final GameInfo INFO = new GameInfo("chaos", m -> true);

    public ChaosMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
        this.items = Arrays.stream(Material.values()).filter(Material::isItem).toList(); // Without the filter, the console would be filled with exceptions.
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
