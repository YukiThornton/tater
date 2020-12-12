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
            .url("${config.endpoint()}/3/movie/$id")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${config.authToken()}")
            .build()
        return client.newCall(request).execute()?.let {
            mapper.readValue(it.body().string())
        }!!
    }
}