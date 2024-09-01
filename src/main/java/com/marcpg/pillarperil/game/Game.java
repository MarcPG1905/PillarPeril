package com.marcpg.pillarperil.game;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.PillarPeril;
import com.marcpg.pillarperil.PillarPlayer;
import com.marcpg.pillarperil.game.util.GameInfo;
import com.marcpg.pillarperil.game.util.GameManager;
import com.marcpg.pillarperil.generation.Generator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;

public abstract class Game {
    public enum EndingCause { FORCE, TIME_OVER, LAST_STANDING }

    public final String id = Randomizer.generateRandomString(10, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    public final Location center;
    public final World world;
    public final int startTick;
    public final Audience initialAudience;

    protected final List<PillarPlayer> initialPlayers = new ArrayList<>();
    protected final List<PillarPlayer> players = new ArrayList<>();
    protected final Map<Location, BlockData> initialBlocks = new HashMap<>();
    protected List<Material> items;

    protected long itemCooldown = info().itemCooldown();
    protected final Time timeLeft = new Time(info().timeLimit());

    protected final Style keyStyle = Style.style(info().accentColor(), TextDecoration.BOLD);
    protected final Style valueStyle = Style.style(NamedTextColor.WHITE, TextDecoration.BOLD.withState(false));

    public Game(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        this.center = center.clone();
        this.center.setY(Generator.pillarHeight + 1);

        this.world = center.getWorld();
        this.startTick = startTick;

        players.stream()
                .peek(p -> {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.clearActivePotionEffects();
                    p.getInventory().clear();
                    p.setHealth(20.0);
                })
                .map(player -> new PillarPlayer(player, this))
                .peek(this.initialPlayers::add)
                .forEach(this.players::add);

        this.initialAudience = Audience.audience(initialPlayers);
        this.items = Arrays.stream(Material.values())
                .filter(m -> !m.isEmpty() && !m.isLegacy() && m.isEnabledByFeature(world) && info().filter().test(m))
                .toList();

        try {
            List<Location> pillars = info().generator().getConstructor(Game.class, Location.class, int.class).newInstance(this, center.clone(), players.size()).generate();
            for (int i = 0; i < pillars.size(); i++) {
                players.get(i).teleport(pillars.get(i));
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        GameManager.GAMES.add(this);
    }

    public abstract GameInfo info();
    public @Nullable Component actionbar(PillarPlayer player) { return null; }
    public @Nullable List<Component> scoreboard(@NotNull PillarPlayer player) {
        return List.of(
                MiniMessage.miniMessage().deserialize("<bold><gradient:#71CCF8:#FC91EC:#F87171>Pillar Peril"),
                Component.text("Mode: ", keyStyle).append(Component.text(info().name(), valueStyle)),
                Component.text("Name: ", keyStyle).append(player.player.name().style(valueStyle)),
                Component.text("Time Left: ", keyStyle).append(Component.text(timeLeft.getOneUnitFormatted(), valueStyle)),
                Component.text("Kills: ", keyStyle).append(Component.text(player.kills(), valueStyle))
        );
    }

    public final List<PillarPlayer> initialPlayers() {
        return initialPlayers;
    }

    public final List<PillarPlayer> players() {
        return players;
    }

    public final @Nullable PillarPlayer player(Player player, boolean onlyAlive) {
        for (PillarPlayer p : onlyAlive ? players : initialPlayers) {
            if (p.player == player) return p;
        }
        return null;
    }

    public final @NotNull Audience audience(boolean onlyAlive) {
        return Audience.audience(onlyAlive ? players : initialPlayers);
    }

    public void eliminate(PillarPlayer player) {
        players.remove(player);

        Bukkit.getScheduler().runTaskLater(PillarPeril.PLUGIN, () -> {
            if (players.size() == 1) {
                end(EndingCause.LAST_STANDING, List.of(players.getFirst()));
            }
            player.player.teleport(center);
            player.player.setGameMode(GameMode.SPECTATOR);
        }, 20);
    }

    public final void addBlock(Location location, BlockData oldData) {
        if (!initialBlocks.containsKey(location))
            initialBlocks.put(location, oldData);
    }

    @OverridingMethodsMustInvokeSuper
    public void tick(int tick) {
        if (tick - startTick % 10 == 0) {
            players.forEach(PillarPlayer::tick);
            if (tick - startTick % 20 == 0)
                tickSecond();
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void tickSecond() {
        itemCooldown--;
        if (itemCooldown < 0) {
            players.forEach(p -> p.giveItem(items));
            itemCooldown = info().itemCooldown();
        }

        timeLeft.decrement();
        if (timeLeft.get() <= 0)
            end(EndingCause.FORCE, List.of());
    }

    public void end(@NotNull EndingCause cause, List<PillarPlayer> winners) {
        switch (cause) {
            case FORCE -> initialAudience.showTitle(Title.title(Component.text("Game Forcefully Ended!", NamedTextColor.YELLOW), Component.text("It's forced, so there are no winners!", NamedTextColor.RED)));
            case TIME_OVER -> {
                initialAudience.showTitle(Title.title(Component.text("Time's Over!", NamedTextColor.GREEN), Component.text("The game ends, because the time is over!", NamedTextColor.YELLOW)));

                Bukkit.getServer().sendMessage(Component.text("=== Top " + winners.size() + " Players ===", NamedTextColor.GREEN, TextDecoration.BOLD));
                List<PillarPlayer> sortedPlayers = players.stream().sorted(Comparator.comparingInt(PillarPlayer::kills)).toList();
                for (int i = 0; i < sortedPlayers.size(); i++) {
                    Bukkit.getServer().sendMessage(Component.text(i + ". " + sortedPlayers.get(i).player.getName() + ": " + sortedPlayers.get(i).kills()));
                }
            }
            case LAST_STANDING -> initialAudience.showTitle(Title.title(Component.text(winners.getFirst().player.getName() + " won!", NamedTextColor.GREEN), Component.text("With " + winners.getFirst().kills() + " kills!", NamedTextColor.YELLOW)));
        }
        cleanup();
    }

    public void cleanup() {
        initialPlayers.forEach(PillarPlayer::clean);
        initialBlocks.forEach((location, blockData) -> location.getBlock().setBlockData(blockData));
        GameManager.GAMES.remove(this);
    }

    public static boolean hasUse(@NotNull Material m) {
        return m.isSolid();
    }
}
