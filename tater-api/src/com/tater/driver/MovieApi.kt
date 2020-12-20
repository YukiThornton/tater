package com.tater.driver

interface MovieApi {
    suspend fun fetchMovie(id: String): MovieJson

    data class MovieJson(val id: String, val title: String)

    class NotFoundException(override val message: String?, override val cause: Throwable?): RuntimeException()
}