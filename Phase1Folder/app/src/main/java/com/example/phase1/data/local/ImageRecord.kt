package com.example.phase1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val filepath: String,
    val date: String
)