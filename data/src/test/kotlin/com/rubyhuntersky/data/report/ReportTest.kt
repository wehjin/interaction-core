package com.rubyhuntersky.data.report

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

class ReportTest {

    @Test
    fun currentInvestmentIsIndexCashEquivalent() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.Sample(cashAmount = CashAmount.TEN, date = Date()),
            ownedShares = ShareCount.TEN
        )
        val index = Index(listOf(constituent), "")
        val report = Report(index, CashAmount.TEN)
        assertEquals(index.cashEquivalentOfAllConstituents, report.currentInvestment)
    }

    @Test
    fun newInvestmentIsPublic() {
        val report = Report(index = Index.EMPTY, newInvestment = CashAmount.TEN)
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
        val index = Index(listOf(constituent), "")
        val report = Report(index, CashAmount.TEN)
        assertEquals(index.cashEquivalentOfAllConstituents + CashAmount.TEN, report.fullInvestment)
    }
}