package com.marcpg.pillarperil.gen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Generator {
    protected static final double TAU = 6.28318530717958647692;
    protected static final double RADIUS_FACTOR = 10.0 / TAU;
    protected final Set<Block> blockCache = new HashSet<>();
    protected final Location center;
    protected final int players;

    protected Generator(@NotNull Location center, int players) {
        this.center = center;
        center.setY(200);
        this.players = players;
    }

    public abstract List<Location> generate();

    public void clear() {
        for (Block block : blockCache) {
            block.setType(Material.AIR);
        }
    }

    public void addBlockToCache(Block block) {
        blockCache.add(block);
    }

    protected void placePillar(World world, double x, double z) {
        for (int y = 0; y < 200; y++) {
            Block block = new Location(world, x, y, z).getBlock();
            block.setType(Material.BEDROCK);
            blockCache.add(block);
        }
    }

    protected void placeFilledCircle(@NotNull Location center, double radius, Material material) {
        World world = center.getWorld();
        int blockX = center.getBlockX();
        int blockZ = center.getBlockZ();
        for (int x = (int) (blockX - radius); x <= blockX + radius; x++) {
            for (int z = (int) (blockZ - radius); z <= blockZ + radius; z++) {
                Location loc = new Location(world, x, 0, z);
                if (center.distance(loc) <= radius) {
                    world.getBlockAt(loc).setType(material);
                    blockCache.add(world.getBlockAt(loc));
                }
            }
        }
    }
}
