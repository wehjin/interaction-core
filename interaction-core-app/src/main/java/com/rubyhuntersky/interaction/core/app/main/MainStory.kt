package com.rubyhuntersky.interaction.core.app.main

import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.StorySearch
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MainStory : Story<MainVision, Void, Void> {
    override val name: String
        get() = tag

    override val visions: Observable<MainVision> = BehaviorSubject.createDefault(
        MainVision.Idle as MainVision
    )

    companion object {
        private val tag = this::class.java.simpleName

        fun locateInEdge(edge: Edge): MainStory {
            val search = StorySearch.ByName(tag)
            return edge.findStory<MainVision, Void, Void>(search) as MainStory
        }
    }
}