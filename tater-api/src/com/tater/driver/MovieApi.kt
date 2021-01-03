package com.tater.driver

import com.fasterxml.jackson.annotation.JsonAlias

interface MovieApi {
    suspend fun fetchMovie(id: String): MovieDetailJson
    fun getMovie(id: String): MovieDetailJson
    fun searchMovies(conditions: Map<String, Any>): SearchedMovieListJson

    data class MovieDetailJson(
            val id: String,
            val title: String,
            val overview: String,
            val runtime: Int,
            @JsonAlias("vote_average") val voteAverage: Double,
            @JsonAlias("vote_count") val voteCount: Int)

    data class SearchedMovieJson(
            val id: String,
            val title: String,
            @JsonAlias("vote_average") val voteAverage: Double,
            @JsonAlias("vote_count") val voteCount: Int)
    data class SearchedMovieListJson(val results: List<SearchedMovieJson>)

    class NotFoundException(override val message: String?, override val cause: Throwable?): RuntimeException()
}