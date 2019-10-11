package com.rubyhuntersky.interaction.android

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction

@SuppressLint("StaticFieldLeak")
object AndroidEdge : Edge() {

    private val projectionBuilders = mutableMapOf<String, ProjectionSource<*, *>>()
    private var fragmentActivity: FragmentActivity? = null

    operator fun <V : Any, A : Any> plusAssign(projectionSource: ProjectionSource<V, A>) {
        addProjectionBuilder(projectionSource)
    }

    fun addProjectionBuilder(vararg projectionSource: ProjectionSource<*, *>) {
        projectionSource.forEach {
            projectionBuilders[it.group] = it
        }
    }

    override fun <V : Any, A : Any> presentInteraction(interaction: Interaction<V, A>): Long =
        super.presentInteraction(interaction)
            .also {
                projectionSource<A, V>(interaction.group).startProjection(fragmentActivity!!, interaction, it)
            }

    private fun <A : Any, V : Any> projectionSource(group: String): ProjectionSource<V, A> {
        return projectionBuilders[group]?.let {
            @Suppress("UNCHECKED_CAST")
            it as ProjectionSource<V, A>
        } ?: error("No projection source for group $group")
    }

    fun setActivity(fragmentActivity: FragmentActivity) {
        this.fragmentActivity = fragmentActivity
    }

    fun unsetActivity(fragmentActivity: FragmentActivity) {
        if (this.fragmentActivity == fragmentActivity) {
            this.fragmentActivity = null
        }
    }
}