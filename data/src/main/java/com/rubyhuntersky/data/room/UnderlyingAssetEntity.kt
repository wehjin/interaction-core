package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class UnderlyingAssetEntity(
    @PrimaryKey var symbol: String,
    var name: String,
    var marketValue: BigDecimal,
    var shareValue: BigDecimal
)