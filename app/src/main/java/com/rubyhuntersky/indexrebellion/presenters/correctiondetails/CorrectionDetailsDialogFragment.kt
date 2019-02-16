package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.updateshares.UpdateSharesDialogFragment
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.BehaviorBook
import com.rubyhuntersky.interaction.common.InteractionRegistry
import com.rubyhuntersky.interaction.correctiondetails.Action
import com.rubyhuntersky.interaction.correctiondetails.CorrectionDetailsInteractionImpl
import com.rubyhuntersky.interaction.correctiondetails.Vision
import kotlinx.android.synthetic.main.view_correction_details.*
import kotlin.random.Random


class CorrectionDetailsDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.view_correction_details,
    directInteraction = null
) {

    override fun render(vision: Vision) {
        when (vision) {
            is Vision.Loading -> {
                symbolTextView.text = getString(R.string.loading)
                symbolTextView.text = null
                currentSharesTextView.text = null
                updateSharesTextView.isEnabled = false
            }
            is Vision.Viewing -> {
                val details = vision.details
                symbolTextView.text = details.assetSymbol.toString().toUpperCase()
                currentSharesTextView.text = getString(
                    R.string.n_shares,
                    details.ownedCount.toCountString(),
                    details.ownedValue.toStatString()
                )
                val delta = details.targetValue - details.ownedValue
                val deltaDirection = delta.compareTo(CashAmount.ZERO)
                notAdviceTextView.text = when {
                    deltaDirection > 0 -> getString(
                        R.string.buy_not_advice,
                        delta.toStatString(),
                        details.targetValue.toStatString()
                    )
                    deltaDirection < 0 -> getString(
                        R.string.sell_not_advice,
                        (-delta).toStatString(),
                        details.targetValue.toStatString()
                    )
                    else -> getString(R.string.hold_not_advice)
                }
                with(updateSharesTextView) {
                    isEnabled = true
                    setOnClickListener {
                        sendAction(Action.UpdateShares)
                        dismiss()
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(
            details: CorrectionDetails,
            getFragmentActivity: () -> FragmentActivity
        ): CorrectionDetailsDialogFragment {
            val detailsBook = BehaviorBook(details)
            val updateSharesCatalyst = object : Catalyst<AssetSymbol> {
                override fun catalyze(seed: AssetSymbol) =
                    UpdateSharesDialogFragment.catalyze(Pair(seed, getFragmentActivity))
            }
            val interaction = CorrectionDetailsInteractionImpl(
                detailsBook,
                updateSharesCatalyst
            )
            return CorrectionDetailsDialogFragment().also {
                it.indirectInteractionKey = Random.nextLong()
                InteractionRegistry.addInteraction(it.indirectInteractionKey, interaction)
            }
        }
    }
}