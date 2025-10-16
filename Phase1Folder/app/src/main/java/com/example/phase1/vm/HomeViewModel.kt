package com.example.phase1.vm

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phase1.data.local.ImageRecord
import com.example.phase1.data.repository.ImageRepository
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
}