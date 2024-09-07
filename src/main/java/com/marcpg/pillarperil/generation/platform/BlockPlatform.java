package com.marcpg.pillarperil.generation.platform;

import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;

public class BlockPlatform extends Platform {
    public BlockPlatform(Game game, Generator generator) {
        super(game, generator);
    }

    @Override
    public void place(double x, double z) {
        placeBlock(x, Platform.platformHeight, z);
    }
}
