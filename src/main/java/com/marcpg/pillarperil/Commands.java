package com.marcpg.pillarperil;

import com.marcpg.libpg.lang.Translation;
import com.marcpg.pillarperil.game.Game;
import com.marcpg.pillarperil.game.mode.*;
import com.marcpg.pillarperil.game.util.GameManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class Commands {
    public static final Component SEPARATOR_20 = Component.text("====================", NamedTextColor.DARK_GRAY);

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
                        .requires(source -> source.getSender().hasPermission("pillarperil.start"))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("force")
                                .requires(source -> source.getSender().hasPermission("pillarperil.start.force"))
                                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            MODES.keySet().forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, BlockPositionResolver>argument("center", ArgumentTypes.blockPosition())
                                                .then(RequiredArgumentBuilder.<CommandSourceStack, World>argument("world", ArgumentTypes.world())
                                                        .then(RequiredArgumentBuilder.<CommandSourceStack, PlayerSelectorArgumentResolver>argument("players", ArgumentTypes.players())
                                                                .executes(context -> {
                                                                    CommandSender source = context.getSource().getSender();
                                                                    Locale l = PillarPeril.locale(source);

                                                                    String mode = context.getArgument("mode", String.class);
                                                                    List<Player> players = context.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
                                                                    BlockPosition centerPos = context.getArgument("center", BlockPositionResolver.class).resolve(context.getSource());
                                                                    Location center = centerPos.toLocation(context.getArgument("world", World.class));

                                                                    if (players.stream().anyMatch(p -> GameManager.game(p, true) != null)) {
                                                                        source.sendMessage(Translation.component(l, "games.start.player_in_game").color(NamedTextColor.RED));
                                                                        return 1;
                                                                    }

                                                                    if (MODES.containsKey(mode)) {
                                                                        try {
                                                                            MODES.get(mode).getConstructor(Location.class, int.class, List.class).newInstance(center, Bukkit.getCurrentTick(), players);
                                                                            source.sendMessage(Translation.component(l, "games.start.success").color(NamedTextColor.GREEN));
                                                                        } catch (ReflectiveOperationException e) {
                                                                            source.sendMessage(Translation.component(l, "games.start.internal_error").color(NamedTextColor.RED));
                                                                        }
                                                                    } else {
                                                                        source.sendMessage(Translation.component(l, "games.start.invalid_mode").color(NamedTextColor.RED));
                                                                    }
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("stop")
                        .requires(source -> source.getSender().hasPermission("pillarperil.stop"))
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("game", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    GameManager.GAMES.forEach(game -> builder.suggest(game.id));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    CommandSender source = context.getSource().getSender();
                                    Locale l = PillarPeril.locale(source);

                                    Game game = GameManager.game(context.getArgument("game", String.class));
                                    if (game != null) {
                                        game.end(Game.EndingCause.FORCE, List.of());
                                        source.sendMessage(Translation.component(l, "games.stop.success").color(NamedTextColor.YELLOW));
                                    } else {
                                        source.sendMessage(Translation.component(l, "games.wrong_id").color(NamedTextColor.RED));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("info")
                        .requires(source -> source.getSender().hasPermission("pillarperil.info"))
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("game", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    GameManager.GAMES.forEach(game -> builder.suggest(game.id));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    CommandSender source = context.getSource().getSender();
                                    Locale l = PillarPeril.locale(source);

                                    Game game = GameManager.game(context.getArgument("game", String.class));
                                    if (game != null) {
                                        source.sendMessage(SEPARATOR_20);
                                        source.sendMessage(Component.text("id: " + game.id));
                                        source.sendMessage(Component.text("world: " + game.world.getName()));
                                        source.sendMessage(Component.text("center: " + game.center));
                                        source.sendMessage(SEPARATOR_20);
                                        source.sendMessage(Component.text("timeLeft: " + game.timeLeft().getPreciselyFormatted()));
                                        source.sendMessage(Component.text("itemCooldown: " + game.itemCooldown()));
                                        source.sendMessage(SEPARATOR_20);
                                        source.sendMessage(Component.text("mode.name: " + game.info().name(l)));
                                        source.sendMessage(Component.text("mode.color: ").append(Component.text(game.info().accentColor().asHexString(), game.info().accentColor())));
                                        source.sendMessage(Component.text("mode.generator: " + game.info().generator()));
                                        source.sendMessage(Component.text("mode.itemCooldown: " + game.info().itemCooldown()));
                                        source.sendMessage(SEPARATOR_20);
                                        source.sendMessage(Component.text("players:"));
                                        for (PillarPlayer p : game.initialPlayers()) {
                                            source.sendMessage(Component.text("| ").append(Component.text(p.player.getName(), game.players().contains(p) ? NamedTextColor.GREEN : NamedTextColor.GRAY)));
                                        }
                                        source.sendMessage(SEPARATOR_20);
                                    } else {
                                        source.sendMessage(Translation.component(l, "games.wrong_id").color(NamedTextColor.RED));
                                    }
                                    return 1;
                                })
                        )
                )
                .build();
    }
}
