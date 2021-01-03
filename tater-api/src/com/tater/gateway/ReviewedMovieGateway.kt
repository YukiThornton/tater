package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.ReviewedMoviePort

class ReviewedMovieGateway(private val movieApi: MovieApi): ReviewedMoviePort {
    override fun searchMovies(searchFilter: MovieSearchFilter, sort: SortedBy): ReviewedMovies {
        return try {
            movieApi.searchMovies(createConditions(sort, searchFilter)).toMovies()
        } catch (e: Throwable) {
            throw ReviewedMoviePort.SearchUnavailableException("MovieApi's movie search functionality is not available", e)
        }
    }

    private fun createConditions(sort: SortedBy, searchFilter: MovieSearchFilter): Map<String, Any> {
        return mapOf<String, Any>(
                "sort_by" to sort.toKey(),
                "vote_count.gte" to searchFilter.minReviewCount(),
                "include_adult" to searchFilter.includeAdult(),
                "include_video" to searchFilter.includeVideo()
        )
    }

    private fun SortedBy.toKey() = when (this) {
        SortedBy.ReviewAverageDesc -> "vote_average.desc"
    }

    private fun MovieApi.MovieListJson.toMovies() = this.results.map { it.toMovie() }.let(::ReviewedMovies)

    private fun MovieApi.MovieJson.toMovie() = ReviewedMovie(
            MovieId(this.id),
            MovieTitle(this.title),
            MovieReview(AverageScore(this.voteAverage), ReviewCount(this.voteCount)))

}