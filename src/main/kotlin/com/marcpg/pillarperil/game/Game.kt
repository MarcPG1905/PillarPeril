package com.marcpg.pillarperil.game

import com.marcpg.libpg.data.time.Time
import com.marcpg.libpg.display.*
import com.marcpg.libpg.util.Randomizer
import com.marcpg.libpg.util.bukkitRunLater
import com.marcpg.libpg.util.component
import com.marcpg.libpg.util.miniMessage
import com.marcpg.pillarperil.PillarPeril
import com.marcpg.pillarperil.game.util.GameInfo
import com.marcpg.pillarperil.game.util.GameManager
import com.marcpg.pillarperil.game.util.QueueManager
import com.marcpg.pillarperil.generation.Buildings
import com.marcpg.pillarperil.player.PillarPlayer
import com.marcpg.pillarperil.util.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemType

abstract class Game(
    val id: String,
    center: Location,
    protected val bukkitPlayers: List<Player>,
    val modifiers: List<GameModifier>,
): Ticking {
    enum class EndingCause {
        FORCE,
        TIME_OVER,
        LAST_STANDING,
        DRAW,
        ERROR,
    }

    companion object {
        private val itemNowColor = listOf(TextColor.color(0x00AA22), TextColor.color(0x11FF77))
        private val itemTimeColor = listOf(TextColor.color(0x0022FF), TextColor.color(0x3399FF))

        fun getColor(left: Float): BossBar.Color = when {
            left < 0.2 -> BossBar.Color.BLUE
            left < 0.4 -> BossBar.Color.GREEN
            left < 0.6 -> BossBar.Color.YELLOW
            left < 0.8 -> BossBar.Color.RED
            else -> BossBar.Color.PINK
        }

        fun generateId() = Randomizer.generateRandomString(Constants.GAME_ID_LENGTH, Constants.GAME_ID_CHARSET)
    }

    // ================ CONSTRUCTION DATA ================

    abstract val info: GameInfo

    val center: Location = center.clone().apply { y = Configuration.platformHeight + 1.0 }
    val world: World = center.world
    val startingTick: Int = Bukkit.getCurrentTick()

    val initialPlayers: List<PillarPlayer> = mutableListOf() // Only modified at startup, hence hidden mutability.
    val players = mutableListOf<PillarPlayer>()

    // Initial target consisting of all `initialPlayers`, just used for caching.
    private val initialTarget: ForwardingMinecraftReceiver by lazy { initialPlayers.toList().receiver() }

    var radius: Double = 0.0

    lateinit var items: List<ItemType> protected set
    lateinit var buildings: Buildings private set

    // ==================== GAME STATE ====================

    val timeLeft = Time()
    val itemCountdown = Time(0, allowNegatives = true)

    val itemCountdownPercentage: Float
        get() = (itemCountdown.get().toFloat() / (info.itemCountdown().toFloat() - 1)).coerceIn(0.0f, 1.0f)

    private val tickEvents = mutableMapOf<() -> Unit, Int>()
    private val itemEvents = mutableListOf<() -> Unit>()

    var ending = false

    // ================= DISPLAY METHODS =================

    open val scoreboard: ((PillarPlayer) -> SimpleScoreboard)? = { p -> SimpleScoreboard(p, 5, MiniMessage.miniMessage().deserialize("<bold><gradient:#71CCF8:#FC91EC:#F87171>Pillar Peril"),
        StaticValueScoreboardEntry(p.locale().component("scoreboard.mode").style(info.keyStyle()), component(info.name(p.locale())).style(info.valueStyle)),
        StaticValueScoreboardEntry(p.locale().component("scoreboard.name").style(info.keyStyle()), component(p.name()).style(info.valueStyle)),
        ValueScoreboardEntry(p.locale().component("scoreboard.time").style(info.keyStyle())) { component(timeLeft.oneUnitFormatted).style(info.valueStyle) },
        ValueScoreboardEntry(p.locale().component("scoreboard.kills").style(info.keyStyle())) { component(p.kills.toString()).style(info.valueStyle)},
    ) }

    open val actionBar: ((PillarPlayer) -> SimpleActionBar)? = { p -> GradientActionBar(p, 5, 0.1) {
        if (itemCountdown.get() == 0L)
            p.locale().component("actionbar.now") to itemNowColor
        else
            p.locale().component("actionbar.time", itemCountdown.preciselyFormatted) to itemTimeColor
    } }

    open val bossBarCreator: () -> SimpleBossBar = { SimpleBossBar(target(false),
        20,
        { component("=== ${itemCountdown.oneUnitFormatted} ===").style(info.keyStyle()) },
        { itemCountdownPercentage },
        { getColor(itemCountdownPercentage) },
        { BossBar.Overlay.NOTCHED_10 }
    ) }

    var bossBar: SimpleBossBar? = null
        private set

    // =============== OVERRIDABLE METHODS ===============

    open fun init() {
        world.setGameRuleSafe("DO_IMMEDIATE_RESPAWN", "IMMEDIATE_RESPAWN", true)

        bukkitPlayers
            .map { PillarPlayer(it, this) }
            .onEach {
                QueueManager.remove(it.player)

                it.player.gameMode = GameMode.SURVIVAL
                it.player.clearActivePotionEffects()
                it.player.inventory.clear()
                it.player.foodLevel = 20
                it.player.saturation = 20.0f

                val maxHealth = it.player.getAttribute(Attribute.MAX_HEALTH)?.value
                if (maxHealth != null) {
                    it.player.health = maxHealth
                } else {
                    it.player.heal(999.0)
                }
            }
            .forEach {
                (initialPlayers as MutableList) += it
                players += it
            }

        @Suppress("DEPRECATION", "removal")
        val enabledCheck: (ItemType) -> Boolean = getIfClassExists(
            "io.papermc.paper.world.flag.FeatureDependant",
            {{ world.isEnabled(it) }},
            {{ it.isEnabledByFeature(world) }}
        )

        items = Registry.ITEM.filter { it !in Configuration.itemsBlacklist && enabledCheck(it) && info.additionalFilter(it) }.toList()

        radius = initialPlayers.size * Configuration.platformDistanceFactor / Math.TAU

        modifiers.forEach { it.init() }

        buildings = Buildings(this, info.horGen().constructGen(this), info.vertGen().constructGen(this))
        buildings.generate().forEachIndexed { i, l -> players[i].teleport(l.clone().add(0.0, 1.0, 0.0).toCenterLocation()) }

        modifiers.forEach { it.customBuild() }

        if (info.showBossBar()) {
            bossBar = bossBarCreator()
            bossBar?.start()
        }

        timeLeft.set(info.timeLimit())
        itemCountdown.set(info.itemCountdown())

        GameManager.add(this)
        info("Initialized the game.")
    }

    open fun addItem(player: PillarPlayer) = player.giveItems(items)

    // ================= UTILITY METHODS =================

    fun info(msg: String) = PillarPeril.LOG.info("[${Constants.GAME_LOG_PREFIX}$id] $msg")
    fun warn(msg: String) = PillarPeril.LOG.warn("[${Constants.GAME_LOG_PREFIX}$id] $msg")
    fun error(msg: String, e: Throwable) = PillarPeril.LOG.error("[${Constants.GAME_LOG_PREFIX}$id] $msg", e)

    protected fun addTickEvent(interval: Time, event: () -> Unit) = addTickEvent(interval.get() * 20L, event)

    protected fun addTickEvent(intervalTicks: Long, event: () -> Unit) {
        tickEvents[event] = intervalTicks.toInt()
    }

    protected fun addItemEvent(event: () -> Unit) {
        itemEvents.add(event)
    }

    fun target(onlyAlive: Boolean = true): MinecraftReceiver = if (onlyAlive) players.receiver() else initialTarget

    fun player(bukkitPlayer: Player, onlyAlive: Boolean = true): PillarPlayer? {
        for (player in (if (onlyAlive) players else initialPlayers)) {
            if (player.uuid() == bukkitPlayer.uniqueId)
                return player
        }
        return null
    }

    // ================ GAME-LOGIC METHODS ================

    fun eliminate(player: PillarPlayer) {
        if (ending || player !in players) return

        players -= player
        info("$player got eliminated.")

        player.deathTime = Bukkit.getCurrentTick()

        modifiers.forEach { it.onPlayerDeath(player) }

        val win = players.size <= 1
        val winners = players.toList()

        bukkitRunLater(19) { // 0.95s / 950ms
            if (!ending && win) {
                val lastDeath = initialPlayers.mapNotNull { it.deathTime }.max()
                val drawWinners = initialPlayers.filter { (it.deathTime ?: Int.MIN_VALUE) + 19 >= lastDeath }

                if (Configuration.enableDraws && players.isEmpty() && drawWinners.isNotEmpty()) {
                    end(EndingCause.DRAW, drawWinners)
                } else {
                    end(EndingCause.LAST_STANDING, winners)
                }
            }

            if (Configuration.respawnAtConfig) {
                player.player.gameMode = Configuration.spawnGameMode
                player.player.teleport(Configuration.spawnLocation)
            } else {
                player.player.gameMode = GameMode.SPECTATOR
                player.player.teleport(center)
            }

            modifiers.forEach { it.onPostPlayerDeath(player) }
        }
    }

    override fun tick(tick: Ticking.Tick) {
        if (ending || players.isEmpty()) return

        if (tick.isSecond(startingTick)) {
            if (itemCountdown.get() <= 0) {
                modifiers.forEach { it.onItemCycle() }
                itemEvents.forEach { it() }

                players.forEach { addItem(it) }
                itemCountdown.set(info.itemCountdown())
            } else {
                players.playSoundSafe(Sound.UI_BUTTON_CLICK, 0.2f, 2.0f) {
                    itemCountdown.get() <= Configuration.soundEffectsCooldown
                }
            }
            itemCountdown.dec()

            timeLeft.dec()
            if (timeLeft.get() <= 0)
                end(EndingCause.TIME_OVER)
        }

        tickEvents.filter { tick.isInInterval(startingTick, it.value) }.forEach { it.key() }

        modifiers.forEach { it.tick(tick) }
    }

    fun end(cause: EndingCause, winners: List<PillarPlayer> = listOf()) {
        if (ending) return
        ending = true

        for (p in initialPlayers) {
            when (cause) {
                EndingCause.FORCE -> p.showTitle(Title.title(
                    p.locale().component("info.end.force.title", color = NamedTextColor.YELLOW),
                    p.locale().component("info.end.force.subtitle", color = NamedTextColor.RED)
                ))
                EndingCause.TIME_OVER -> p.showTitle(Title.title(
                    p.locale().component("info.end.time-over.title", color = NamedTextColor.GREEN),
                    p.locale().component("info.end.time-over.subtitle", color = NamedTextColor.YELLOW)
                ))
                EndingCause.LAST_STANDING -> p.showTitle(Title.title(
                    p.locale().component("info.end.last-standing.title", winners.joinToString(" & "), color = NamedTextColor.GREEN),
                    p.locale().component("info.end.last-standing.subtitle", winners.sumOf { it.kills }.toString(), color = NamedTextColor.YELLOW)
                ))
                EndingCause.DRAW -> p.showTitle(Title.title(
                    p.locale().component("info.end.draw.title", color = NamedTextColor.GREEN),
                    p.locale().component("info.end.draw.subtitle", winners.joinToString(" & "), color = NamedTextColor.YELLOW)
                ))
                EndingCause.ERROR -> p.showTitle(Title.title(
                    component("Nobody wins!", color = NamedTextColor.RED),
                    component("An error occurred, resulting in no winner.", color = NamedTextColor.GRAY)
                ))
            }

            p.sendMessage(component("=== ").append(p.locale().component("info.end.time-over.stats")).append(component(" ===")).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
            initialPlayers.sortedByDescending { it.kills }.forEachIndexed { i, sorted ->
                p.sendMessage(miniMessage("<dark_gray>${i + 1}. <gray>${sorted.player.name} <dark_gray>(<gold>${sorted.kills}<red>⚔<dark_gray>)"))
            }
        }

        when (cause) {
            EndingCause.FORCE -> warn("Stopped game forcefully.")
            EndingCause.TIME_OVER -> info("Stopped game because the time is up.")
            EndingCause.LAST_STANDING -> info("Stopped game because ${winners.joinToString()} won.")
            EndingCause.DRAW -> info("Stopped game because ${winners.joinToString(" & ")} died at the same time, resulting in a draw.")
            EndingCause.ERROR -> error("Stopped game due to an error. Error code: #001")
        }

        Configuration.endingCommands.forEach { PillarPeril.sendCommand(it(
            "id" to id,
            "mode" to info.mode.gameInfo.namespace,
            "players" to initialPlayers.size,
            "cause" to cause.name.lowercase(),
            "world" to center.world.name,
            "x" to center.x,
            "y" to center.y,
            "z" to center.z,
        )) }
        cleanup()
    }

    private fun cleanup() {
        GameManager.remove(this)
        initialPlayers.forEach { it.clear(true) }
        buildings.reset()
        bossBar?.stop()
    }
}
