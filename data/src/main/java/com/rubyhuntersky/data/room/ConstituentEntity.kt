package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

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