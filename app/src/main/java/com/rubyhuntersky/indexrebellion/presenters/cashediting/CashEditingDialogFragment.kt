package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingAction
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingVision
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.additions.Floor
import com.rubyhuntersky.vx.additions.plus
import kotlinx.android.synthetic.main.fragment_cash_editing.*
import kotlinx.android.synthetic.main.fragment_cash_editing.view.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<CashEditingVision, CashEditingAction>(
    layoutRes = R.layout.fragment_cash_editing,
    directInteraction = SharedCashEditingInteraction
) {
    data class FundingEditor(
        val title: String,
        val targetInput: Input
    ) {
        fun toPair() = Pair(title, targetInput)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dash =
            TitleDash + Floor(InputDash, FundingEditor::toPair)

        dashView = dash.enview(view.screenView, ViewId())
            .also {
                view.screenView.setContentView(it)
            }
    }

    private lateinit var dashView: Dash.View<FundingEditor, Nothing>

    override fun render(vision: CashEditingVision) {
        when (vision) {
            is CashEditingVision.Editing -> renderEditing()
            is CashEditingVision.Done -> dismiss()
        }
    }

    private fun renderEditing() {
        val content = FundingEditor(
            title = "Set funding target",
            targetInput = Input(
                label = "Funding Goal",
                text = ""
            )
        )
        dashView.setContent(content)
        saveButton.setOnClickListener {
            sendAction(CashEditingAction.SaveCashChange)
        }
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}