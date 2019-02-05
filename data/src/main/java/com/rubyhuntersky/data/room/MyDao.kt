package com.rubyhuntersky.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update

@Dao
interface MyDao {

    @Insert
    fun insertRebellion(rebellion: Rebellion): Long

    @Insert
    fun insertUnderlyingAsset(vararg underlyingAssets: UnderlyingAsset)

    @Insert
    fun insertConstituent(vararg constituents: Constituent)

    @Insert
    fun insertOwner(owner: Owner): Long

    @Insert
    fun insertOwnedAsset(vararg ownedAssets: OwnedAsset)

    @Update
    fun updateUnderlyingAsset(vararg underlyingAssets: UnderlyingAsset)

    @Update
    fun updateOwnedAssetAndPlannedInvestment(ownedAsset: OwnedAsset, rebellion: Rebellion)

    @Delete
    fun deleteOwnedAssets(vararg ownedAssets: OwnedAsset)

    @Delete
    fun deleteConstituent(vararg constituents: Constituent)
}