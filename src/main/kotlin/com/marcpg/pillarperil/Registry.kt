package com.marcpg.pillarperil

import com.marcpg.pillarperil.generation.HorGenCompanion
import com.marcpg.pillarperil.generation.HorizontalGen
import com.marcpg.pillarperil.generation.VertGenCompanion
import com.marcpg.pillarperil.generation.VerticalGen

object Registry {
    val horizontalGenerators = listOf<HorGenCompanion<out HorizontalGen>>(
    ).associateBy { it.namespace }

    val verticalGenerators = listOf<VertGenCompanion<out VerticalGen>>(
    ).associateBy { it.namespace }

    fun load() {
        PillarPeril.LOG.info("[Registry] Loaded ${horizontalGenerators.size} horizontal generators as Map<Name, HorGen>.")
        PillarPeril.LOG.info("[Registry] Loaded ${verticalGenerators.size} vertical generators as Map<Name, VertGen>.")
    }
}
