package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.Catalyst

class ConstituentSearchCatalyst(private val getStarterActivity: () -> FragmentActivity) : Catalyst<Unit> {
    override fun catalyze(seed: Unit) {
        SharedConstituentSearch.reset()
        ConstituentSearchDialogFragment.newInstance()
            .show(getStarterActivity().supportFragmentManager, "constituent_search")
    }
}