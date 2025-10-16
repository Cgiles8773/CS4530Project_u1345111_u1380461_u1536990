package com.example.phase1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "images")
data class ImageRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val name: String,
    val date: Long
)
