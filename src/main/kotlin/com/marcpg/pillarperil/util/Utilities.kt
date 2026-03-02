package com.marcpg.pillarperil.util

import com.marcpg.pillarperil.PillarPeril

fun Throwable.trackToFastStats() {
    if (Configuration.disableFastStats) return

    PillarPeril.PLUGIN.errorTracker?.trackError(this)
}
