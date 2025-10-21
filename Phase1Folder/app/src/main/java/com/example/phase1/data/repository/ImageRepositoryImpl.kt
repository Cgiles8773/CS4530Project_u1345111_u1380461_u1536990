package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val imageHandler: ImageHandler
) : ImageRepository {

    override fun getImages(): Flow<List<ImageRecord>> {
        return imageDao.getImages()
    }

    override suspend fun saveImage(name: String, bitmap: Bitmap): Boolean {
        val filePath = imageHandler.saveBitmapToFile(bitmap, name)
        if (filePath != null) {
            imageDao.insertImage(ImageRecord(
                filePath = filePath,
                name = name,
                date = System.currentTimeMillis()))
            return true
        }
        return false
    }

    override suspend fun deleteImage(imageRecord: ImageRecord) {
        imageDao.deleteImage(imageRecord)
        imageHandler.deleteBitmapFile(imageRecord.filePath)
    }

    override fun loadBitmap(image: ImageRecord): Bitmap? {
        return imageHandler.loadBitmapFromFile(image.filePath)
    }

    override fun loadBitmap(filepath: String): Bitmap? {
        return imageHandler.loadBitmapFromFile(filepath)
    }
}