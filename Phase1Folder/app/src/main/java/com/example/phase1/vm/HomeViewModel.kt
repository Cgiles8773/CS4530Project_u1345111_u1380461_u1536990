package com.example.phase1.vm

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phase1.data.local.ImageRecord
import com.example.phase1.data.repository.ImageRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    val images = imageRepository.getImages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteImage(imageRecord: ImageRecord) {
        viewModelScope.launch {
            imageRepository.deleteImage(imageRecord)
        }
    }

    fun loadBitmap(imageRecord: ImageRecord): Bitmap? {
        return imageRepository.loadBitmap(imageRecord)
    }
    // Firebase state functions
    suspend fun login(email: String, password: String) : String?
    {
        return imageRepository.login(email, password)
    }
    suspend fun createUser(email: String, password: String) : String?
    {
        return imageRepository.createUser(email, password)
    }
    fun getUser() : FirebaseUser?
    {
        return imageRepository.getUser()
    }
    suspend fun logout() : String?
    {
        return imageRepository.logout()
    }

    suspend fun getAllDocuments() : List<DocumentSnapshot>?
    {
        return imageRepository.getAllDocumentsFirestore()
    }

    suspend fun getAllDocuments(uid: String) : List<DocumentSnapshot>?
    {
        val list = mutableListOf<DocumentSnapshot>()
        val documents = imageRepository.getAllDocumentsFirestore()
        if (documents == null) {return null}
        for (document in documents)
        {
            if(document.getString("uid") == uid)
            {
                list.add(document)
            }
        }
        return list
    }
    suspend fun getAllDocumentsExcluding(uid: String) : List<DocumentSnapshot>?
    {
        val list = mutableListOf<DocumentSnapshot>()
        val documents = imageRepository.getAllDocumentsFirestore()
        if (documents == null) {return null}
        for (document in documents)
        {
            if(document.getString("uid") != uid)
            {
                list.add(document)
            }
        }
        return list
    }
}