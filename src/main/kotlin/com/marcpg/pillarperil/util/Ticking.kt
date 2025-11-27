package com.marcpg.pillarperil.util

interface Ticking {
    fun tick(tick: Tick)

    data class Tick(val number: Int) {
        fun isSecond(startingTick: Int): Boolean = isInInterval(startingTick, 20)
        fun isInInterval(startingTick: Int, interval: Int): Boolean = (number - startingTick) % interval == 0
    }
}
