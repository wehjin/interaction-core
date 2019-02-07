package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.interaction.InteractionCatalyst

class CorrectionDetailsCatalyst(private val getFragmentActivity: () -> FragmentActivity) : InteractionCatalyst {

    override fun catalyze() {
        val fragment = CorrectionDetailsDialogFragment.newInstance()
        fragment.show(getFragmentActivity().supportFragmentManager, fragment.javaClass.simpleName)
    }
}