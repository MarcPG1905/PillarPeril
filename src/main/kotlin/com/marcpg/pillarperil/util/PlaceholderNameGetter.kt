package com.marcpg.pillarperil.util

data class PlaceholderNameGetter(val base: String) {
    operator fun invoke(values: Map<String, Any>): String = base.apply {
        values.forEach { replace("{${it.key}}", it.value.toString()) }
    }

    operator fun invoke(vararg values: Pair<String, Any>): String = invoke(mapOf(*values))
}
