package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class RebellionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String
)

@Entity(
    primaryKeys = ["rebellion", "symbol"],
    foreignKeys = [
        ForeignKey(
            entity = RebellionEntity::class,
            parentColumns = ["id"],
            childColumns = ["rebellion"],
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
data class ConstituentEntity(
    var rebellion: Int,
    var symbol: String
)

@Entity
data class UnderlyingAssetEntity(
    @PrimaryKey var symbol: String,
    var name: String,
    var marketValue: BigDecimal,
    var shareValue: BigDecimal
)

@Entity
data class OwnerEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String
)

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
