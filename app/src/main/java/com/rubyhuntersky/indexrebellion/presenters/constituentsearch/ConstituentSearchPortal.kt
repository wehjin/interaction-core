package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.core.Portal

class ConstituentSearchPortal(private val getStarterActivity: () -> FragmentActivity) :
    Portal<Unit> {
    override fun jump(carry: Unit) {
        SharedConstituentSearch.reset()
        ConstituentSearchDialogFragment.newInstance()
            .show(getStarterActivity().supportFragmentManager, "constituent_search")
    }
}