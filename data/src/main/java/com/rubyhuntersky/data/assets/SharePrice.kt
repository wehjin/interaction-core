package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashAmount
import java.util.*

sealed class SharePrice {
    object UNKNOWN : SharePrice()
    data class SAMPLE(val cashAmount: CashAmount, val date: Date) : SharePrice()
}