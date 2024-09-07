package com.marcpg.pillarperil.generation;

import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Generator {
    public static double platformDistanceFactor = 10.0;

    protected final Game game;
    protected final Location center;
    protected final int players;
    protected final Platform platform;

    public Generator(@NotNull Game game, Location center, int players) {
        this.game = game;
        this.center = center;
        this.players = players;
        try {
            this.platform = game.info().platforms().getConstructor(Game.class, Generator.class).newInstance(game, this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract List<Location> generate();
}
