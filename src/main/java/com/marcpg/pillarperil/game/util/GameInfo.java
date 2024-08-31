package com.marcpg.pillarperil.game.util;

import com.marcpg.libpg.data.time.Time;
import com.marcpg.pillarperil.generation.Generator;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.function.Predicate;

public record GameInfo(String name, int itemCooldown, Time timeLimit, TextColor accentColor, Class<? extends Generator> generator, Predicate<Material> filter) {}
