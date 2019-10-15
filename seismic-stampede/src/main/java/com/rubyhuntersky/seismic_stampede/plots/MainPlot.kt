package com.rubyhuntersky.seismic_stampede.plots

import com.rubyhuntersky.seismic_stampede.*
import com.rubyhuntersky.seismic_stampede.gather.core.gatherOf
import com.rubyhuntersky.seismic_stampede.gather.core.validWhenNotEmpty
import com.rubyhuntersky.seismic_stampede.preinteraction.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

object MainPlot {

    sealed class Vision : Revisable {
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
        storyOf("Main", init()) { action, vision, offer ->
            Log.info("ACTION: $action")
            try {
                when (action) {
                    is Action.Ignore -> vision.toRevision()
                    is Action.Refresh -> (vision as Vision.Viewing).refresh().toRevision()
                    is Action.Quit -> Vision.Ended(action.message).toRevision(isLast = true)
                    is Action.AddNote -> addNote(action, vision, storybook, offer)
                    is Action.AddPassword -> addPassword(vision, action, storybook, offer)
                }
            } catch (t: Throwable) {
                Vision.Ended(t.localizedMessage).toRevision()
            }
        }

    @ExperimentalCoroutinesApi
    private fun addPassword(
        vision: Vision,
        action: Action.AddPassword,
        storybook: Storybook,
        offer: (Action) -> Boolean
    ): Revision<Vision> {
        require(vision is Vision.Viewing)
        val session = action.session
        return if (action.location.isNullOrBlank() || action.username.isNullOrBlank()) {
            Vision.Viewing(session).also {
                val gather =
                    gatherOf("Location", validator = ::validWhenNotEmpty)
                        .and("Username", validator = ::validWhenNotEmpty)
                GatherPlot.start(gather, storybook).follow(offer) { progress ->
                    when (progress) {
                        is GatherPlot.Vision.Gathering -> null
                        is GatherPlot.Vision.Ended -> {
                            when (val end = progress.end) {
                                is End.High -> {
                                    val gathering = end.value
                                    val location = gather[0](gathering)!!
                                    val username = gather[1](gathering)!!
                                    Action.AddPassword(location, username, session)
                                }
                                is End.Flat, is End.Low -> Action.Refresh
                            }
                        }
                    }
                }
            }.toRevision()
        } else {
            val newSession = session.addPassword(action.location, action.username)
            Vision.Viewing(newSession).toRevision()
        }
    }

    @ExperimentalCoroutinesApi
    private fun addNote(
        action: Action.AddNote,
        vision: Vision,
        storybook: Storybook,
        offer: (Action) -> Boolean
    ): Revision<Vision> {
        require(vision is Vision.Viewing)
        val (text, session) = action
        return if (session.keyStack is KeyStack.Empty) {
            Vision.Viewing(session).also {
                PasswordPlot.start(storybook).follow(offer) { progress ->
                    when (progress) {
                        is PasswordPlot.Vision.Ended ->
                            when (val ending = progress.end) {
                                is End.High -> Action.AddNote(
                                    text = text,
                                    session = session.setKeyStack(KeyStack.Shallow(ending.value))
                                )
                                is End.Flat -> Action.Quit("Cancelled")
                                is End.Low -> Action.Quit(ending.error.localizedMessage)
                            }
                        else -> null
                    }
                }
            }.toRevision()
        } else {
            val newSession = session.addNote(text)
            Vision.Viewing(newSession).toRevision()
        }
    }

    private fun init(): Vision = Vision.Viewing(Session(KeyStack.Empty, vaultOf(defaultFolder)))
}