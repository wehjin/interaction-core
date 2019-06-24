package com.rubyhuntersky.interaction.android

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ActivityInteraction<V : Any, A : Any>(
    private val activity: AppCompatActivity,
    interactionName: String,
    private val renderVision: (vision: V) -> Unit
) : LifecycleObserver, Interaction<V, A>
by AndroidEdge.findInteraction<V, A>(InteractionSearch.ByName(interactionName)) {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        AndroidEdge.setActivity(activity)
        renderings = visions
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (isEnding(it)) {
                    finishActivity()
                } else {
                    renderVision(it)
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
}