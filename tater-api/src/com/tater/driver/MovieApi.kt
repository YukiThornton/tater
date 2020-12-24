package com.tater.driver

import com.fasterxml.jackson.annotation.JsonAlias

interface MovieApi {
    suspend fun fetchMovie(id: String): MovieDetailJson
    fun searchMovies(conditions: Map<String, Any>): MovieListJson

    data class MovieDetailJson(val id: String, val title: String)
    data class MovieJson(
            val id: String,
            val title: String,
            @JsonAlias("vote_average") val voteAverage: Double,
            @JsonAlias("vote_count") val voteCount: Int)
    data class MovieListJson(val results: List<MovieJson>)

    class NotFoundException(override val message: String?, override val cause: Throwable?): RuntimeException()
}