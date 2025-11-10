package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.util.Configuration
import org.bukkit.Location

abstract class HorizontalGen {
    abstract fun generate(): List<Location>

    protected fun location(x: Double, z: Double): Location = Location(null, x, Configuration.platformHeight, z)
}

interface HorGenCompanion<T : HorizontalGen> {
    val genConstructor: () -> T
    val namespace: String
}
