package com.marcpg.pillarperil.generation;

import com.marcpg.pillarperil.Config;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CircularPillarGen extends Generator {
    public CircularPillarGen(Game game, Location center, int players) {
        super(game, center, players);
    }

    @Override
    public List<Location> generate() {
        List<Location> locations = new ArrayList<>();
        double radius = players * RADIUS_FACTOR;

        for (int i = 0; i < players; i++) {
            double angle = TAU * i / players;
            double x = center.x() + radius * Math.cos(angle);
            double z = center.z() + radius * Math.sin(angle);
            placePillar(x, z);
            locations.add(new Location(game.world, x, Config.pillarHeight + 1, z));
        }
        return locations;
    }
}
