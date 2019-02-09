package com.rubyhuntersky.indexrebellion.presenters.updatesharecount

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.common.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.Catalyst
import com.rubyhuntersky.interaction.books.RebellionConstituentBook
import com.rubyhuntersky.interaction.interactions.UpdateShares
import com.rubyhuntersky.interaction.interactions.common.InteractionRegistry
import kotlin.random.Random


class UpdateSharesDialogFragment : InteractionBottomSheetDialogFragment<UpdateShares.Vision, UpdateShares.Action>(
    layoutRes = R.layout.view_update_share_count,
    directInteraction = null
) {

    override fun render(vision: UpdateShares.Vision) {
    }

    companion object : Catalyst<Pair<AssetSymbol, () -> FragmentActivity>> {

        override fun catalyze(seed: Pair<AssetSymbol, () -> FragmentActivity>) {
            val key = Random.nextLong()
            val fragment = UpdateSharesDialogFragment()
                .also {
                    it.indirectInteractionKey = key
                    InteractionRegistry.addInteraction(
                        key = it.indirectInteractionKey,
                        interaction = UpdateShares.Interaction(
                            RebellionConstituentBook(SharedRebellionBook, seed.first)
                        )
                    )
                }
            fragment.show(seed.second().supportFragmentManager, key.toString())
        }
    }
}

