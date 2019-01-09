package com.rubyhuntersky.interaction.interactions.constituentsearch

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.index.MarketWeight
import com.rubyhuntersky.interaction.books.RebellionBook
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertNotNull
import org.junit.Test

class ConstituentSearchInteractionTest {

    private val rebellionBehavior = BehaviorSubject.createDefault(Rebellion.SEED)

    private val rebellionBook = mock<RebellionBook> {
        on { reader } doReturn rebellionBehavior
    }

    private val interaction = ConstituentSearchInteraction(rebellionBook)

    @Test
    fun startingVisionIsIdle() {
        interaction.visionStream.test()
            .assertSubscribed()
            .assertValue(ConstituentSearchVision.Idle)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun saveUpdatesRebellionBook() {
        interaction.update(ConstituentSearchAction.Search("TSLA"))
        interaction.update(ConstituentSearchAction.Save(AssetSymbol("TSLA"), MarketWeight.TEN))

        argumentCaptor<Rebellion>().apply {
            verify(rebellionBook).write(capture())
            assertNotNull(firstValue.index.constituents.find { it.assetSymbol == AssetSymbol("TSLA") })
        }
    }
}