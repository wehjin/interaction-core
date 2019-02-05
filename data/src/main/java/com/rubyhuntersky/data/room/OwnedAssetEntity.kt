package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import java.math.BigDecimal

@Entity(
    primaryKeys = ["owner", "custodian", "account", "symbol"],
    foreignKeys = [
        ForeignKey(
            entity = OwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["owner"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnderlyingAssetEntity::class,
            parentColumns = ["symbol"],
            childColumns = ["symbol"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class OwnedAssetEntity(
    var owner: Int,
    var custodian: String,
    var account: String,
    var symbol: String,
    var shares: BigDecimal
)
