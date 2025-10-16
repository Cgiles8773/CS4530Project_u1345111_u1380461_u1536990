package com.example.phase1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageRecord: ImageRecord)

    @Delete
    suspend fun deleteImage(imageRecord: ImageRecord)

    @Query("SELECT * FROM images")
    fun getImages(): Flow<List<ImageRecord>>
}