package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.support.v4.app.DialogFragment
import com.rubyhuntersky.interaction.InteractionCatalyst

class ConstituentSearchCatalyst(private val showDialogFragment: (DialogFragment, String) -> Unit) :
    InteractionCatalyst {

    override fun catalyze() {
        SharedConstituentSearchInteraction.reset()
        showDialogFragment(ConstituentSearchDialogFragment.newInstance(), "constituent_search")
    }
}