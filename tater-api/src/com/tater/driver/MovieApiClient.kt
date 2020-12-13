package com.tater.driver

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.tater.config.MovieApiConfig

class MovieApiClient(
    private val config: MovieApiConfig
): MovieApi {
    private val client = OkHttpClient()
    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()

    override fun getMovie(id: String): MovieApi.MovieJson {
        val request = Request.Builder()
            .url("${config.endpoint()}/3/movie/$id?api_key=${config.authToken()}")
            .header("Content-Type", "application/json")
            .build()
        return client.newCall(request).execute()?.let {
            when(it.code()) {
                200 -> mapper.readValue(it.body().string())
                404 -> throw MovieApi.NotFoundException("movie(id=$id) not found", null)
                else -> throw RuntimeException("statusCode=${it.code()}, body=${it.body().string()}")
            }
        }!!
    }
}