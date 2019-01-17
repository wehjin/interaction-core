package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.interactions.ConstituentSearch.Action
import com.rubyhuntersky.interaction.interactions.ConstituentSearch.Vision
import kotlinx.android.synthetic.main.fragment_constituent_search.*


class ConstituentSearchDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.fragment_constituent_search,
    interaction = SharedConstituentSearchInteraction
) {

    override fun render(vision: Vision) {
        when (vision) {
            is Vision.Idle -> renderIdle()
            is Vision.Searching -> Unit
            is Vision.Done -> dismiss()
        }
    }

    private fun renderIdle() {
        saveButton.setOnClickListener {
            sendAction(Action.Search("TSLA"))
            sendAction(Action.Save(AssetSymbol("TSLA"), MarketWeight(50000000000)))
        }
    }

    companion object {
        fun newInstance(): ConstituentSearchDialogFragment = ConstituentSearchDialogFragment()
    }
}