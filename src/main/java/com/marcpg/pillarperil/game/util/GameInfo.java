package com.marcpg.pillarperil.game.util;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.PillarPeril;
import com.marcpg.pillarperil.generation.CircularPillarGen;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.RandomPillarGen;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record GameInfo(String name, int itemCooldown, Time timeLimit, TextColor accentColor, Class<? extends Generator> generator, Predicate<Material> filter) {
    public GameInfo(String name, String key, Predicate<Material> filter) {
        this(
                name,
                PillarPeril.CONFIG.getInt("modes." + key + ".cooldown", 5),
                Time.parse(PillarPeril.CONFIG.getString("modes." + key + ".time-limit", "5min")),
                color(PillarPeril.CONFIG.getString("modes." + key + ".color", "#FFFFFF")),
                generator(PillarPeril.CONFIG.getString("modes." + key + ".generator", "circular")),
                filter
        );
    }

    private static TextColor color(@Nullable String color) {
        return color == null ? NamedTextColor.WHITE : TextColor.fromHexString(color);
    }

    private static Class<? extends Generator> generator(@Nullable String name) {
        if ("random".equals(name)) {
            return RandomPillarGen.class;
        } else {
            return CircularPillarGen.class;
        }
    }
}
