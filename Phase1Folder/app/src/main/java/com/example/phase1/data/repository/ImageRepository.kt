package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.local.ImageRecord
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(): Flow<List<ImageRecord>>
    suspend fun saveImage(name: String, bitmap: Bitmap) : Boolean
    suspend fun deleteImage(imageRecord: ImageRecord)
    fun loadBitmap(image: ImageRecord): Bitmap?
    fun loadBitmap(filepath: String): Bitmap?

    fun getUser(): FirebaseUser?
    suspend fun createUser(email: String, password: String) : String?
    suspend fun login(email: String, password: String) : String?
    suspend fun logout() : String?
}