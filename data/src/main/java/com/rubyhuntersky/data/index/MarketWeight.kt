package com.rubyhuntersky.data.index

import java.math.BigDecimal

data class MarketWeight(private val value: BigDecimal) {

    operator fun plus(other: MarketWeight): MarketWeight = MarketWeight(value + other.value)

    operator fun div(other: MarketWeight): Double = (value / other.value).toDouble()

    override fun equals(other: Any?): Boolean = if (other is MarketWeight) {
        value.compareTo(other.value) == 0
    } else {
        false
    }

    override fun hashCode(): Int = value.toDouble().hashCode()

    companion object {
        val ZERO = MarketWeight(BigDecimal.ZERO)
    }
}