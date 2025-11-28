package com.marcpg.pillarperil

import com.marcpg.libpg.config.ConfigValueType
import com.marcpg.libpg.config.PaperConfigProvider
import com.marcpg.libpg.lang.string
import com.marcpg.libpg.sarge.*
import com.marcpg.libpg.util.*
import com.marcpg.pillarperil.game.Game
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.util.Configuration
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World

@Suppress("UnstableApiUsage")
object Commands {
    val game = command<CommandSourceStack>("game") {
        subcommand("start") {
            require("pillarperil.start")
            argument("mode", ExtendedArgumentTypes.valued(Registry.modes.keys).paper()) {
                argument("center", ArgumentTypes.blockPosition()) {
                    argument("world", ArgumentTypes.world()) {
                        argument("players", ArgumentTypes.players()) {
                            action { context ->
                                val source = context.exec()

                                val mode = context.arg<String>("mode")
                                val center = context.arg<BlockPositionResolver>("center").resolve(context.source)
                                    .toLocation(context.arg<World>("world"))
                                val players = context.arg<PlayerSelectorArgumentResolver>("players").resolve(context.source)

                                if (players.any { GameManager.player(it) != null })
                                    return@action source.locale().component("games.start.player_in_game", color = NamedTextColor.RED)

                                if (mode !in Registry.modes)
                                    return@action source.locale().component("games.start.invalid_mode", color = NamedTextColor.RED)

                                val id = Game.generateId()
                                runCatching {
                                    Registry.modes[mode]!!.gameConstructor(id, center, players).init()
                                }.onFailure {
                                    PillarPeril.LOG.error("Could not start game", it)
                                    return@action source.locale().component("games.start.internal_error", color = NamedTextColor.RED)
                                }
                                return@action source.locale().component("games.start.success", id, color = NamedTextColor.GREEN)
                            }
                        }
                    }
                }
            }
        }
        subcommand("stop") {
            require("pillarperil.stop")
            argument("game", ExtendedArgumentTypes.valued { GameManager.games.keys }.paper()) {
                action { context ->
                    val source = context.exec()

                    val game = GameManager[context.arg<String>("game")]
                        ?: return@action source.locale().component("games.wrong_id", color = NamedTextColor.RED)

                    game.end(Game.EndingCause.FORCE)
                    return@action source.locale().component("games.stop.success", game.id, color = NamedTextColor.YELLOW)
                }
            }
        }
        subcommand("list") {
            require("pillarperil.list")
            action { context ->
                val source = context.exec()
                if (GameManager.games.isEmpty())
                    return@action component("There are no games running.", NamedTextColor.YELLOW)

                source.sendMessage(component("Running games:"))
                for (game in GameManager.games.values) {
                    val accentColor = game.info.accentColor()
                    source.sendMessage(component("==== ", NamedTextColor.DARK_GRAY).append(component(game.id, accentColor)).append(component(" ====", NamedTextColor.DARK_GRAY)))
                    source.sendMessage(component("> Players: ", NamedTextColor.GRAY).append(component("${game.players.size}/${game.initialPlayers.size}", accentColor)))
                    source.sendMessage(component("> Item Countdown: ", NamedTextColor.GRAY).append(component(game.itemCountdown.toString(), accentColor)))
                    source.sendMessage(component("> Time Left: ", NamedTextColor.GRAY).append(component(game.timeLeft.preciselyFormatted, accentColor)))
                    source.sendMessage(component("> Mode: ", NamedTextColor.GRAY).append(component(game.info.namespace, accentColor)))
                    source.sendMessage(component("> Center Location: ", NamedTextColor.GRAY).append(component(game.center.toString(), accentColor)))
                }
                return@action null
            }
            subcommand("raw") {
                action {
                    if (GameManager.games.isEmpty())
                        return@action component("empty")
                    return@action component(GameManager.games.keys.joinToString(";"))
                }
            }
        }
        subcommand("info") {
            require("pillarperil.info")
            argument("game", ExtendedArgumentTypes.valued { GameManager.games.keys }.paper()) {
                action { context ->
                    val source = context.exec()

                    val game = GameManager[context.arg<String>("game")]
                        ?: return@action source.locale().component("games.wrong_id", color = NamedTextColor.RED)

                    return@action miniMessage("""
<dark_gray>=== <yellow>${game.id} <dark_gray> ===
<dark_gray>Center: <yellow>${game.center}
<dark_gray>Time Left: <yellow>${game.timeLeft.preciselyFormatted}
<dark_gray>Item Countdown: <yellow>${game.itemCountdown.preciselyFormatted}
<dark_gray>========================
<dark_gray>Mode Name: <yellow>${game.info.name(source.locale())}
<dark_gray>Mode Color: <${game.info.accentColor().asHexString()}>${game.info.accentColor().asHexString()}
<dark_gray>Mode Generator: <yellow>${game.info.vertGen()}
<dark_gray>Mode Item Countdown: <yellow>${game.info.itemCountdown()}
<dark_gray>========================
<dark_gray>Players:
${game.initialPlayers.joinToString { "<dark_gray>| <${if (it in game.players) "green" else "red"}>${it.name()}" }}
<dark_gray>========================
                    """.trimIndent())
                }
            }
        }
    }

