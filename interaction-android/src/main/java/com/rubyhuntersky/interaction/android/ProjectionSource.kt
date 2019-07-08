package com.rubyhuntersky.interaction.android

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Interaction

interface ProjectionSource<V : Any, out A : Any> {
    val group: String
    fun startProjection(activity: FragmentActivity, interaction: Interaction<V, A>, key: Long)
}