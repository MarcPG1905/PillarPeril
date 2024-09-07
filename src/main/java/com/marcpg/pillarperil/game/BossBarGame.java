package com.marcpg.pillarperil.game;

import com.marcpg.libpg.data.time.Time;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BossBarGame extends Game {
    protected final BossBar bossBar = BossBar.bossBar(Component.empty(), 0.0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);

    public BossBarGame(@NotNull Location center, int startTick, @NotNull List<Player> players) {
        super(center, startTick, players);
        bossBar.addViewer(initialAudience);
    }

    public void updateBossBar() {
        float left = (float) itemCooldown / info().itemCooldown();
        bossBar.name(Component.text("=== " + new Time(itemCooldown).getOneUnitFormatted() + " ===", keyStyle));
        bossBar.progress(left);
        bossBar.color(getColor(left));
    }

    @Override
    public void tickSecond() {
        super.tickSecond();
        updateBossBar();
    }

    @Override
    public void cleanup() {
        bossBar.removeViewer(initialAudience);
        super.cleanup();
    }

    private static BossBar.Color getColor(float left) {
        return left < 0.1 ? BossBar.Color.WHITE :
               left < 0.2 ? BossBar.Color.BLUE :
               left < 0.4 ? BossBar.Color.GREEN :
               left < 0.6 ? BossBar.Color.YELLOW :
               left < 0.8 ? BossBar.Color.RED :
               BossBar.Color.PINK;
    }
}
