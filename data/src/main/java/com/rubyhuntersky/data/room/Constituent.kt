package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

@Entity(
    primaryKeys = ["rebellionId", "assetSymbol"],
    foreignKeys = [
        ForeignKey(
            entity = Rebellion::class,
            parentColumns = ["id"],
            childColumns = ["rebellionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnderlyingAsset::class,
            parentColumns = ["symbol"],
            childColumns = ["assetSymbol"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Constituent(
    var rebellionId: Int,
    var assetSymbol: String
)