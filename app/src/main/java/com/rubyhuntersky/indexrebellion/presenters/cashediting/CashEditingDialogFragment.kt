package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.cashediting.Action
import com.rubyhuntersky.interaction.cashediting.Vision
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.additions.Floor
import com.rubyhuntersky.vx.additions.plus
import kotlinx.android.synthetic.main.fragment_cash_editing.*
import kotlinx.android.synthetic.main.fragment_cash_editing.view.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
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

        saveButton.setOnClickListener {
            sendAction(Action.Save)
        }
    }

    private lateinit var dashView: Dash.View<FundingEditor, Nothing>

    override fun render(vision: Vision) {
        Log.d(this.javaClass.simpleName, "VISION: $vision")
        when (vision) {
            is Vision.Editing -> renderEditing(vision)
            is Vision.Idle -> dismiss()
        }
    }

    private fun renderEditing(vision: Vision.Editing) {
        val content = FundingEditor(
            title = "Update Funding",
            targetInput = Input(
                text = vision.edit,
                originalText = vision.oldCashAmount.toStatString(),
                label = "Cash Available",
                icon = Icon.ResId(R.drawable.ic_attach_money_black_24dp)
            )
        )
        dashView.setContent(content)
        saveButton.isEnabled = vision.canSave
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}