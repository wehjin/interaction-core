package com.rubyhuntersky.interaction.app.select

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Interaction

object SelectOptionProjectionSource : ProjectionSource {

    override val group: String = SelectOptionStory.TAG

    override fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long) {
        val dialog = SelectBottomSheetDialogFragment().also { it.edgeKey = key }
        val tag = "${interaction.group}/key"
        dialog.show(fragmentActivity.supportFragmentManager, tag)
    }
}