package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingAction
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingVision
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.TitleDash
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.additions.Subtitle
import com.rubyhuntersky.vx.additions.TitleSubtitle
import com.rubyhuntersky.vx.additions.plus
import kotlinx.android.synthetic.main.fragment_cash_editing.*
import kotlinx.android.synthetic.main.fragment_cash_editing.view.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<CashEditingVision, CashEditingAction>(
    layoutRes = R.layout.fragment_cash_editing,
    directInteraction = SharedCashEditingInteraction
) {
    private val dash =
        TitleDash + Subtitle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashView = dash.enview(view.screenView, ViewId())
            .also {
                view.screenView.setContentView(it)
            }
    }

    private lateinit var dashView: Dash.View<TitleSubtitle, Nothing>

    override fun render(vision: CashEditingVision) {
        when (vision) {
            is CashEditingVision.Editing -> renderEditing()
            is CashEditingVision.Done -> dismiss()
        }
    }

    private fun renderEditing() {
        dashView.setContent(TitleSubtitle("Title", "Subtitle"))
        saveButton.setOnClickListener {
            sendAction(CashEditingAction.SaveCashChange)
        }
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}