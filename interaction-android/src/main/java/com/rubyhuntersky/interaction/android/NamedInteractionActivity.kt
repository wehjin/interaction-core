package com.rubyhuntersky.interaction.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class NamedInteractionActivity<V : Any, A : Any>(
    private val interactionName: String
) : AppCompatActivity() {

    protected abstract fun renderVision(vision: V)

    protected fun sendAction(action: A) = activityInteraction.sendAction(action)

    private val activityInteraction
            by lazy {
                ActivityInteraction<V, A>(this, interactionName, this::renderVision)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(activityInteraction)
    }
}
