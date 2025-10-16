package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.local.ImageRecord
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(): Flow<List<ImageRecord>>
    suspend fun saveImage(name: String, bitmap: Bitmap) : Boolean
    suspend fun deleteImage(imageRecord: ImageRecord)
    fun loadBitmap(image: ImageRecord): Bitmap?
}