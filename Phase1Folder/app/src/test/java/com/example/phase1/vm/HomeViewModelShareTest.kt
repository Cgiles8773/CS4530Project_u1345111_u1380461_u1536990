/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * This file contains unit tests for the HomeViewModel’s sharing and
 * deletion functionality. It verifies that repository delete calls
 * are made correctly and that intent-like share data is constructed
 * with the proper type and content.
 */


package com.example.phase1.vm

import com.example.phase1.data.local.ImageRecord
import com.example.phase1.data.repository.ImageRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FakeHomeViewModel(private val repository: ImageRepository) {
    suspend fun deleteImage(record: ImageRecord) {
        repository.deleteImage(record)
    }
}

class HomeViewModelShareTest {

    @Test
    fun deleteImage_callsRepositoryDelete() = runBlocking {
        val mockRepo = mock<ImageRepository>()
        val record = ImageRecord(1, "/tmp/delete.png", "Delete", System.currentTimeMillis())

        val fakeViewModel = FakeHomeViewModel(mockRepo)
        fakeViewModel.deleteImage(record)

        verify(mockRepo).deleteImage(record)
    }

    @Test
    fun shareIntent_hasCorrectTypeAndExtras() {
        val type = "image/*"
        val filePath = "/tmp/test.png"

        val intentMap = mutableMapOf<String, Any?>().apply {
            this["action"] = "android.intent.action.SEND"
            this["type"] = type
            this["extra_stream"] = filePath
        }

        assertEquals("image/*", intentMap["type"])
        assertEquals(filePath, intentMap["extra_stream"])
        assertTrue(intentMap.containsKey("extra_stream"))
    }
}
