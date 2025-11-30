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
import com.marcpg.pillarperil.util.Configuration
import com.marcpg.pillarperil.util.Constants
import com.marcpg.pillarperil.util.Ticking
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemType

abstract class Game(
    val id: String,
    center: Location,
    private val bukkitPlayers: List<Player>
): Ticking {
    companion object {
        private val itemNowColor = listOf(TextColor.color(0x00AA22), TextColor.color(0x11FF77))
        private val itemTimeColor = listOf(TextColor.color(0x0022FF), TextColor.color(0x3399FF))

        fun getColor(left: Float): BossBar.Color = when {
            left < 0.1 -> BossBar.Color.WHITE
            left < 0.2 -> BossBar.Color.BLUE
            left < 0.4 -> BossBar.Color.GREEN
            left < 0.6 -> BossBar.Color.YELLOW
            left < 0.8 -> BossBar.Color.RED
            else -> BossBar.Color.PINK
        }

        fun generateId() = Randomizer.generateRandomString(Constants.GAME_ID_LENGTH, Constants.GAME_ID_CHARSET)
    }

    val initialPlayers: List<PillarPlayer> = mutableListOf()
    private lateinit var initialTarget: ForwardingMinecraftReceiver
    val players = mutableListOf<PillarPlayer>()
    val deathTimes = mutableMapOf<PillarPlayer, Int>()

    abstract val info: GameInfo

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
        { (itemCountdown.get().toFloat() / info.itemCountdown().toFloat()) },
        { getColor(itemCountdown.get().toFloat() / info.itemCountdown().toFloat()) },
        { BossBar.Overlay.NOTCHED_10 }
    ) }

    val center: Location = center.clone()
    val world: World = center.world

    lateinit var items: List<ItemType>

    lateinit var buildings: Buildings private set
    val startingTick: Int = Bukkit.getCurrentTick()

    var bossBar: SimpleBossBar? = null
        private set

    val timeLeft: Time = Time()

    var itemCountdown: Time = Time(0, allowNegatives = true)
        private set

    private val tickEvents = mutableMapOf<() -> Unit, Int>()

    private var ending = false

    open fun init() {
        this.center.y = Configuration.platformHeight + 1.0

        bukkitPlayers
            .onEach {
                QueueManager.remove(it)

                it.gameMode = GameMode.SURVIVAL
                it.clearActivePotionEffects()
                it.inventory.clear()
                it.health = 20.0
            }
            .map { PillarPlayer(it, this) }
            .forEach {
                (initialPlayers as MutableList) += it
                players += it
            }
        initialTarget = initialPlayers.toList().receiver()

        items = Registry.ITEM.filter { it != ItemType.AIR && world.isEnabled(it) && info.additionalFilter(it) }.toList()

        buildings = Buildings(this, info.horGen().genConstructor(this), info.vertGen().genConstructor(this))
        buildings.generate().forEachIndexed { i, l -> players[i].teleport(l.clone().add(0.0, 1.0, 0.0)) }

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

    fun info(msg: String) = PillarPeril.LOG.info("[${Constants.GAME_LOG_PREFIX}$id] $msg")
    fun warn(msg: String) = PillarPeril.LOG.warn("[${Constants.GAME_LOG_PREFIX}$id] $msg")

    protected fun addTickEvent(interval: Time, event: () -> Unit) = addTickEvent(interval.get() * 20L, event)

    protected fun addTickEvent(intervalTicks: Long, event: () -> Unit) {
        tickEvents[event] = intervalTicks.toInt()
    }

    fun target(onlyAlive: Boolean = true): MinecraftReceiver = if (onlyAlive) players.receiver() else initialTarget

    fun player(bukkitPlayer: Player, onlyAlive: Boolean = true): PillarPlayer? {
        for (player in (if (onlyAlive) players else initialPlayers)) {
            if (player.uuid() == bukkitPlayer.uniqueId)
                return player
        }
        return null
    }

    fun eliminate(player: PillarPlayer) {
        if (ending || player !in players) return

        players -= player
        info("$player got eliminated.")

        deathTimes[player] = Bukkit.getCurrentTick()

        val win = players.size <= 1
        val winners = players.toList()

        bukkitRunLater(19) { // 0.95s / 950ms
            if (!ending && win) {
                val lastDeath = deathTimes.maxOf { it.value }
                val drawWinners = deathTimes.filter { it.value + 19 >= lastDeath }.keys.toList()

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
        }
    }

    override fun tick(tick: Ticking.Tick) {
        if (ending) return

        if (tick.isSecond(startingTick)) {
            itemCountdown.dec()
            if (itemCountdown.get() <= 0) {
                players.forEach { addItem(it) }
                itemCountdown.set(info.itemCountdown())
            }

            timeLeft.dec()
            if (timeLeft.get() <= 0)
                end(EndingCause.TIME_OVER)
        }

        tickEvents.filter { tick.isInInterval(startingTick, it.value) }.forEach { it.key() }
    }

    enum class EndingCause {
        FORCE,
        TIME_OVER,
        LAST_STANDING,
        DRAW,
        ERROR,
    }

    fun end(cause: EndingCause, winners: List<PillarPlayer> = listOf()) {
        if (ending) return
        ending = true

        for (p in initialPlayers) {
            when (cause) {
                EndingCause.FORCE -> {
                    p.showTitle(Title.title(
                        p.locale().component("info.end.force.title", color = NamedTextColor.YELLOW),
                        p.locale().component("info.end.force.subtitle", color = NamedTextColor.RED),
                    ))
                    warn("Stopped game forcefully.")
                }
                EndingCause.TIME_OVER -> {
                    p.showTitle(Title.title(
                        p.locale().component("info.end.time-over.title", color = NamedTextColor.GREEN),
                        p.locale().component("info.end.time-over.subtitle", color = NamedTextColor.YELLOW),
                    ))
                    info("Stopped game because the time is up.")
                }
                EndingCause.LAST_STANDING -> {
                    p.showTitle(Title.title(
                        p.locale().component("info.end.last-standing.title", winners.joinToString(" & "), color = NamedTextColor.GREEN),
                        p.locale().component("info.end.last-standing.subtitle", winners.sumOf { it.kills }.toString(), color = NamedTextColor.YELLOW),
                    ))
                    info("Stopped game because ${winners.joinToString()} won.")
                }
                EndingCause.DRAW -> {
                    p.showTitle(Title.title(
                        p.locale().component("info.end.draw.title", color = NamedTextColor.GREEN),
                        p.locale().component("info.end.draw.subtitle", winners.joinToString(" & "), color = NamedTextColor.YELLOW),
                    ))
                    info("Stopped game because ${winners.joinToString(" & ")} died at the same time, resulting in a draw.")
                }
                EndingCause.ERROR -> {
                    p.showTitle(Title.title(
                        component("Nobody wins!", color = NamedTextColor.RED),
                        component("An error occurred, resulting in no winner.", color = NamedTextColor.GRAY),
                    ))
                    error("Stopped game because ${winners.joinToString()} of an error. Error: #001")
                }
            }

            p.sendMessage(component("=== ").append(p.locale().component("info.end.time-over.stats")).append(component(" ===")).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
            initialPlayers.sortedByDescending { it.kills }.forEachIndexed { i, sorted ->
                p.sendMessage(miniMessage("<dark_gray>${i + 1}. <gray>${sorted.player.name} <dark_gray>(<gold>${sorted.kills}<red>⚔<dark_gray>)"))
            }
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
        clear()
    }

    fun clear() {
        GameManager.remove(this)
        initialPlayers.forEach { it.clear(true) }
        buildings.reset()
        bossBar?.stop()
    }
}
