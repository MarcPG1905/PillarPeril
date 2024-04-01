package com.marcpg.pillarperil.mode.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.mode.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CubeCraftMode extends Game {
    private static final Material[] FLOOR_MATERIALS = { Material.GRASS_BLOCK, Material.OBSIDIAN, Material.SLIME_BLOCK, Material.NETHERRACK, Material.LAVA };
    private static final Time itemCooldown = new Time(5);
    private static final Time limit = new Time(5, Time.Unit.MINUTES);
    private final List<ItemStack> items;

    public CubeCraftMode(Location center, @NotNull List<Player> players) {
        super(players, new CircularPillarGen(center, players.size(), Randomizer.fromArray(FLOOR_MATERIALS)));
        World world = new ArrayList<>(this.players.keySet()).get(0).getWorld();
        items = Arrays.stream(Material.values())
                .filter(m -> !m.isLegacy() && !m.isAir() && m.isEnabledByFeature(world) && !m.name().contains("BOAT") && hasUse(m))
                .map(ItemStack::new)
                .map(ItemStack::new)
                .toList();
    }

    @Override
    public List<ItemStack> items() {
        return items;
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
