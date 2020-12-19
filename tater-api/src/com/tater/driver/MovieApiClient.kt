package com.tater.driver

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.tater.config.MovieApiConfig
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class MovieApiClient(
    private val config: MovieApiConfig
): MovieApi {
    private val client = HttpClient.newBuilder().build()
    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()

    override suspend fun getMovie(id: String): MovieApi.MovieJson {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${config.endpoint()}/3/movie/$id?api_key=${config.authToken()}"))
            .header("Content-Type", "application/json")
            .build()
        val asyncRequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val response = asyncRequest.await()
        return when(response.statusCode()) {
            200 -> mapper.readValue(response.body())
            404 -> throw MovieApi.NotFoundException("movie(id=$id) not found", null)
            else -> throw RuntimeException("statusCode=${response.statusCode()}, body=${response.body()}")
        }
    }
}