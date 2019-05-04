package com.rubyhuntersky.interaction.core.app.main

import android.util.Log
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class MainVision {
    data class Idle(val toSelectAction: () -> MainAction) : MainVision()
}

sealed class MainAction {
    object Select : MainAction()
}

class MainInteraction : Interaction<MainVision, MainAction> {
    override val name: String
        get() = tag

    override val visionStream: Observable<MainVision> = BehaviorSubject.createDefault(
        MainVision.Idle { MainAction.Select } as MainVision
    )

    override fun sendAction(action: MainAction) {
        when (action) {
            is MainAction.Select -> Log.d(tag, "SELECT")
        }
    }

    companion object {
        private val tag = this::class.java.simpleName

        fun locateInEdge(edge: Edge): MainInteraction {
            val search = InteractionSearch.ByName(tag)
            return edge.findInteraction<MainVision, MainAction, Void>(search) as MainInteraction
        }
    }
}