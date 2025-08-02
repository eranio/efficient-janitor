package com.eranio.efficientjanitor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BagDao {

    @Query("SELECT * FROM bags")
    fun getAllBags(): Flow<List<BagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBag(bag: BagEntity)

    @Query("DELETE FROM bags WHERE id = :id")
    suspend fun deleteBag(id: Long)

    @Query("DELETE FROM bags")
    suspend fun clearAllBags()
}