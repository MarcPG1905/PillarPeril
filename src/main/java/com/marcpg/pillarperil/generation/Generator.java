package com.marcpg.pillarperil.generation;

import com.marcpg.pillarperil.Config;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public abstract class Generator {
    protected static final double MIN_DISTANCE = 3.0;
    protected static final double TAU = 6.28318530717958647692;
    protected static final double RADIUS_FACTOR = 10.0 / TAU;

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
        for (int y = 0; y < Config.pillarHeight; y++) {
            Block block = new Location(game.world, x, Config.pillarHeight, z).getBlock();
            game.addBlock(block.getLocation(), block.getBlockData());
            block.setType(Material.BEDROCK);
        }
    }
}
