/**
 * Created by Eric Nguyen, Jacob Nguyen, Collin Giles
 * Fall 2025, CS4530
 *
 * Tests the fo
 */
package com.example.phase1

import com.example.phase1.data.repository.VisionRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertTrue

// helper to build a mockable Ktor client
private fun mockClient(
    handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
): HttpClient {
    return HttpClient(MockEngine(handler)) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

// helper to inject the mock client into VisionRepository
private fun VisionRepository.setMockClient(client: HttpClient) {
    val f = VisionRepository::class.java.getDeclaredField("client")
    f.isAccessible = true
    f.set(this, client)
}

class VisionRepositoryTest {

    // test that a normal API call works, sends correct JSON, and parses result
    @Test
    fun `test analyzeImageBase64 - correct request + correct parsing`() = runTest {
        var capturedRequestBody: String? = null

        val client = mockClient { request ->

            if (request.url.host == "vision.googleapis.com") {

                // grab outgoing request body so we can check it
                capturedRequestBody = when (val content = request.body) {
                    is OutgoingContent.ByteArrayContent -> String(content.bytes())
                    is OutgoingContent.ReadChannelContent -> content.readFrom().readRemaining().readText()
                    else -> ""
                }

                // respond with fake detection
                respond(
                    """
                    {
                      "responses": [{
                        "labelAnnotations": [
                          {"description": "cat", "score": 0.95}
                        ],
                        "localizedObjectAnnotations": []
                      }]
                    }
                    """.trimIndent(),
                    headers = headersOf("Content-Type", "application/json")
                )
            }

            // test GET to google.com
            if (request.url.host == "www.google.com") {
                respond("OK")
            } else respondError(HttpStatusCode.BadRequest)
        }

        val repo = VisionRepository()
        repo.setMockClient(client)

        val result = repo.analyzeImageBase64("ABC123_BASE64")

        assertTrue(result.isSuccess)

        // make sure request body had base64 + features
        assertTrue(capturedRequestBody!!.contains("ABC123_BASE64"))
        assertTrue(capturedRequestBody!!.contains("LABEL_DETECTION"))
        assertTrue(capturedRequestBody!!.contains("OBJECT_LOCALIZATION"))

        // make sure parsing worked
        val parsed = result.getOrNull()
        assertTrue(parsed?.labelAnnotations?.first()?.description == "cat")
    }

    // test that empty API responses return failure
    @Test
    fun `test analyzeImageBase64 - empty responses returns failure`() = runTest {
        val client = mockClient { request ->
            if (request.url.host == "www.google.com") return@mockClient respond("OK")

            if (request.url.host == "vision.googleapis.com")
                return@mockClient respond("""{"responses": []}""",
                    headers = headersOf("Content-Type", "application/json"))

            respondError(HttpStatusCode.BadRequest)
        }

        val repo = VisionRepository()
        repo.setMockClient(client)

        val result = repo.analyzeImageBase64("BASE64")
        assertTrue(result.isFailure)
    }

    // test that network failure triggers failure result
    @Test
    fun `test analyzeImageBase64 - network failure returns failure`() = runTest {
        val client = mockClient { throw RuntimeException("network down") }

        val repo = VisionRepository()
        repo.setMockClient(client)

        val result = repo.analyzeImageBase64("BASE64")
        assertTrue(result.isFailure)
    }

    // test that testPlainGoogleCall handles errors correctly
    @Test
    fun `testPlainGoogleCall - error returns failure`() = runTest {
        val client = mockClient { throw RuntimeException("oops") }

        val repo = VisionRepository()
        repo.setMockClient(client)

        val result = repo.testPlainGoogleCall()
        assertTrue(result.isFailure)
    }
}
