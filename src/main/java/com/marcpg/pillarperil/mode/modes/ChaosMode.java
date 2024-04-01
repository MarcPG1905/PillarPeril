package com.marcpg.pillarperil.mode.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.RandomPillarGen;
import com.marcpg.pillarperil.mode.VisibleCooldownGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChaosMode extends VisibleCooldownGame {
    private static final Time itemCooldown = new Time(3);
    private static final Time limit = new Time(8, Time.Unit.MINUTES);
    private final List<ItemStack> items;

    public ChaosMode(Location center, @NotNull List<Player> players) {
        super(players, new RandomPillarGen(center, players.size()));
        World world = new ArrayList<>(this.players.keySet()).get(0).getWorld();
        items = Arrays.stream(Material.values())
                .filter(m -> !m.isLegacy() && !m.isAir() && m.isEnabledByFeature(world))
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
