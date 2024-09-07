package com.marcpg.pillarperil.generation.platform;

import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;

public class PillarPlatform extends BlockPlatform {
    public PillarPlatform(Game game, Generator generator) {
        super(game, generator);
    }

    @Override
    public void place(double x, double z) {
        for (int y = 0; y < Platform.platformHeight; y++)
            placeBlock(x, y, z);
    }
}
