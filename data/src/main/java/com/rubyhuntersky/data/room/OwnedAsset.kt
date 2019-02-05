package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import java.math.BigDecimal

@Entity(
    primaryKeys = ["ownerId", "custodianId", "accountId", "assetSymbol"],
    foreignKeys = [
        ForeignKey(
            entity = Owner::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
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
data class OwnedAsset(
    var ownerId: Int,
    var custodianId: String,
    var accountId: String,
    var assetSymbol: String,
    var shares: BigDecimal
)
