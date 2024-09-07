package com.marcpg.pillarperil.game.util;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.libpg.lang.Translation;
import com.marcpg.pillarperil.PillarPeril;
import com.marcpg.pillarperil.generation.Generator;
import com.marcpg.pillarperil.generation.Platform;
import com.marcpg.pillarperil.generation.generator.CircularPillarGen;
import com.marcpg.pillarperil.generation.generator.RandomPillarGen;
import com.marcpg.pillarperil.generation.platform.BlockPlatform;
import com.marcpg.pillarperil.generation.platform.PillarPlatform;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Predicate;

public record GameInfo(String key, int itemCooldown, Time timeLimit, TextColor accentColor, Class<? extends Generator> generator, Class<? extends Platform> platforms, Predicate<Material> filter) {
    public GameInfo(String key, Predicate<Material> filter) {
        this(
                key,
                PillarPeril.CONFIG.getInt("modes." + key + ".cooldown", 5),
                Time.parse(PillarPeril.CONFIG.getString("modes." + key + ".time-limit", "5min")),
                color(PillarPeril.CONFIG.getString("modes." + key + ".color", "#FFFFFF")),
                generator(PillarPeril.CONFIG.getString("modes." + key + ".generator", "circular")),
                platforms(PillarPeril.CONFIG.getString("modes." + key + ".platforms", "pillars")),
                filter
        );
    }

    public String name(Locale l) {
        return Translation.string(l, "name." + key);
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

    private static Class<? extends Platform> platforms(@Nullable String name) {
        if ("blocks".equals(name)) {
            return BlockPlatform.class;
        } else {
            return PillarPlatform.class;
        }
    }
}
