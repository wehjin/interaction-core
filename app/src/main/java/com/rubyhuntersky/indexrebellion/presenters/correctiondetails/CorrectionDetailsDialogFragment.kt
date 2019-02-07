package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.BehaviorInteraction
import kotlinx.android.synthetic.main.view_correction_details.*


class CorrectionDetailsDialogFragment : InteractionBottomSheetDialogFragment<Boolean, Unit>(
    layoutRes = R.layout.view_correction_details,
    interaction = object : BehaviorInteraction<Boolean, Unit>(true) {
        override fun onAction(action: Unit) {
        }
    }
) {

    override fun render(vision: Boolean) {
        updateSharesTextView.setOnClickListener { }
    }

    companion object {
        fun newInstance(): CorrectionDetailsDialogFragment = CorrectionDetailsDialogFragment()
    }
}