package com.marcpg.pillarperil.player;

import com.marcpg.libpg.util.Randomizer;
import com.marcpg.pillarperil.game.Game;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PillarPlayer implements ForwardingAudience.Single {
    private static final ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();

    public final Player player;
    public final Game game;

    private int kills = 0;

    public PillarPlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    public void giveItem(List<Material> availableItems) {
        player.getInventory().addItem(new ItemStack(Randomizer.fromCollection(availableItems)));
    }

    public void tick() {
        List<Component> scoreboard = game.scoreboard(this);
        if (scoreboard != null && !scoreboard.isEmpty()) {
            Objective objective;

            if (player.getScoreboard() == SCOREBOARD_MANAGER.getMainScoreboard()) {
                Scoreboard sb = SCOREBOARD_MANAGER.getNewScoreboard();
                objective = sb.registerNewObjective("pp", Criteria.DUMMY, scoreboard.getFirst());
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(sb);
            } else {
                objective = player.getScoreboard().getObjective("pp");
                if (objective == null)
                    objective = player.getScoreboard().registerNewObjective("pp", Criteria.DUMMY, scoreboard.getFirst());
            }

            for (int i = scoreboard.size() - 1; i > 0; i--) {
                Score score = objective.getScore("score-" + i);
                score.numberFormat(NumberFormat.blank());
                score.setScore(scoreboard.size() - i);

                score.customName(scoreboard.get(i));
            }
        }

        Component actionbar = game.actionbar(this);
        if (actionbar != null && !actionbar.equals(Component.empty()))
            player.sendActionBar(actionbar);
    }

    public void addKill() {
        kills++;
    }

    public int kills() {
        return kills;
    }

    @Override
    public @NotNull Audience audience() {
        return player;
    }

    public List<PillarPlayer> opponents(boolean onlyAlive) {
        return (onlyAlive ? game.players() : game.initialPlayers()).stream()
                .filter(p -> p != this)
                .toList();
    }

    public Locale locale() {
        return player.locale();
    }

    public UUID uuid() {
        return player.getUniqueId();
    }

    public void clean() {
        player.setScoreboard(SCOREBOARD_MANAGER.getMainScoreboard());
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.teleport(game.world.getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
    }
}
