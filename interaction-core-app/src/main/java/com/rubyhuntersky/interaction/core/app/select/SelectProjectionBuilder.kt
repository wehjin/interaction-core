package com.rubyhuntersky.interaction.core.app.select

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.app.common.ProjectionBuilder

class SelectProjectionBuilder : ProjectionBuilder {
    override val name: String = SelectInteraction.TAG

    override fun <V, A> startProjection(fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long) {
        val dialog = SelectBottomSheetDialogFragment()
            .also { it.edgeKey = key }
        val tag = "${interaction.name}/key"
        dialog.show(fragmentActivity.supportFragmentManager, tag)
    }
}