package com.rubyhuntersky.interaction.android

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction

@SuppressLint("StaticFieldLeak")
object AndroidEdge : Edge() {

    private val projectionBuilders = mutableMapOf<String, ProjectionSource>()
    private var fragmentActivity: FragmentActivity? = null

    operator fun plusAssign(projectionSource: ProjectionSource) = addProjectionBuilder(projectionSource)

    fun addProjectionBuilder(vararg projectionSource: ProjectionSource) {
        projectionSource.forEach {
            projectionBuilders[it.group] = it
        }
    }

    override fun presentInteraction(interaction: Interaction<*, *>): Long {
        val key = super.presentInteraction(interaction)
        projectionBuilders[interaction.group]!!.startProjection(fragmentActivity!!, interaction, key)
        return key
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