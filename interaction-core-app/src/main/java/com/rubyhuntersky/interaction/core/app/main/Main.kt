package com.rubyhuntersky.interaction.core.app.main

import android.util.Log
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.StorySearch
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class MainVision {
    data class Idle(val toSelectAction: () -> MainAction) : MainVision()
}

sealed class MainAction {
    object Select : MainAction()
}

class MainStory : Story<MainVision, MainAction, Void> {
    override val name: String
        get() = tag

    override val visions: Observable<MainVision> = BehaviorSubject.createDefault(
        MainVision.Idle { MainAction.Select } as MainVision
    )

    override fun update(action: MainAction) {
        when (action) {
            is MainAction.Select -> Log.d(tag, "SELECT")
        }
    }

    companion object {
        private val tag = this::class.java.simpleName

        fun locateInEdge(edge: Edge): MainStory {
            val search = StorySearch.ByName(tag)
            return edge.findStory<MainVision, MainAction, Void>(search) as MainStory
        }
    }
}