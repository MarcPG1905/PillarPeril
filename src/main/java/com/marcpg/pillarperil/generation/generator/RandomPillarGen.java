package com.marcpg.pillarperil.generation.generator;

import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomPillarGen extends Generator {
    public RandomPillarGen(Game game, Location center, int players) {
        super(game, center, players);
    }

    @Override
    public List<Location> generate() {
        double radius = players * platformDistanceFactor / Math.TAU * 1.2;
        Set<Location> candidates = new HashSet<>();

        for (int x = (int) -radius; x <= radius; x++) {
            for (int z = (int) -radius; z <= radius; z++) {
                Location loc = center.clone().add(x, 0, z);
                if (center.distance(loc) <= radius) {
                    candidates.add(loc);
                }
            }
        }

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            Location loc = Randomizer.fromCollection(candidates);
            candidates.remove(loc);
            locations.add(new Location(game.world, loc.x(), Platform.platformHeight + 1, loc.z()));
            platform.place(loc.getX(), loc.getZ());
        }
        return locations;
    }
}
