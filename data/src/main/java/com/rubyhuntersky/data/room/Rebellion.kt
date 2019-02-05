package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Rebellion(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var plannedInvestment: BigDecimal
)