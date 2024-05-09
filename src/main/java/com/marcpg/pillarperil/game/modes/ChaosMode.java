package com.marcpg.pillarperil.game.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.RandomPillarGen;
import com.marcpg.pillarperil.game.VisibleCooldownGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChaosMode extends VisibleCooldownGame {
    private static final Time itemCooldown = new Time(3);
    private static final Time limit = new Time(8, Time.Unit.MINUTES);

    public ChaosMode(Location center, @NotNull List<Player> players) {
        super(players, new RandomPillarGen(center, players.size()), m -> true);
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
