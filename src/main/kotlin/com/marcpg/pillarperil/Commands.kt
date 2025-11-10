package com.marcpg.pillarperil

import com.marcpg.libpg.config.ConfigValueType
import com.marcpg.libpg.config.PaperConfigProvider
import com.marcpg.libpg.lang.string
import com.marcpg.libpg.sarge.arg
import com.marcpg.libpg.sarge.argument
import com.marcpg.libpg.sarge.command
import com.marcpg.libpg.sarge.subcommand
import com.marcpg.libpg.util.action
import com.marcpg.libpg.util.component
import com.marcpg.libpg.util.locale
import com.marcpg.libpg.util.require
import com.marcpg.pillarperil.util.Configuration
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.format.NamedTextColor

@Suppress("UnstableApiUsage")
object Commands {
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
