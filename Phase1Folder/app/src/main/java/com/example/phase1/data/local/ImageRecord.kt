package com.example.phase1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageRecord(
    @PrimaryKey val name: String,
    val filePath: String,
    val date: Long
)
