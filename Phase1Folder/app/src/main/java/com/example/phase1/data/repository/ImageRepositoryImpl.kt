package com.example.phase1.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val imageHandler: ImageHandler
) : ImageRepository {
    val auth = Firebase.auth
    val db = Firebase.firestore
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

    override fun getUser() : FirebaseUser?
    {
        return auth.currentUser
    }
    override suspend fun login(email: String, password: String) : String?
    {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            if(e.message == null)
            {
                return "Error logging in, try again"
            }
            return e.message
        }
        return null
    }
    override suspend fun logout() : String?
    {
        try {
            auth.signOut()
        }
        catch (e: Exception) {
            if(e.message == null)
            {
                return "Error logging out, try again"
            }
            return e.message
        }
        return null
    }

    override suspend fun createUser(email: String, password: String): String? {
        try {
            auth.createUserWithEmailAndPassword(email, password)
        }
        catch (e: Exception) {
            Log.d("AI", "Error creating user: ${e.message}")
            if(e.message == null)
            {
                return "Error creating user, try again"
            }
            return e.message
        }
        return null
    }
}