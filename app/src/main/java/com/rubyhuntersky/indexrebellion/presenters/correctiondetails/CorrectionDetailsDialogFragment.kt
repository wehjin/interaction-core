package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.CorrectionDetails
import com.rubyhuntersky.interaction.interactions.CorrectionDetails.Vision
import com.rubyhuntersky.interaction.interactions.common.ReadWrite
import kotlinx.android.synthetic.main.view_correction_details.*


class CorrectionDetailsDialogFragment :
    InteractionBottomSheetDialogFragment<Vision, CorrectionDetails.Action>(
        layoutRes = R.layout.view_correction_details,
        interaction = CorrectionDetailsInteraction
    ) {

    override fun render(vision: Vision) {
        val unwrappedVision = (vision as Vision.Wrap).unwrap
        when (unwrappedVision) {
            is ReadWrite.Vision.Reading -> {
                symbolTextView.text = getString(R.string.loading)
                symbolTextView.text = null
                currentSharesTextView.text = null
                updateSharesTextView.isEnabled = false
            }
            is ReadWrite.Vision.Ready -> {
                val correction = unwrappedVision.value
                symbolTextView.text = correction.assetSymbol.toString().toUpperCase()
                currentSharesTextView.text = when (correction) {
                    is Correction.Hold -> getString(R.string.value_and_shares, "???", "???")
                    is Correction.Buy -> getString(R.string.value_and_shares, "???", "0")
                    is Correction.Sell -> getString(R.string.value_and_shares, "???", "0")
                }
                notAdviceTextView.text = when (correction) {
                    is Correction.Hold -> getString(R.string.hold_not_advice, "???")
                    is Correction.Buy -> getString(
                        R.string.buy_not_advice,
                        correction.deficit.toStatString(),
                        correction.deficit.toStatString()
                    )
                    is Correction.Sell -> getString(
                        R.string.buy_not_advice,
                        correction.surplus.toStatString(),
                        correction.surplus.toStatString()
                    )
                }
                with(updateSharesTextView) {
                    isEnabled = true
                    setOnClickListener { }
                }
            }
        }
    }

    companion object {
        fun newInstance(): CorrectionDetailsDialogFragment = CorrectionDetailsDialogFragment()
    }
}