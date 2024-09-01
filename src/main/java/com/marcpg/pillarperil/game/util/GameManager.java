package com.marcpg.pillarperil.game.util;

import com.marcpg.pillarperil.PillarPlayer;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    public static final List<Game> GAMES = new ArrayList<>();

    public static @Nullable Game game(String id) {
        for (Game game : GAMES) {
            if (game.id.equals(id)) return game;
        }
        return null;
    }

    public static @Nullable Game game(Player player, boolean onlyAlive) {
        for (Game game : GAMES) {
            if (game.player(player, onlyAlive) != null) return game;
        }
        return null;
    }

    public static @Nullable PillarPlayer player(Player player, boolean onlyAlive) {
        for (Game game : GAMES) {
            PillarPlayer p = game.player(player, onlyAlive);
            if (p != null) return p;
        }
        return null;
    }
}
