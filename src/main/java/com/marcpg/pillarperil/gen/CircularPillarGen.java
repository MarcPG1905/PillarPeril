package com.marcpg.pillarperil.gen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class CircularPillarGen extends Generator {
    private final Material floorMaterial;

    public CircularPillarGen(Location center, int players, Material floorMaterial) {
        super(center, players);
        this.floorMaterial = floorMaterial;
    }

    public CircularPillarGen(Location center, int players) {
        this(center, players, Material.AIR);
    }

    @Override
    public List<Location> generate() {
        List<Location> locations = new ArrayList<>();
        double radius = players * RADIUS_FACTOR;
        World world = center.getWorld();

        for (int i = 0; i < players; i++) {
            double angle = TAU * i / players;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            placePillar(world, x, z);
            locations.add(new Location(world, x, 200, z));
        }
        placeFilledCircle(center, radius, floorMaterial);

        return locations;
    }
}
