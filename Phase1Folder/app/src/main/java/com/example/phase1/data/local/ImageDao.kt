package com.example.phase1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageRecord: ImageRecord)

    @Update
    suspend fun update(imageRecord: ImageRecord) //Unused

    @Delete
    suspend fun delete(imageRecord: ImageRecord)

    @Query("SELECT * FROM images ORDER BY name ASC")
    fun getAll(): Flow<List<ImageRecord>>
    @Query("SELECT * FROM images WHERE id = :id")
    fun get(id: Int): Flow<ImageRecord>
}