package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.local.ImageRecord
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    // Local DB
    fun getImages(): Flow<List<ImageRecord>>
    suspend fun saveImage(name: String, bitmap: Bitmap) : Boolean
    suspend fun insertImage(imageRecord: ImageRecord)
    suspend fun deleteImage(imageRecord: ImageRecord)
    fun loadBitmap(image: ImageRecord): Bitmap?
    fun loadBitmap(filepath: String): Bitmap?
    // Firebase
    fun getUser(): FirebaseUser?
    suspend fun createUser(email: String, password: String) : String?
    suspend fun login(email: String, password: String) : String?
    suspend fun logout() : String?
    suspend fun getAllDocumentsFirestore() : List<DocumentSnapshot>?
}