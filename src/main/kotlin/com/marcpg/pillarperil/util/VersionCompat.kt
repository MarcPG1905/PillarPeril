package com.marcpg.pillarperil.util

import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.inventory.ItemStack

fun <T> getIfClassExists(requiredClass: String, hasClass: () -> T, alternative: () -> T): T {
    try {
        Class.forName(requiredClass)
    } catch (_: ClassNotFoundException) {
        return alternative()
    }
    // Return outside the try to not catch exceptions possibly created by hasClass itself.
    return hasClass()
}

fun <T : Any> World.setGameRuleSafe(oldName: String, newName: String, value: T) {
    val field = try {
        // New game-rule system from 1.21.9, 1.21.10, or 1.21.11, not sure when exactly it got added:
        Class.forName("org.bukkit.GameRules").getField(newName)
    } catch (_: Exception) {
        Class.forName("org.bukkit.GameRule").getField(oldName)
    }
    @Suppress("UNCHECKED_CAST")
    setGameRule(field.get(null) as GameRule<T>, value)
}

val cachedAttributes = mutableMapOf<String, Attribute>()
fun Attributable.getAttributeSafe(name: String): AttributeInstance? {
    if (name in cachedAttributes)
        return getAttribute(cachedAttributes[name]!!)

    val attribute = Attribute::class.java.fields.firstOrNull { it.name.endsWith(name) }?.get(null) ?: return null
    cachedAttributes[name] = attribute as Attribute
    return getAttribute(attribute)
}

private var cachedItemStackCreator: ((Material) -> ItemStack)? = null
fun Material.toItemStackSafe(): ItemStack {
    if (cachedItemStackCreator == null) {
        try {
            val of = ItemStack::class.java.getMethod("of", Material::class.java)
            cachedItemStackCreator = { of(null, it) as ItemStack }
        } catch (_: NoSuchMethodException) {
            cachedItemStackCreator = { ItemStack(it) }
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
    }

    return cachedItemStackCreator!!(this)
}
