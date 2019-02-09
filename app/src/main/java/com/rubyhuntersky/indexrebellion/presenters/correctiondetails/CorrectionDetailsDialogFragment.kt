package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.updateshares.UpdateSharesDialogFragment
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.interactions.CorrectionDetails
import com.rubyhuntersky.interaction.interactions.CorrectionDetails.Vision
import com.rubyhuntersky.interaction.interactions.common.InteractionRegistry
import com.rubyhuntersky.interaction.interactions.common.Persist
import kotlinx.android.synthetic.main.view_correction_details.*
import kotlin.random.Random


class CorrectionDetailsDialogFragment : InteractionBottomSheetDialogFragment<Vision, CorrectionDetails.Action>(
    layoutRes = R.layout.view_correction_details,
    directInteraction = null
) {

    override fun render(vision: Vision) {
        val unwrappedVision = (vision as Vision.Wrap).unwrap
        when (unwrappedVision) {
            is Persist.Vision.Reading -> {
                symbolTextView.text = getString(R.string.loading)
                symbolTextView.text = null
                currentSharesTextView.text = null
                updateSharesTextView.isEnabled = false
            }
            is Persist.Vision.Ready -> {
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
                    setOnClickListener {
                        sendAction(CorrectionDetails.Action.UpdateShares)
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(correction: Correction, getFragmentActivity: () -> FragmentActivity):
                CorrectionDetailsDialogFragment {

            val interaction = CorrectionDetails.Interaction(
                correctionBook = BehaviorBook(correction),
                updateSharesCatalyst = object : Catalyst<AssetSymbol> {
                    override fun catalyze(seed: AssetSymbol) =
                        UpdateSharesDialogFragment.catalyze(Pair(seed, getFragmentActivity))
                })

            return CorrectionDetailsDialogFragment()
                .also {
                    it.indirectInteractionKey = Random.nextLong()
                    InteractionRegistry.addInteraction(it.indirectInteractionKey, interaction)
                }
        }
    }
}