package com.example.phase1.vm

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    data class Image(
        val id: Int,
        val name: String,
        val filepath: String,
        val date: String,
        val image: Bitmap
    )

}