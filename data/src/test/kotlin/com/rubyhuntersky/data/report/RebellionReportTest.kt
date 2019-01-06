package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.index.Constituent
import com.rubyhuntersky.data.index.Index
import com.rubyhuntersky.data.index.MarketWeight
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class RebellionReportTest {

    @Test
    fun currentInvestmentIsIndexCashEquivalent() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.Sample(cashAmount = CashAmount.TEN, date = Date()),
            ownedShares = ShareCount.TEN
        )
        val rebellion = Rebellion(
            index = Index(listOf(constituent), ""),
            newInvestment = CashAmount.TEN
        )
        val report = RebellionReport(rebellion)
        assertEquals(rebellion.index.cashEquivalentOfAllConstituents, report.currentInvestment)
    }

    @Test
    fun newInvestmentIsPublic() {
        val rebellion = Rebellion(
            index = Index.EMPTY,
            newInvestment = CashAmount.TEN
        )
        val report = RebellionReport(rebellion)
        assertEquals(CashAmount.TEN, report.newInvestment)
    }

    @Test
    fun fullInvestmentIsIndexCashEquivalentPlusNewInvestment() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.Sample(cashAmount = CashAmount.TEN, date = Date()),
            ownedShares = ShareCount.TEN
        )
        val newInvestment = CashAmount.TEN
        val rebellion = Rebellion(index = Index(listOf(constituent), ""), newInvestment = newInvestment)
        val report = RebellionReport(rebellion)
        assertEquals(rebellion.index.cashEquivalentOfAllConstituents + newInvestment, report.fullInvestment)
    }
}