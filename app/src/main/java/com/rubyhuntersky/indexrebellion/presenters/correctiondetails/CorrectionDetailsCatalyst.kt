package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.interaction.InteractionCatalyst
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.interactions.CorrectionDetails
import com.rubyhuntersky.interaction.interactions.common.Interaction

class CorrectionDetailsCatalyst(private val getFragmentActivity: () -> FragmentActivity) :
    InteractionCatalyst<Correction> {

    override fun catalyze(seed: Correction) {
        correctionBook.write(seed)
        val fragment = CorrectionDetailsDialogFragment.newInstance()
        fragment.show(getFragmentActivity().supportFragmentManager, fragment.javaClass.simpleName)
    }
}

private val correctionBook = BehaviorBook<Correction>()

object CorrectionDetailsInteraction : Interaction<CorrectionDetails.Vision, CorrectionDetails.Action>
by CorrectionDetails.Interaction(correctionBook)
