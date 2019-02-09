package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.Catalyst

class CorrectionDetailsCatalyst(private val getFragmentActivity: () -> FragmentActivity) : Catalyst<CorrectionDetails> {

    override fun catalyze(seed: CorrectionDetails) {
        CorrectionDetailsDialogFragment.newInstance(seed, getFragmentActivity)
            .show(
                getFragmentActivity().supportFragmentManager,
                CorrectionDetailsDialogFragment.newInstance(seed, getFragmentActivity).javaClass.simpleName
            )
    }
}
