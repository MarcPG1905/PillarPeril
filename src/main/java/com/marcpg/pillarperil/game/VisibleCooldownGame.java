package com.marcpg.pillarperil.game;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.gen.Generator;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class VisibleCooldownGame extends Game {
    protected final BossBar bossBar = BossBar.bossBar(Component.empty(), 0.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    protected VisibleCooldownGame(@NotNull List<Player> players, Generator generator, Predicate<Material> filter) {
        super(players, generator, filter);
        players.forEach(bossBar::addViewer);
    }

    @Override
    public void onSecond() {
        super.onSecond();
        updateBossBar(timeLeft().get());
    }

    private void updateBossBar(long timeLeft) {
        long cooldown = itemCooldown().get();
        long untilNext = timeLeft % cooldown;
        float progress = (float) (cooldown - untilNext) / cooldown;
        bossBar.name(Component.text("Next Block: " + (untilNext == 0 ? "Now!" : new Time(untilNext).getOneUnitFormatted())));
        bossBar.progress(progress);
        bossBar.color(getColor(progress));
    }

    private static BossBar.Color getColor(float progress) {
        return progress > 0.9 ? BossBar.Color.WHITE :
               progress > 0.8 ? BossBar.Color.BLUE :
               progress > 0.6 ? BossBar.Color.GREEN :
               progress > 0.4 ? BossBar.Color.YELLOW :
               progress > 0.2 ? BossBar.Color.RED :
               BossBar.Color.PINK;
    }
}
