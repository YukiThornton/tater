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

    override suspend fun fetchMovie(id: String): MovieApi.MovieDetailJson {
        val request = createRequest("/3/movie/$id", mapOf("api_key" to config.authToken()))
        val asyncRequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val response = asyncRequest.await()
        return when(response.statusCode()) {
            200 -> mapper.readValue(response.body())
            404 -> throw notFoundException(id)
            else -> throw response.toException()
        }
    }

    override fun getMovie(id: String): MovieApi.MovieDetailJson {
        val request = createRequest("/3/movie/$id", mapOf("api_key" to config.authToken()))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return when(response.statusCode()) {
            200 -> mapper.readValue(response.body())
            404 -> throw notFoundException(id)
            else -> throw response.toException()
        }
    }

    private fun notFoundException(id: String) = MovieApi.NotFoundException("movie(id=$id) not found", null)

    override fun searchMovies(conditions: Map<String, Any>): MovieApi.SearchedMovieListJson {
        val queryParams = conditions.plus(mapOf(
                "api_key" to config.authToken(),
                "page" to 1
        ))
        val request = createRequest("/3/discover/movie", queryParams)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return when (response.statusCode()) {
            200 -> mapper.readValue(response.body())
            else -> throw response.toException()
        }
    }

    override fun getMovieTranslations(id: String): MovieApi.TranslationsJson {
        val request = createRequest("/3/movie/$id/translations", mapOf("api_key" to config.authToken()))
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return when(response.statusCode()) {
            200 -> mapper.readValue(response.body())
            else -> TODO("status node not 200")
        }
    }

    private fun HttpResponse<String>.toException() =
            RuntimeException("statusCode=${this.statusCode()}, body=${this.body()}")

    private fun createRequest(path: String, queryParams: Map<String, Any>): HttpRequest? {
        val query = queryParams.map { (key, value) -> "$key=$value" }.joinToString("&")
        return HttpRequest.newBuilder()
                .uri(URI.create("${config.endpoint()}$path?$query"))
                .build()
    }

}