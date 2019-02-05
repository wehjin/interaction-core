package com.rubyhuntersky.interaction.interactions

import com.nhaarman.mockitokotlin2.*
import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.interaction.books.RebellionBook
import com.rubyhuntersky.interaction.interactions.ConstituentSearch.Action
import com.rubyhuntersky.stockcatalog.*
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.math.BigDecimal

class ConstituentSearchTest {

    private val rebellionBehavior = BehaviorSubject.createDefault(Rebellion.SEED)

    private val rebellionBook = mock<RebellionBook> {
        on { reader } doReturn rebellionBehavior
        on { value } doReturn rebellionBehavior.value!!
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
        val network = mock<HttpNetwork> {
            on { request(any()) } doAnswer {
                Thread.sleep(1000)
                HttpNetworkResponse.ConnectionError("url", Exception("mocked"))
            }
        }
        StockCatalog(network).connect(interaction)
        interaction.onAction(Action.BeginSearch("TSLA"))
        interaction.onStockCatalogResult(
            StockCatalogResult.Samples(
                search = "TSLA",
                samples = listOf(
                    StockSample(
                        symbol = "TSLA",
                        sharePrice = BigDecimal.ONE,
                        marketCapitalization = BigDecimal.TEN,
                        issuer = "Tesla Motors"
                    )
                )
            )
        )
        interaction.onAction(Action.AddConstituent)

        argumentCaptor<Rebellion>().apply {
            verify(rebellionBook).write(capture())
            assertNotNull(firstValue.index.constituents.find { it.assetSymbol == AssetSymbol("TSLA") })
        }
    }
}