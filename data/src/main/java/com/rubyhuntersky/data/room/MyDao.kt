package com.rubyhuntersky.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update

@Dao
interface MyDao {

    @Insert
    fun insertRebellion(rebellionEntity: RebellionEntity): Long

    @Insert
    fun insertUnderlyingAsset(vararg underlyingAssetEntities: UnderlyingAssetEntity)

    @Insert
    fun insertConstituent(vararg constituentEntities: ConstituentEntity)

    @Insert
    fun insertOwner(ownerEntity: OwnerEntity): Long

    @Insert
    fun insertOwnedAsset(vararg ownedAssetEntities: OwnedAssetEntity)

    @Update
    fun updateUnderlyingAsset(vararg underlyingAssetEntities: UnderlyingAssetEntity)

    @Update
    fun updateOwnedAssetAndPlannedInvestment(ownedAssetEntity: OwnedAssetEntity, rebellionEntity: RebellionEntity)

    @Delete
    fun deleteOwnedAssets(vararg ownedAssets: OwnedAssetEntity)

    @Delete
    fun deleteConstituent(vararg constituents: ConstituentEntity)
}