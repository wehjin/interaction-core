package com.rubyhuntersky.interaction.core

import io.reactivex.disposables.Disposable

class PendingInteractions {

    fun <V : Any, A : Any> follow(interaction: Interaction<V, A>, whenEnded: (V) -> Unit) {
        val id = interaction.hashCode().toLong()
        endings[id] = interaction.ending
            .subscribe { ending ->
                endings.remove(id)?.dispose()
                whenEnded(ending)
            }
    }

    private val endings = mutableMapOf<Long, Disposable>()

    val count: Int
        get() = endings.size

    fun dispose() {
        endings.values.forEach { it.dispose() }
        endings.clear()
    }
}