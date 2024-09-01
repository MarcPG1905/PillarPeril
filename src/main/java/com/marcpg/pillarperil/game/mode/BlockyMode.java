package com.marcpg.pillarperil.game.mode;

import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.util.GameInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.bukkit.Material.*;

public class BlockyMode extends Game {
    private static final List<Material> ATTACK_ITEMS = List.of(STICK, TRIDENT,
            STONE_SWORD,    IRON_SWORD,     GOLDEN_SWORD,   DIAMOND_SWORD,      NETHERITE_SWORD,
            STONE_AXE,      IRON_AXE,       GOLDEN_AXE,     DIAMOND_AXE,        NETHERITE_AXE,
            STONE_PICKAXE,  IRON_PICKAXE,   GOLDEN_PICKAXE, DIAMOND_PICKAXE,    NETHERITE_PICKAXE,
            STONE_SHOVEL,   IRON_SHOVEL,    GOLDEN_SHOVEL,  DIAMOND_SHOVEL,     NETHERITE_SHOVEL
    );

    private static final GameInfo INFO = new GameInfo("Blocky", "blocky", m -> m.isBlock() && hasUse(m));

    public BlockyMode(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
        players.forEach(p -> p.getInventory().addItem(new ItemStack(Randomizer.fromCollection(ATTACK_ITEMS))));
    }

    @Override
    public GameInfo info() {
        return INFO;
    }
}
