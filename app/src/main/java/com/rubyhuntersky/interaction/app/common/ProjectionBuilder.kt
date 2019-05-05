package com.rubyhuntersky.interaction.app.common

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Interaction

interface ProjectionBuilder {
    val name: String
    fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long)
}