package com.rubyhuntersky.seismic_stampede.stories

import com.rubyhuntersky.seismic_stampede.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import com.rubyhuntersky.seismic_stampede.preinteraction.core.End.*
import com.rubyhuntersky.seismic_stampede.wishForLocationUsername
import com.rubyhuntersky.seismic_stampede.wishForNewPassword
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

        data class AddNote(
            val text: String,
            val session: Session
        ) : Action()

        data class AddPassword(
            val location: String? = null,
            val username: String? = null,
            val session: Session
        ) : Action()
    }

    @ExperimentalCoroutinesApi
    fun start(storybook: Storybook): Story2<Vision, Action> =
        storyOf("Main", init()) { action, vision ->
            Log.info("ACTION: $action")
            try {
                when (action) {
                    is Action.Ignore -> vision.toRevision()
                    is Action.Refresh -> (vision as Vision.Viewing).refresh().toRevision()
                    is Action.Quit -> Vision.Ended(action.message).toRevision(isLast = true)
                    is Action.AddNote -> addNote(action, vision, storybook)
                    is Action.AddPassword -> addPassword(action, vision, storybook)
                }
            } catch (t: Throwable) {
                Vision.Ended(t.localizedMessage).toRevision()
            }
        }

    @ExperimentalCoroutinesApi
    private fun addPassword(
        action: Action.AddPassword,
        vision: Vision,
        storybook: Storybook
    ): Revision<Vision, Action> {
        require(vision is Vision.Viewing)
        val session = action.session
        return if (action.location.isNullOrBlank() || action.username.isNullOrBlank()) {
            Vision.Viewing(session) and wishForLocationUsername(
                storybook
            ) { end ->
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

    @ExperimentalCoroutinesApi
    private fun addNote(
        action: Action.AddNote,
        vision: Vision,
        storybook: Storybook
    ): Revision<Vision, Action> {
        require(vision is Vision.Viewing)
        val (text, session) = action
        return if (session.keyStack is KeyStack.Empty) {
            Vision.Viewing(session) and wishForNewPassword(
                storybook
            ) { end ->
                when (end) {
                    is High -> {
                        val newSession = session.setKeyStack(KeyStack.Shallow(end.value))
                        Action.AddNote(text, newSession)
                    }
                    is Flat -> Action.Quit("Cancelled")
                    is Low -> Action.Quit(end.error.localizedMessage)
                }
            }
        } else {
            val newSession = session.addNote(text)
            Vision.Viewing(newSession).toRevision()
        }
    }

    private fun init(): Vision = Vision.Viewing(Session(KeyStack.Empty, vaultOf(defaultFolder)))
}