    val ppConfig = command<CommandSourceStack>("pp-config") {
        require("pillarperil.config")
        subcommand("reload") {
            action { context ->
                val locale = context.source.sender.locale()
                val result = Configuration.loadChecking()

                result.second.forEach { context.source.sender.sendMessage(component(it)) }
                return@action locale.component("config.reload", locale.string("config.reload.result.${result.first.name.lowercase()}"), color = NamedTextColor.YELLOW)
            }
        }
        subcommand("modify") {
            argument("path", StringArgumentType.word()) {
                subcommand("get") {
                    action { context ->
                        val locale = context.source.sender.locale()
                        val path = context.arg<String>("path")

                        val obj = (Configuration.provider as PaperConfigProvider).configuration.get(path)
                        return@action when {
                            obj is List<*> -> locale.component("config.get.list", path, color = NamedTextColor.YELLOW)
                                .append(component(obj.joinToString("\n- ", "\n- ") { it.toString() }))

                            obj != null -> locale.component("config.get.object", path, obj.toString(), color = NamedTextColor.YELLOW)

                            else -> locale.component("config.key_not_existing", path, color = NamedTextColor.RED)
                        }
                    }
                }
                subcommand("set") {
                    argument("value", StringArgumentType.greedyString()) {
                        action { context ->
                            val locale = context.source.sender.locale()
                            val path = context.arg<String>("path")
                            val value = context.arg<String>("value")

                            return@action runCatching {
                                when (Configuration.provider.approximatePathType(path)) {
                                    ConfigValueType.STRING -> Configuration.provider.setString(path, value)
                                    ConfigValueType.INT -> Configuration.provider.setInt(path, value.toInt())
                                    ConfigValueType.LONG -> Configuration.provider.setLong(path, value.toLong())
                                    ConfigValueType.DOUBLE -> Configuration.provider.setDouble(path, value.toDouble())
                                    ConfigValueType.BOOLEAN -> Configuration.provider.setBoolean(path, value.toBoolean())
                                    ConfigValueType.LIST, ConfigValueType.MAP -> error("list/map")
                                    else -> error("unknown")
                                }

                                runCatching { Configuration.save() }
                                    .onFailure { error("save") }

                                locale.component("config.set.confirm", path, value, color = NamedTextColor.YELLOW)
                            }.getOrElse {
                                when (it.message) {
                                    "list/map" -> locale.component("config.set.section_list", color = NamedTextColor.RED)
                                    "save" -> locale.component("config.error", color = NamedTextColor.RED)
                                    else -> locale.component("config.set.invalid", path, value, color = NamedTextColor.RED)
                                }
                            }
                        }
                    }
                }
                subcommand("add") {
                    argument("value", StringArgumentType.greedyString()) {
                        action { context ->
                            val locale = context.source.sender.locale()
                            val path = context.arg<String>("path")
                            val value = context.arg<String>("value")

                            if (Configuration.provider.approximatePathType(path) != ConfigValueType.LIST)
                                return@action locale.component("config.not_list", path, color = NamedTextColor.RED)

                            val list = Configuration.provider.getList(path)!!.toMutableList()
                            list += value
                            Configuration.provider.setList(path, list)

                            runCatching { Configuration.save() }
                                .onFailure { error("save") }

                            return@action locale.component("config.add.confirm", value, path, color = NamedTextColor.YELLOW)
                        }
                    }
                }
                subcommand("remove") {
                    argument("value", StringArgumentType.greedyString()) {
                        action { context ->
                            val locale = context.source.sender.locale()
                            val path = context.arg<String>("path")
                            val value = context.arg<String>("value")

                            if (Configuration.provider.approximatePathType(path) != ConfigValueType.LIST)
                                return@action locale.component("config.not_list", path, color = NamedTextColor.RED)

                            val list = Configuration.provider.getList(path)!!.toMutableList()
                            list -= value
                            Configuration.provider.setList(path, list)

                            runCatching {
                                Configuration.save()
                            }.onFailure {
                                return@action locale.component("config.error", color = NamedTextColor.RED)
                            }

                            return@action locale.component("config.remove.confirm", value, path, color = NamedTextColor.YELLOW)
                        }
                    }
                }
            }
        }
    }
}
