package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashAmount
import java.util.*

sealed class SharePrice {
    object Unknown : SharePrice()
    data class Sampled(private val cashAmount: CashAmount, val date: Date) : SharePrice()
}