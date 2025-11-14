/**
 * Cloud Vision API repository.
 * Handles encoding, request construction, and network communication.
 */

package com.example.phase1.data.repository

import android.util.Log
import com.example.phase1.BuildConfig
import com.example.phase1.data.repository.vision.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class VisionRepository {

    // Http client with Android engine + JSON support
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // Simple GET request for debugging connectivity
    suspend fun testPlainGoogleCall(): Result<String> {
        return try {
            val text: String = client.get("https://www.google.com").body()
            Result.success(text.take(100)) // limit output
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Sends a Base64-encoded image to Cloud Vision API.
     */
    suspend fun analyzeImageBase64(base64: String): Result<VisionApiResult> {

        // Debug test request
        val googleTest = testPlainGoogleCall()
        googleTest.onSuccess {
            Log.d("AI", "Plain Google call SUCCESS: $it")
        }.onFailure {
            Log.e("AI", "Plain Google call FAILED", it)
        }

        return try {
            // Build Vision API request object
            val request = VisionRequest(
                requests = listOf(
                    VisionImageRequest(
                        image = VisionImage(content = base64),
                        features = listOf(
                            VisionFeature("LABEL_DETECTION", 10),
                            VisionFeature("OBJECT_LOCALIZATION", 10)
                        )
                    )
                )
            )

            // Debug logging
            Log.d("AI", "Vision URL = https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.VISION_API_KEY}")
            Log.d("AI", "Using API Key: '${BuildConfig.VISION_API_KEY}'")
            Log.d("AI", "Base64 length = ${base64.length}")

            // Execute POST request
            val response: VisionResponse =
                client.post("https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.VISION_API_KEY}") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

            // Extract first response
            val first = response.responses?.firstOrNull()
                ?: return Result.failure(Exception("Empty Vision API response"))

            Result.success(first)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
