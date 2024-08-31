package com.marcpg.pillarperil.game.mode;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.game.BossBarGame;
import com.marcpg.pillarperil.game.util.GameInfo;
import com.marcpg.pillarperil.generation.CircularPillarGen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChaosMode extends BossBarGame {
    private static final GameInfo INFO = new GameInfo("Chaos", 3, new Time(8, Time.Unit.MINUTES), NamedTextColor.YELLOW, CircularPillarGen.class, m -> true);

    public ChaosMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
        this.items = List.of(Material.values());
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
