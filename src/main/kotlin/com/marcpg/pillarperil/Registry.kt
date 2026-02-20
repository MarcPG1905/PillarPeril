package com.marcpg.pillarperil

import com.marcpg.pillarperil.game.mode.*
import com.marcpg.pillarperil.generation.horizontal.CircularHorGen
import com.marcpg.pillarperil.generation.horizontal.RandomHorGen
import com.marcpg.pillarperil.generation.vertical.BlockVertGen
import com.marcpg.pillarperil.generation.vertical.PillarVertGen

object Registry {
    val horizontalGenerators = listOf(
        CircularHorGen,
        RandomHorGen,
    ).associateBy { it.namespace }

    val verticalGenerators = listOf(
        BlockVertGen,
        PillarVertGen,
    ).associateBy { it.namespace }

    val modes = listOf(
        BlockyGame,
        ChaosGame,
        CubeCraftGame,
        ItemOnlyGame,
        ItemShuffleGame,
        OriginalGame,
        PlayerShuffleGame,
    ).associateBy { it.gameInfo.namespace }

    fun load() {
        PillarPeril.LOG.info("[Registry] Loaded ${horizontalGenerators.size} horizontal generators as Map<Name, HorGen>.")
        PillarPeril.LOG.info("[Registry] Loaded ${verticalGenerators.size} vertical generators as Map<Name, VertGen>.")

        PillarPeril.LOG.info("[Registry] Loaded ${modes.size} modes as Map<Name, Mode>.")
    }
}
