package com.rubyhuntersky.interaction.interactions

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.OwnedAsset
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.interaction.UpdateShares
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.books.RebellionConstituentBook
import org.junit.Test
import java.util.*

class UpdateSharesTest {

    @Test
    fun happy() {
        val rebellionBook = mock<RebellionBook>()
        val assetSymbol = AssetSymbol("AMD")
        val constituentBook = RebellionConstituentBook(rebellionBook, assetSymbol)

        val interaction = UpdateShares.Interaction(constituentBook)
        interaction.visionStream.test().assertValue { it is UpdateShares.Vision.Loading }

        val ownedAsset = OwnedAsset(
            assetSymbol = assetSymbol,
            shareCount = ShareCount.ONE,
            sharePrice = SharePrice.Unknown()
        )
        interaction.sendAction(UpdateShares.Action.Load(ownedAsset))
        interaction.visionStream.test().assertValue {
            it is UpdateShares.Vision.Prompt && !it.canUpdate && it.numberDelta is UpdateShares.NumberDelta.Undecided && it.ownedCount == 1
        }

        interaction.sendAction(UpdateShares.Action.NewChangeCount("9"))
        interaction.visionStream.test().assertValue {
            it is UpdateShares.Vision.Prompt && !it.canUpdate && it.numberDelta is UpdateShares.NumberDelta.Change
        }

        interaction.sendAction(UpdateShares.Action.NewPrice("1"))
        interaction.visionStream.test().assertValue {
            it is UpdateShares.Vision.Prompt && it.canUpdate
        }

        val date = Date(1000000)
        interaction.sendAction(UpdateShares.Action.Save(date))
        interaction.visionStream.test().assertValue {
            it is UpdateShares.Vision.Dismissed
        }
        verify(rebellionBook).updateShareCountPriceAndCash(
            assetSymbol, ShareCount.TEN, SharePrice.Sample(CashAmount.ONE, date), CashAmount(-9)
        )
    }
}