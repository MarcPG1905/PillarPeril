package com.marcpg.pillarperil.generation;

import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.game.Game;
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
        double radius = players * pillarDistanceFactor / TAU * 1.2;
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
            locations.add(loc);
            placePillar(loc.getX(), loc.getZ());
        }
        return locations;
    }
}
