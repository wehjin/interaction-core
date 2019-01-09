package com.rubyhuntersky.indexrebellion.presenters.cashediting

import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingAction
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingVision
import kotlinx.android.synthetic.main.fragment_cash_editing.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<CashEditingVision, CashEditingAction>(
    layoutRes = R.layout.fragment_cash_editing,
    interaction = SharedCashEditingInteraction
) {
    override fun render(vision: CashEditingVision) {
        when (vision) {
            is CashEditingVision.Editing -> renderEditing()
            is CashEditingVision.Done -> dismiss()
        }
    }

    private fun renderEditing() {
        saveButton.setOnClickListener {
            sendAction(CashEditingAction.SaveCashChange)
        }
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}