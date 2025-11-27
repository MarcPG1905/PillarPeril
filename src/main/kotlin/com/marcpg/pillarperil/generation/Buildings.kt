package com.marcpg.pillarperil.generation

import com.marcpg.pillarperil.game.Game
import org.bukkit.Location
import org.bukkit.block.data.BlockData

class Buildings(
    val game: Game,
    val horizontalGen: HorizontalGen,
    val verticalGen: VerticalGen,
) {
    val initialBlocks: MutableMap<Location, BlockData> = mutableMapOf()

    fun place(location: Location, data: BlockData = location.block.blockData) {
        if (location !in initialBlocks)
            initialBlocks[location.clone()] = data
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
    }
}
