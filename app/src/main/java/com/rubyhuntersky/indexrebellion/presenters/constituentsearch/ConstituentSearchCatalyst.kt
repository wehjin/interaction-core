package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.InteractionCatalyst

class ConstituentSearchCatalyst(private val getStarterActivity: () -> FragmentActivity) : InteractionCatalyst<Unit> {
    override fun catalyze(seed: Unit) {
        SharedConstituentSearchInteraction.reset()
        ConstituentSearchDialogFragment.newInstance()
            .show(getStarterActivity().supportFragmentManager, "constituent_search")
    }
}