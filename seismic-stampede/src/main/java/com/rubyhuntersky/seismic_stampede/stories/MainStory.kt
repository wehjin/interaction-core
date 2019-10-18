package com.rubyhuntersky.seismic_stampede.stories

import com.rubyhuntersky.seismic_stampede.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.End.*
import com.rubyhuntersky.seismic_stampede.stories.MainStory.Action
import com.rubyhuntersky.seismic_stampede.stories.MainStory.Vision
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
fun storyOfMain(storybook: Storybook): Story2<Vision, Action> = storyOf(
    family = "Main",
    init = {
        val session = Session(KeyStack.Empty, vaultOf(defaultFolder))
        Vision.Viewing(session) and wishForNewPassword(storybook) { ending ->
            when (ending) {
                is High -> Action.SetKeystack(KeyStack.Shallow(ending.value))
                is Flat -> Action.Quit("Cancelled")
                is Low -> Action.Quit(ending.error.localizedMessage)
            }
        }
    },
    update = { action, vision ->
        Log.info("ACTION: $action")
        try {
            when (action) {
                is Action.Ignore -> vision.toRevision()
                is Action.Refresh -> (vision as Vision.Viewing).refresh().toRevision()
                is Action.Quit -> Vision.Ended(action.message).toRevision(isLast = true)
                is Action.SetKeystack -> {
                    check(vision is Vision.Viewing)
                    Vision.Viewing(vision.session.setKeyStack(action.keyStack)).toRevision()
                }
                is Action.AddNote -> addNote(action, vision)
                is Action.AddPassword -> addPassword(action, vision, storybook)
            }
        } catch (t: Throwable) {
            Vision.Ended(t.localizedMessage).toRevision()
        }
    }
)

private fun addNote(action: Action.AddNote, vision: Vision): Revision<Vision, Action> {
    require(vision is Vision.Viewing && vision.session.keyStack is KeyStack.Empty)
    val (text, session) = action
    return Vision.Viewing(session.addNote(text)).toRevision()
}

@ExperimentalCoroutinesApi
private fun addPassword(
    action: Action.AddPassword,
    vision: Vision,
    storybook: Storybook
): Revision<Vision, Action> {
    require(vision is Vision.Viewing && vision.session.keyStack is KeyStack.Empty)
    val session = action.session
    return if (action.location.isNullOrBlank() || action.username.isNullOrBlank()) {
        Vision.Viewing(session) and wishForLocationUsername(storybook) { end ->
            when (end) {
                is High -> {
                    val (location, username) = end.value
                    Action.AddPassword(location, username, session)
                }
                is Flat, is Low -> Action.Refresh
            }
        }
    } else {
        val newSession = session.addPassword(action.location, action.username)
        Vision.Viewing(newSession).toRevision()
    }
}

object MainStory {

    sealed class Vision : Revisable<Vision, Action> {
        data class Viewing(val session: Session) : Vision() {
            fun refresh() = copy(session = session.refresh())
        }

        data class Ended(val message: String?) : Vision()
    }

    sealed class Action {
        object Ignore : Action()
        data class Quit(val message: String? = null) : Action()
        object Refresh : Action()

        data class SetKeystack(val keyStack: KeyStack) : Action()

        data class AddNote(val text: String, val session: Session) : Action()

        data class AddPassword(
            val location: String? = null,
            val username: String? = null,
            val session: Session
        ) : Action()
    }
}