package com.rubyhuntersky.data

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Constituent
import com.rubyhuntersky.data.index.Index
import com.rubyhuntersky.data.index.MarketWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

class RebellionTest {

    @Test
    fun seedRebellion() {
        assertNotNull(Rebellion.SEED)
    }

    @Test
    fun fullInvestmentEqualsNewInvestmentWhenIndexIsEmpty() {
        val emptyIndex = Index.EMPTY
        val newInvestments = listOf(CashAmount.ONE, CashAmount.TEN)
        val fullInvestments = newInvestments
            .map { newInvestment -> Rebellion(index = emptyIndex, newInvestment = newInvestment) }
            .map { rebellion -> rebellion.fullInvestment }

        assertEquals(listOf(CashEquivalent.ONE, CashEquivalent.TEN), fullInvestments)
    }

    @Test
    fun fullInvestmentIsUnknownWhenIndexIncludesUnpricedConstituent() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.UNKNOWN,
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "")
        val rebellion = Rebellion(index = index, newInvestment = CashAmount.TEN)
        assertEquals(CashEquivalent.UNKNOWN, rebellion.fullInvestment)
    }

    @Test
    fun fullInvestmentCombinesIndexCashEquivalentAndNewInvestment() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.SAMPLE(CashAmount.TEN, Date()),
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "")
        val rebellion = Rebellion(index = index, newInvestment = CashAmount.ONE)
        assertEquals(CashEquivalent.AMOUNT(CashAmount(11)), rebellion.fullInvestment)
    }
}