package com.marcpg.pillarperil.generation;

import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public abstract class Generator {
    public static final double TAU = 6.28318530717958647692;

    public static int pillarHeight = 200;
    public static int deathHeight = 175;
    public static double pillarDistanceFactor = 10.0;

    protected final Game game;
    protected final Location center;
    protected final int players;

    public Generator(Game game, Location center, int players) {
        this.game = game;
        this.center = center;
        this.players = players;
    }

    public abstract List<Location> generate();

    protected void placePillar(double x, double z) {
        for (int y = 0; y < Generator.pillarHeight; y++) {
            Block block = new Location(game.world, x, y, z).getBlock();
            game.addBlock(block.getLocation(), block.getBlockData());
            block.setType(Material.BEDROCK);
        }
    }
}
