package com.marcpg.pillarperil.game;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.PillarPeril;
import com.marcpg.pillarperil.gen.Generator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Game {
    public enum EndingCause { TIME_OVER, LAST_STANDING }

    private static final Component TITLE = MiniMessage.miniMessage().deserialize("<bold><gradient:#71CCF8:#FC91EC:#F87171>Pillar Peril");

    protected final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    protected final Time time = new Time(0);
    protected final Map<Player, AtomicInteger> players; // Just using AtomicInteger for features like incrementing.
    protected final Generator generator;
    protected final List<ItemStack> items;

    @SuppressWarnings("deprecation")
    protected Game(@NotNull List<Player> players, @NotNull Generator generator, Predicate<Material> filter) {
        players.forEach(p -> {
            p.setGameMode(GameMode.SURVIVAL);
            p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
            p.getInventory().clear();
            p.setHealth(20.0);
        });
        this.players = new HashMap<>(players.stream().collect(Collectors.toMap(p -> p, p -> new AtomicInteger())));
        this.generator = generator;

        this.items = Arrays.stream(Material.values())
                .filter(m -> !m.isAir() && !m.isLegacy() && filter.test(m))
                .map(ItemStack::new)
                .toList();

        List<Location> pillars = generator.generate();
        for (int i = 0; i < pillars.size(); i++) {
            players.get(i).teleport(pillars.get(i));
        }
    }

    public Set<Player> players() {
        return players.keySet();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (this instanceof VisibleCooldownGame vtg)
            vtg.bossBar.removeViewer(player);
    }

    public ItemStack randomItem() {
        return Randomizer.fromCollection(items());
    }

    public boolean giveItem() {
        return time.get() > 0 && time.get() % itemCooldown().get() == 0;
    }

    public List<ItemStack> items() {
        return items;
    }

    public abstract Time itemCooldown();

    public abstract Time timeLimit();

    public Time timeDone() {
        return time;
    }

    public Time timeLeft() {
        return new Time(timeLimit().get() - timeDone().get());
    }

    public void end(EndingCause cause, Map<Player, AtomicInteger> winners) {
        if (cause == EndingCause.TIME_OVER) {
            for (Player player : players.keySet()) {
                player.showTitle(Title.title(Component.text("Time's Over!", NamedTextColor.GREEN), Component.text("The game ends, because the time is over!", NamedTextColor.YELLOW)));
            }
            Bukkit.getServer().sendMessage(Component.text("=== Top " + winners.size() + " Players ===", NamedTextColor.GREEN, TextDecoration.BOLD));
            List<Map.Entry<Player, AtomicInteger>> sortedWinners = players.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().get())).toList();
            for (int i = 0; i < sortedWinners.size(); i++) {
                Bukkit.getServer().sendMessage(Component.text(i + ". " + sortedWinners.get(i).getKey().getName() + ": " + sortedWinners.get(i).getValue().get()));
            }
        } else {
            Player winner = new ArrayList<>(winners.keySet()).get(0);
            for (Player player : players.keySet()) {
                player.showTitle(Title.title(Component.text(winner.getName() + " won!", NamedTextColor.GREEN), Component.text("With " + players.get(winner).get() + " points!", NamedTextColor.YELLOW)));
            }
        }
        reset();
    }

    public void forceEnd() {
        for (Player player : players.keySet()) {
            player.showTitle(Title.title(Component.text("Game Forcefully Ended!", NamedTextColor.YELLOW), Component.text("It's forced, so there are no winners!", NamedTextColor.RED)));
        }
        reset();
    }

    private void reset() {
        if (this instanceof VisibleCooldownGame vtg) {
            players.keySet().forEach(vtg.bossBar::removeViewer);
        }
        PillarPeril.currentGame = null;
        Bukkit.getScheduler().runTaskLater(PillarPeril.PLUGIN, () -> {
            generator.clear();
            for (Player player : players.keySet()) {
                player.setScoreboard(scoreboardManager.getMainScoreboard());
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }, 100); // 5 Seconds
    }

    public void onTick() {}

    @OverridingMethodsMustInvokeSuper
    public void onSecond() {
        time.increment();
        if (time.get() >= timeLimit().get()) {
            end(EndingCause.TIME_OVER, players);
        }
        if (giveItem()) {
            for (Player player : players.keySet()) {
                player.getInventory().addItem(randomItem());
            }
        }
        String timeLeft = timeLeft().getPreciselyFormatted();
        for (Map.Entry<Player, AtomicInteger> player : players.entrySet()) {
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("game", "dummy", TITLE);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§l§9Name: §r" + player.getKey().getName()).setScore(2);
            objective.getScore("§l§dTime: §r" + timeLeft).setScore(1);
            objective.getScore("§l§cKills: §r" + player.getValue().get()).setScore(0);
            player.getKey().setScoreboard(scoreboard);
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        player.setGameMode(GameMode.SPECTATOR);

        removePlayer(player);

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            players.get(killer).incrementAndGet();
            event.deathMessage(Component.text(player.getName() + " got killed by " + killer.getName() + ", " + players.size() + " players left!", NamedTextColor.YELLOW));
        } else {
            event.deathMessage(Component.text(player.getName() + " died, " + players.size() + " players left!", NamedTextColor.YELLOW));
        }

        Bukkit.getScheduler().runTaskLater(PillarPeril.PLUGIN, () -> {
            player.setGameMode(GameMode.SPECTATOR);
            if (players.size() == 1) {
                end(EndingCause.LAST_STANDING, players);
            }
        }, 20L);
    }

    public void onBlockPlace(Block block) {
        generator.addBlockToCache(block);
    }

    public static boolean hasUse(@NotNull Material m) {
        return !(m.getHardness() < 0.05) || m == Material.TNT || m == Material.SLIME_BLOCK || m == Material.HONEY_BLOCK || m == Material.SCAFFOLDING;
    }
}
