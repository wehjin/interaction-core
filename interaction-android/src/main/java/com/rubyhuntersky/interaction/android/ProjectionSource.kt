package com.rubyhuntersky.interaction.android

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Interaction

interface ProjectionSource {
    val group: String
    fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long)
}