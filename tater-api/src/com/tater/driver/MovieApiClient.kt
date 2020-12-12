package com.tater.driver

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

class MovieApiClient: MovieApi {
    private val client = OkHttpClient()
    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()

    override fun getMovie(id: String): MovieApi.MovieJson {
        val request = Request.Builder()
            .url("http://wiremock-svc:8080/3/movie/$id")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer movieApiFakeToken")
            .build()
        return client.newCall(request).execute()?.let {
            mapper.readValue(it.body().string())
        }!!
    }
}