package com.marcpg.pillarperil;

import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.mode.*;
import com.marcpg.pillarperil.game.util.GameManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class Commands {
    public static final Map<String, Class<? extends Game>> MODES = Map.of(
            "original", OriginalMode.class,
            "blocky", BlockyMode.class,
            "cubecraft", CubeCraftMode.class,
            "chaos", ChaosMode.class,
            "items-only", ItemOnlyMode.class
    );

    public static LiteralCommandNode<CommandSourceStack> games() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("games")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("start")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("force")
                                .requires(source -> source.getSender().hasPermission("pillarperil.force"))
                                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            MODES.keySet().forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, FinePositionResolver>argument("center", ArgumentTypes.finePosition(true))
                                                .then(RequiredArgumentBuilder.<CommandSourceStack, PlayerSelectorArgumentResolver>argument("players", ArgumentTypes.players())
                                                        .then(RequiredArgumentBuilder.<CommandSourceStack, World>argument("world", ArgumentTypes.world())
                                                                .executes(context -> {
                                                                    CommandSender source = context.getSource().getSender();

                                                                    String mode = context.getArgument("mode", String.class);
                                                                    List<Player> players = context.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
                                                                    FinePosition centerPos = context.getArgument("center", FinePositionResolver.class).resolve(context.getSource());
                                                                    Location center = centerPos.toLocation(context.getArgument("world", World.class));

                                                                    if (players.stream().anyMatch(p -> GameManager.game(p, true) != null)) {
                                                                        source.sendMessage(Component.text("One of the players is still in a game!", NamedTextColor.RED));
                                                                        return 1;
                                                                    }

                                                                    if (MODES.containsKey(mode)) {
                                                                        try {
                                                                            MODES.get(mode).getConstructor(Location.class, int.class, List.class).newInstance(center, Bukkit.getCurrentTick(), players);
                                                                            source.sendMessage(Component.text("Successfully started the game!", NamedTextColor.GREEN));
                                                                        } catch (ReflectiveOperationException e) {
                                                                            source.sendMessage(Component.text("Could not start game, due to an internal error!", NamedTextColor.RED));
                                                                        }
                                                                    } else {
                                                                        source.sendMessage(Component.text("Invalid mode!", NamedTextColor.RED));
                                                                    }
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .build();
    }
}
