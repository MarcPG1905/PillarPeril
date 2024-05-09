package com.marcpg.pillarperil.game.modes;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.gen.CircularPillarGen;
import com.marcpg.pillarperil.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockyMode extends Game {
    private static final List<Material> ATTACK_ITEMS = List.of(
            Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.STICK, Material.TRIDENT
    );

    private static final Time itemCooldown = new Time(10);
    private static final Time limit = new Time(4, Time.Unit.MINUTES);

    public BlockyMode(Location center, @NotNull List<Player> players) {
        super(players, new CircularPillarGen(center, players.size()), m -> m.isBlock() && hasUse(m));
        this.players.keySet().forEach(p -> p.getInventory().addItem(new ItemStack(Randomizer.fromCollection(ATTACK_ITEMS))));
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
