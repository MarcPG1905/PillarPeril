package com.marcpg.pillarperil.game.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.game.VisibleCooldownGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class OriginalMode extends VisibleCooldownGame {
    private static final Time itemCooldown = new Time(5);
    private static final Time limit = new Time(5, Time.Unit.MINUTES);

    public OriginalMode(Location center, List<Player> players) {
        super(players, new CircularPillarGen(center, players.size()), m -> true);
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
