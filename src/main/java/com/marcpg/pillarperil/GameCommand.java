package com.marcpg.pillarperil;

import com.marcpg.libpg.text.Completer;
import com.marcpg.pillarperil.mode.modes.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class GameCommand implements TabExecutor {
    private static final List<String> OPTIONS = List.of("start", "stop", "info");
    private static final List<String> MODES = List.of("original", "blocky", "cubecraft", "chaos", "itemsOnly");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            if (PillarPeril.currentGame == null) {
                sender.sendMessage(Component.text("There's no match currently running!", NamedTextColor.RED));
                return true;
            }
            switch (args[0]) {
                case "stop" -> {
                    PillarPeril.currentGame.forceEnd();
                    sender.sendMessage(Component.text("Successfully stopped the current match.", NamedTextColor.YELLOW));
                }
                case "info" -> {
                    sender.sendMessage(Component.text("Players in current match:").decorate(TextDecoration.BOLD));
                    for (Player player : PillarPeril.currentGame.players()) {
                        sender.sendMessage(Component.text("- ", NamedTextColor.GRAY).append(player.name()));
                    }
                }
                default -> { return false; }
            }
        } else if (args.length == 2 && args[0].equals("start")) {
            World world = sender instanceof Player player ? player.getWorld() : Objects.requireNonNull(Bukkit.getWorld("world"));
            Location center = sender instanceof Player player ? player.getLocation() : new Location(world, 0, 0, 0);
            switch (args[1]) {
                case "original" -> PillarPeril.currentGame = new OriginalMode(center, world.getPlayers());
                case "blocky" -> PillarPeril.currentGame = new BlockyMode(center, world.getPlayers());
                case "cubecraft" -> PillarPeril.currentGame = new CubeCraftMode(center, world.getPlayers());
                case "chaos" -> PillarPeril.currentGame = new ChaosMode(center, world.getPlayers());
                case "itemsOnly" -> PillarPeril.currentGame = new ItemOnlyMode(center, world.getPlayers());
                default -> sender.sendMessage(Component.text("The game type " + args[1] + " is not valid!", NamedTextColor.RED));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Completer.startComplete(args[0], OPTIONS);
        } else if (args.length == 2 && args[0].equals("start")) {
            return Completer.startComplete(args[1], MODES);
        }
        return List.of();
    }
}
