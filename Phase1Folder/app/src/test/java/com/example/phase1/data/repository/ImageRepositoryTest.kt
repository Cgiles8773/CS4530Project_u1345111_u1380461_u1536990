/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains unit tests for the ImageRepository implementation.
 * It verifies that image saving and deletion operations correctly interact
 * with the DAO and file handler layers, ensuring repository logic behaves
 * as expected in isolation.
 */


package com.example.phase1.data.repository

import android.graphics.Bitmap
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.local.ImageRecord
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class ImageRepositoryTest {

    private lateinit var repo: ImageRepositoryImpl
    private lateinit var mockDao: ImageDao
    private lateinit var mockHandler: ImageHandler

    @Before
    fun setup() {
        mockDao = mock()
        mockHandler = mock()
        repo = ImageRepositoryImpl(mockDao, mockHandler)
    }

    @Test
    fun saveImage_insertsRecord_whenFileSaved() = runBlocking {
        val fakeBitmap = mock<Bitmap>()
        whenever(mockHandler.saveBitmapToFile(any(), any())).thenReturn("/tmp/test.png")

        val result = repo.saveImage("TestImage", fakeBitmap)

        assertTrue(result)
        verify(mockDao).insertImage(check {
            assertEquals("TestImage", it.name)
            assertTrue(it.filePath.contains("/tmp/test.png"))
        })
    }

    @Test
    fun deleteImage_removesRecordAndFile() = runBlocking {
        val record = ImageRecord(filePath = "/tmp/test.png", name = "DeleteMe", date = System.currentTimeMillis())

        repo.deleteImage(record)

        verify(mockDao).deleteImage(record)
        verify(mockHandler).deleteBitmapFile("/tmp/test.png")
    }

    @Test
    fun getImages_returnsFlowFromDao() {
        val records = listOf(ImageRecord(1, "/tmp/test.png", "Img", System.currentTimeMillis()))
        whenever(mockDao.getImages()).thenReturn(flowOf(records))

        val result = repo.getImages()
        assertNotNull(result)
    }
}
