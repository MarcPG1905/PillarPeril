package com.marcpg.pillarperil.generation;

import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class Platform {
    public static int platformHeight = 200;
    public static int deathHeight = 175;

    protected final Game game;
    protected final Generator generator;

    public Platform(Game game, Generator generator) {
        this.game = game;
        this.generator = generator;
    }

    public abstract void place(double x, double z);

    public void placeBlock(double x, double y, double z) {
        Block block = new Location(game.world, x, y, z).getBlock();
        game.addBlock(block.getLocation(), block.getBlockData());
        block.setType(Material.BEDROCK);
    }
}
