package com.example.phase1.data.repository

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

class VisionRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Sends a Base64-encoded image to Cloud Vision API
     */
    suspend fun analyzeImageBase64(base64: String): Result<VisionApiResult> {
        return try {
            val request = VisionRequest(
                requests = listOf(
                    VisionImageRequest(
                        image = VisionImage(base64),
                        features = listOf(
                            VisionFeature("LABEL_DETECTION", 10),
                            VisionFeature("OBJECT_LOCALIZATION", 10)
                        )
                    )
                )
            )

            val response: VisionResponse =
                client.post("https://vision.googleapis.com/v1/images:annotate?key=${BuildConfig.VISION_API_KEY}") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

            val first = response.responses.firstOrNull()
                ?: return Result.failure(Exception("Empty Vision API response"))

            Result.success(first)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
