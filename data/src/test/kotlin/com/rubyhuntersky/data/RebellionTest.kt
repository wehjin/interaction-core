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

class RebellionTest {

    @Test
    fun seedRebellion() {
        val rebellion = Rebellion.SEED
        assertNotNull(rebellion)
    }

    @Test
    fun fullInvestmentEqualsNewInvestmentWhenIndexIsEmpty() {
        val newInvestments = listOf(CashAmount.ONE, CashAmount.TEN)
        val emptyIndex = Index.EMPTY

        val fullInvestments = newInvestments
            .map { newInvestment -> Rebellion(index = emptyIndex, newInvestment = newInvestment) }
            .map { rebellion -> rebellion.fullInvestment }

        assertEquals(fullInvestments, listOf(CashEquivalent.ONE, CashEquivalent.TEN))
    }

    @Test
    fun fullInvestmentIsUnknownWhenIndexIncludesUnpricedConstituent() {
        val unpricedConstituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice.Unknown,
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(unpricedConstituent), memo = "")
        val rebellion = Rebellion(index, CashAmount.TEN)
        assertEquals(rebellion.fullInvestment, CashEquivalent.Unknown)
    }
}