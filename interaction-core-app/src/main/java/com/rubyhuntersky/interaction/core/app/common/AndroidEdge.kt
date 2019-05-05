package com.rubyhuntersky.interaction.core.app.common

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction

interface ProjectionBuilder {
    val name: String
    fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long)
}

class AndroidEdge : Edge() {

    private val projectionBuilders = mutableMapOf<String, ProjectionBuilder>()
    private var fragmentActivity: FragmentActivity? = null

    fun addProjectionBuilder(projectionBuilder: ProjectionBuilder) {
        projectionBuilders[projectionBuilder.name] = projectionBuilder
    }

    override fun presentInteraction(interaction: Interaction<*, *>): Long {
        val key = super.presentInteraction(interaction)
        val name = interaction.name
        projectionBuilders[name]!!.startProjection(fragmentActivity!!, interaction, key)
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