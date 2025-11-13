package com.example.phase1.data.repository

import android.util.Log
import com.example.phase1.BuildConfig
import com.example.phase1.data.repository.vision.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.request.get
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android


class VisionRepository {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun testPlainGoogleCall(): Result<String> {
        return try {
            val responseText: String = client.get("https://www.google.com").body()
            Result.success(responseText.take(100)) // first 100 chars for logging
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    /**
     * Sends a Base64-encoded image to Cloud Vision API
     */
    suspend fun analyzeImageBase64(base64: String): Result<VisionApiResult> {
        val googleResult = testPlainGoogleCall()
        googleResult.onSuccess {
            android.util.Log.d("AI", "Plain Google call SUCCESS: $it")
        }.onFailure {
            android.util.Log.e("AI", "Plain Google call FAILED", it)
        }
        return try {
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
            Log.d("AI", "Vision URL = https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.VISION_API_KEY}")
            Log.d("AI", "Using API Key: '${BuildConfig.VISION_API_KEY}'")
            Log.d("AI", "Base64 length = ${base64.length}")
            val response: VisionResponse =

                client.post("https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.VISION_API_KEY}") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

            val first = response.responses?.firstOrNull()
                ?: return Result.failure(Exception("Empty Vision API response"))

            Result.success(first)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
