package com.rubyhuntersky.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class RebellionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String
)