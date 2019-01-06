package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Index
import com.rubyhuntersky.data.index.MarketWeight

class Report(private val index: Index, val newInvestment: CashAmount) {

    val currentInvestment: CashEquivalent by lazy { index.cashEquivalentOfAllConstituents }
    val fullInvestment: CashEquivalent by lazy { currentInvestment + newInvestment }

    val conclusion: ReportConclusion by lazy {
        val totalMarketWeight = index.totalMarketWeightOfIncludedConstituents
        if (totalMarketWeight == MarketWeight.ZERO) {
            ReportConclusion.AddConstituent
        } else {
            val fullInvestment = fullInvestment
            when (fullInvestment) {
                is CashEquivalent.Unknown -> ReportConclusion.RefreshPrices
                is CashEquivalent.Amount -> {
                    if (fullInvestment <= CashEquivalent.ZERO) {
                        val currentInvestment = currentInvestment as CashEquivalent.Amount
                        val constituentCorrections = index.includedOrOwnedConstituents
                            .map {
                                val actualWeight = if (currentInvestment <= 0) {
                                    0.0
                                } else {
                                    it.cashEquivalent as CashEquivalent.Amount / currentInvestment
                                }
                                if (actualWeight == 0.0) {
                                    ConstituentCorrection.Hold(
                                        assetSymbol = it.assetSymbol,
                                        weight = actualWeight
                                    )
                                } else {
                                    ConstituentCorrection.Sell(
                                        assetSymbol = it.assetSymbol,
                                        targetWeight = 0.0,
                                        actualWeight = actualWeight,
                                        surplus = it.cashEquivalent.toCashAmount()
                                    )
                                }
                            }
                        ReportConclusion.Divest(constituentCorrections)
                    } else {
                        val constituentCorrections = index.includedOrOwnedConstituents
                            .map {
                                val targetWeight = if (it.isRemoved) 0.0 else (it.marketWeight / totalMarketWeight)
                                val actualWeight = it.cashEquivalent as CashEquivalent.Amount / fullInvestment
                                when {
                                    actualWeight == targetWeight -> ConstituentCorrection.Hold(
                                        assetSymbol = it.assetSymbol,
                                        weight = targetWeight
                                    )
                                    actualWeight < targetWeight -> ConstituentCorrection.Buy(
                                        assetSymbol = it.assetSymbol,
                                        targetWeight = targetWeight,
                                        actualWeight = actualWeight,
                                        deficit = fullInvestment.cashAmount * (targetWeight - actualWeight)
                                    )
                                    else -> ConstituentCorrection.Sell(
                                        assetSymbol = it.assetSymbol,
                                        targetWeight = targetWeight,
                                        actualWeight = actualWeight,
                                        surplus = fullInvestment.cashAmount * (actualWeight - targetWeight)
                                    )
                                }
                            }
                        ReportConclusion.Maintain(constituentCorrections)
                    }
                }
            }
        }
    }

}