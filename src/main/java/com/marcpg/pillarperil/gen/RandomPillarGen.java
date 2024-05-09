package com.marcpg.pillarperil.gen;

import com.marcpg.libpg.util.Randomizer;
import org.bukkit.Location;

import java.util.*;

public class RandomPillarGen extends Generator {
    public RandomPillarGen(Location center, int players) {
        super(center, players);
    }

    @Override
    public List<Location> generate() {
        List<Location> locations = new ArrayList<>();
        double radius = players * RADIUS_FACTOR * 1.2;

        Set<Location> candidates = new HashSet<>();

        for (int x = (int) -radius; x <= radius; x++) {
            for (int z = (int) -radius; z <= radius; z++) {
                Location loc = center.clone().add(x, 0, z);
                if (center.distance(loc) <= radius) {
                    candidates.add(loc);
                }
            }
        }


        for (int i = 0; i < players; i++) {
            Location loc = Randomizer.fromCollection(candidates);
            candidates.remove(loc);
            locations.add(loc);
            placePillar(loc.getWorld(), loc.getX(), loc.getZ());
        }

        return locations;
    }
}
