/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains instrumented tests for the Room ImageDao.
 * These tests verify that the database can insert, query, and delete
 * image records properly using an in-memory database instance.
 */


package com.example.phase1.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.phase1.data.local.AppDatabase
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class   ImageDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ImageDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.imageDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveImageRecord() = runBlocking {
        val record = ImageRecord(filePath = "/tmp/test.png", name = "Test", date = System.currentTimeMillis())
        dao.insertImage(record)

        val images = dao.getImages().first()
        assertTrue(images.any { it.name == "Test" })
    }

    @Test
    fun deleteImageRecord_removesIt() = runBlocking {
        val record = ImageRecord(filePath = "/tmp/test.png", name = "ToDelete", date = System.currentTimeMillis())
        dao.insertImage(record)
        dao.deleteImage(record)

        val images = dao.getImages().first()
        assertFalse(images.any { it.name == "ToDelete" })
    }
}
