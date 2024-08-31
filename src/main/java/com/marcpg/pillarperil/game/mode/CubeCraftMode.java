package com.marcpg.pillarperil.game.mode;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameInfo;
import com.marcpg.pillarperil.generation.CircularPillarGen;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CubeCraftMode extends Game {
    private static final GameInfo INFO = new GameInfo("CubeCraft", 5, new Time(5, Time.Unit.MINUTES), TextColor.color(0x53A4FF), CircularPillarGen.class, m -> !m.name().contains("BOAT") && hasUse(m));

    public CubeCraftMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
