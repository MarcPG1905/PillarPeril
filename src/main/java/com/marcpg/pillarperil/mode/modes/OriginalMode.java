package com.marcpg.pillarperil.mode.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.mode.VisibleCooldownGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OriginalMode extends VisibleCooldownGame {
    private static final Time itemCooldown = new Time(5);
    private static final Time limit = new Time(5, Time.Unit.MINUTES);
    protected final List<ItemStack> items;

    public OriginalMode(Location center, List<Player> players) {
        super(players, new CircularPillarGen(center, players.size()));
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
