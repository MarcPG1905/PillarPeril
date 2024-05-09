package com.marcpg.pillarperil.game.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CubeCraftMode extends Game {
    private static final Material[] FLOOR_MATERIALS = { Material.GRASS_BLOCK, Material.OBSIDIAN, Material.SLIME_BLOCK, Material.NETHERRACK, Material.LAVA };
    private static final Time itemCooldown = new Time(5);
    private static final Time limit = new Time(5, Time.Unit.MINUTES);

    public CubeCraftMode(Location center, @NotNull List<Player> players) {
        super(players, new CircularPillarGen(center, players.size(), Randomizer.fromArray(FLOOR_MATERIALS)), m -> m.name().contains("BOAT") && hasUse(m));
    }

    @Override
    public Time itemCooldown() {
        return itemCooldown;
    }

    @Override
    public Time timeLimit() {
        return limit;
    }
}
