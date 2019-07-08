package com.rubyhuntersky.interaction.android

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ActivityInteraction<V : Any, A : Any>(
    interactionName: String,
    private val activity: AppCompatActivity,
    private val renderVision: (vision: V, sendAction: (A) -> Unit, edge: Edge) -> Unit
) : LifecycleObserver,
    Interaction<V, A> by AndroidEdge.findInteraction<V, A>(activity.toInteractionSearch(interactionName)) {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        AndroidEdge.setActivity(activity)
        renderings = visions
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { vision: V ->
                if (isEnding(vision)) {
                    finishActivity()
                } else {
                    renderVision(vision, this::sendAction, this.edge)
                }
            }
            .doOnComplete(::finishActivity)
            .subscribe()
    }

    private var renderings: Disposable? = null

    private fun finishActivity() {
        if (!finishedActivity && activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            finishedActivity = true
            activity.finish()
        }
    }

    private var finishedActivity = false

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        renderings?.dispose()
        AndroidEdge.unsetActivity(activity)
    }

    companion object {

        fun setInteractionSearchKey(intent: Intent, key: Long): Intent {
            return intent.apply { putExtra(INTERACTION_SEARCH_KEY, key) }
        }

        private const val INTERACTION_SEARCH_KEY = "interaction-search-key"

        private fun AppCompatActivity.toInteractionSearch(interactionName: String): InteractionSearch =
            when (val searchKey = toInteractionSearchKey()) {
                Edge.NULL_KEY -> InteractionSearch.ByName(interactionName)
                else -> InteractionSearch.ByKey(searchKey)
            }

        private fun AppCompatActivity.toInteractionSearchKey(): Long {
            return intent.getLongExtra(INTERACTION_SEARCH_KEY, Edge.NULL_KEY)
        }
    }
}