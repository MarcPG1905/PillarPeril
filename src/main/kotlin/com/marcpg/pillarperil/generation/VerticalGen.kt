package com.marcpg.pillarperil.generation

abstract class VerticalGen {
    abstract fun generate(x: Double, z: Double)

    protected fun execPlace(x: Double, y: Double, z: Double) {
    }
}

interface VertGenCompanion<T : VerticalGen> {
    val genConstructor: () -> T
    val namespace: String
}
