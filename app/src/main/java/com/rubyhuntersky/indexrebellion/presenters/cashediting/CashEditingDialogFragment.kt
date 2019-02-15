package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingAction
import com.rubyhuntersky.interaction.interactions.cashediting.CashEditingVision
import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.TitleDash
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.additions.Subtitle
import com.rubyhuntersky.vx.additions.TitleSubtitle
import com.rubyhuntersky.vx.additions.plus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_cash_editing.*
import kotlinx.android.synthetic.main.fragment_cash_editing.view.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<CashEditingVision, CashEditingAction>(
    layoutRes = R.layout.fragment_cash_editing,
    directInteraction = SharedCashEditingInteraction
) {
    private lateinit var dashView: Dash.View<TitleSubtitle, Nothing>
    private val composite = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screenView = view.screenView
        val dashView = (TitleDash + Subtitle).enview(screenView, ViewId())
        dashView.setAnchor(Anchor(0, 0f))
        screenView.horizontalBound.subscribe {
            dashView.setLimit(Dash.Limit(it.first, it.second))
        }.addTo(composite)
        this.dashView = dashView
    }

    override fun onDestroyView() {
        composite.clear()
        super.onDestroyView()
    }

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