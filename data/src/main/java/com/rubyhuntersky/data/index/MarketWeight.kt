package com.rubyhuntersky.data.index

import java.math.BigDecimal

data class MarketWeight(private val value: BigDecimal) {

    companion object {
        val ZERO = MarketWeight(BigDecimal.ZERO)
    }
}