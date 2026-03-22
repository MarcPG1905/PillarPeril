package com.marcpg.pillarperil.game

import com.marcpg.pillarperil.game.util.GameModifierInfo

interface GameModifierCompanion<T : GameModifier> {
    val modifierInfo: GameModifierInfo

    fun constructModifier(game: Game): T
}
