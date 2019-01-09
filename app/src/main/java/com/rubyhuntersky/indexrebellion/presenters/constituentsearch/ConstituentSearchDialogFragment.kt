package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.constituentsearch.ConstituentSearchAction
import com.rubyhuntersky.interaction.interactions.constituentsearch.ConstituentSearchVision
import kotlinx.android.synthetic.main.fragment_constituent_search.*


class ConstituentSearchDialogFragment :
    InteractionBottomSheetDialogFragment<ConstituentSearchVision, ConstituentSearchAction>(
        layoutRes = R.layout.fragment_constituent_search,
        interaction = SharedConstituentSearchInteraction
    ) {

    override fun render(vision: ConstituentSearchVision) {
        when (vision) {
            is ConstituentSearchVision.Idle -> renderIdle()
            is ConstituentSearchVision.Searching -> Unit
            is ConstituentSearchVision.Done -> dismiss()
        }
    }

    private fun renderIdle() {
        saveButton.setOnClickListener {
            sendAction(ConstituentSearchAction.Search("TSLA"))
            sendAction(ConstituentSearchAction.Save(AssetSymbol("TSLA"), MarketWeight(50000000000)))
        }
    }

    companion object {
        fun newInstance(): ConstituentSearchDialogFragment = ConstituentSearchDialogFragment()
    }
}