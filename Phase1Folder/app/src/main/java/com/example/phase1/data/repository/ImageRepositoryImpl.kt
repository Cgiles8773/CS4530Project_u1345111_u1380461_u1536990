package com.example.phase1.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
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
    override suspend fun insertImage(imageRecord: ImageRecord) {
        imageDao.insertImage(imageRecord)
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
    override suspend fun getAllDocumentsFirestore() : List<DocumentSnapshot>?
    {
        val list = mutableListOf<DocumentSnapshot>()
        // Reference the 'user_drawings' collection
        db.collection("user_drawings")
            .get() // Retrieve all documents in the collection
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    list.add(document)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the query
                Log.w("FirestoreQuery", "Error getting documents: ", exception)
            }
        return list
    }

}

//val imageRef = storage.reference.child("apple.png")
//
//// State to hold the image URI
//var imageUri by remember { mutableStateOf<Uri?>(null) }
//
//// Use LaunchedEffect to fetch the download URL asynchronously
//LaunchedEffect(imageRef) {
//    try {
//        imageUri = imageRef.downloadUrl.await()
//    } catch (e: Exception) {
//        error = "Image download failed: ${e.message}"
//    }
//}
//
//// Display the image using the Coil library once the URI is available
//imageUri?.let {
//    AsyncImage(
//        model = it.toString(),
//        contentDescription = "Apple from Firebase Storage",
//        modifier = Modifier.height(150.dp)
//    )
//}