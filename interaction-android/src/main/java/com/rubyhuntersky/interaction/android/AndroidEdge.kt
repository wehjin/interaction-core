package com.rubyhuntersky.interaction.android

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction

@SuppressLint("StaticFieldLeak")
object AndroidEdge : Edge() {

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
        AndroidEdge.fragmentActivity = fragmentActivity
    }

    fun unsetActivity(fragmentActivity: FragmentActivity) {
        if (AndroidEdge.fragmentActivity == fragmentActivity) {
            AndroidEdge.fragmentActivity = null
        }
    }
}