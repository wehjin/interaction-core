package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.Catalyst

class CorrectionDetailsCatalyst(private val getFragmentActivity: () -> FragmentActivity) : Catalyst<Correction> {

    override fun catalyze(seed: Correction) {
        val fragment = CorrectionDetailsDialogFragment.newInstance(seed, getFragmentActivity)
        fragment.show(getFragmentActivity().supportFragmentManager, fragment.javaClass.simpleName)
    }
}
