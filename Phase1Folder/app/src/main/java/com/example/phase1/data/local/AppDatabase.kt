package com.example.phase1.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}