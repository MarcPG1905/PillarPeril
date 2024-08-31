package com.marcpg.pillarperil.game.mode;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameInfo;
import com.marcpg.pillarperil.generation.CircularPillarGen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemOnlyMode extends Game {
    private static final GameInfo INFO = new GameInfo("Item-Only", 10, new Time(4, Time.Unit.MINUTES), NamedTextColor.YELLOW, CircularPillarGen.class, Material::isItem);

    public ItemOnlyMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
