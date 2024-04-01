package com.marcpg.pillarperil.mode.modes;

import com.marcpg.libpg.data.time.Time;
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

public class ItemOnlyMode extends Game {
    private static final Time itemCooldown = new Time(10);
    private static final Time limit = new Time(4, Time.Unit.MINUTES);
    private final List<ItemStack> items;

    public ItemOnlyMode(Location center, @NotNull List<Player> players) {
        super(players, new CircularPillarGen(center, players.size(), Material.SLIME_BLOCK));
        World world = new ArrayList<>(this.players.keySet()).get(0).getWorld();
        items = Arrays.stream(Material.values())
                .filter(m -> m.isItem() && !m.isLegacy() && !m.isAir() && m.isEnabledByFeature(world))
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
