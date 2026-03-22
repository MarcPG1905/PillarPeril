package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.game.Game
import org.bukkit.Location
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class Buildings(
    val game: Game,
    val horizontalGen: HorizontalGen,
    val verticalGen: VerticalGen,
) {
    var placedRadius = 0.0

    val initialBlocks = mutableMapOf<Location, BlockData>()
    val spawnedEntities = mutableListOf<Entity>()

    fun placeBlock(x: Number, y: Number, z: Number, type: BlockType) = placeBlock(Location(game.world, x.toDouble(), y.toDouble(), z.toDouble()), type)

    fun placeBlock(location: Location, type: BlockType) {
        registerPlace(location)
        location.block.setBlockData(type.createBlockData(), false)
    }

    fun registerPlace(location: Location, data: BlockData = location.block.blockData) {
        if (location !in initialBlocks) {
            initialBlocks[location.clone()] = data

            val distance = location.distance(game.center)
            if (distance > placedRadius)
                placedRadius = distance
        }
    }

    fun registerSpawn(entity: Entity) {
        if (entity !is Player)
            spawnedEntities.add(entity)
    }

    fun generate(): List<Location> {
        val locations = horizontalGen.generate()
        locations.forEach { verticalGen.generate(it.x, it.z) }
        return locations
    }

    fun reset() {
        // First, place with physics to influence stuff like fluids.
        initialBlocks.forEach { (l, b) -> l.block.setBlockData(b, true) }

        // Then, force the placement for anything that wasn't properly placed before.
        initialBlocks.forEach { (l, b) -> l.block.setBlockData(b, false) }

        // Kill all entities spawned during this game.
        spawnedEntities.forEach { it.remove() }
    }
}
