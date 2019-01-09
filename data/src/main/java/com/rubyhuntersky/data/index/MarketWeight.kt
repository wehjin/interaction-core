package com.rubyhuntersky.data.index

import java.math.BigDecimal

data class MarketWeight(private val value: BigDecimal) {
    constructor(double: Double) : this(BigDecimal.valueOf(double))
    constructor(long: Long) : this(BigDecimal.valueOf(long))

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
        val TEN = MarketWeight(BigDecimal.TEN)
    }
}