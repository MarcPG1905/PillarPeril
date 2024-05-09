package com.marcpg.pillarperil.game.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemOnlyMode extends Game {
    private static final Time itemCooldown = new Time(10);
    private static final Time limit = new Time(4, Time.Unit.MINUTES);

    public ItemOnlyMode(Location center, @NotNull List<Player> players) {
        super(players, new CircularPillarGen(center, players.size(), Material.SLIME_BLOCK), Material::isItem);
    }

    @Override
    public Time itemCooldown() {
        return itemCooldown;
    }

    @Override
    public Time timeLimit() {
        return limit;
    }
}
