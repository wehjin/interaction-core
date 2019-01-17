package com.rubyhuntersky.interaction.interactions

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.ConstituentSearch.Action
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertNotNull
import org.junit.Test

class ConstituentSearchTest {

    private val rebellionBehavior = BehaviorSubject.createDefault(Rebellion.SEED)

    private val rebellionBook = mock<RebellionBook> {
        on { reader } doReturn rebellionBehavior
    }

    private val interaction = ConstituentSearch.Interaction(rebellionBook)

    @Test
    fun startingVisionIsIdle() {
        interaction.visionStream.test()
            .assertSubscribed()
            .assertValue(ConstituentSearch.Vision.Idle)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun saveUpdatesRebellionBook() {
        interaction.onAction(Action.Search("TSLA"))
        interaction.onAction(Action.Save(AssetSymbol("TSLA"), MarketWeight.TEN))

        argumentCaptor<Rebellion>().apply {
            verify(rebellionBook).write(capture())
            assertNotNull(firstValue.index.constituents.find { it.assetSymbol == AssetSymbol("TSLA") })
        }
    }
}