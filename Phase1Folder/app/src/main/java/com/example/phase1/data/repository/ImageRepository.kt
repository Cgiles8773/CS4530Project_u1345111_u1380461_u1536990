package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import kotlinx.coroutines.flow.Flow

class ImageRepository private constructor(
    private val imageDao: ImageDao,
    val imageHandler: ImageHandler
) {
    companion object {
        @Volatile
        private var INSTANCE: ImageRepository? = null
        fun getInstance(dao: ImageDao, handler: ImageHandler): ImageRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ImageRepository(dao, handler)
                INSTANCE = instance
                instance
            }
        }
    }

    val allImages: Flow<List<ImageRecord>> = imageDao.getAll()

    suspend fun saveImage(bitmap: Bitmap, name: String, date: String) {
        val path = imageHandler.saveBitmapToFile(bitmap, name)
        val record = ImageRecord(name = name, filepath = path, date = date)
        imageDao.insert(record)
    }

    fun loadBitmap(imageRecord: ImageRecord): Bitmap? =
        imageHandler.loadBitmapFromFile(imageRecord.filepath)

    suspend fun deleteImage(imageRecord: ImageRecord) {
        imageHandler.deleteBitmapFile(imageRecord.filepath)
        imageDao.delete(imageRecord)
    }
}