package com.marcpg.pillarperil.generation.generator;

import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;
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
        double radius = players * platformDistanceFactor / Math.TAU;

        for (int i = 0; i < players; i++) {
            double angle = Math.TAU * i / players;
            double x = center.x() + radius * Math.cos(angle);
            double z = center.z() + radius * Math.sin(angle);
            platform.place(x, z);
            locations.add(new Location(game.world, x, Platform.platformHeight + 1, z));
        }
        return locations;
    }
}
