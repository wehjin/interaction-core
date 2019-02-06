package com.rubyhuntersky.data.assets

import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.common.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

sealed class SharePrice {

    @Serializable
    data class Unknown(
        @Serializable(with = DateSerializer::class)
        val date: Date = Date(0)
    ) : SharePrice()

    @Serializable
    data class Sample(
        val cashAmount: CashAmount,

        @Serializable(with = DateSerializer::class)
        val date: Date
    ) : SharePrice()
}