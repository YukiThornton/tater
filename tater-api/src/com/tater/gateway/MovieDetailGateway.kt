package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieDetailPort

class MovieDetailGateway(
        private val movieApi: MovieApi
): MovieDetailPort {
    override fun getDetailsOf(movieId: MovieId): MovieDetails {
        return movieApi.getMovie(movieId.value).toMovieDetails()
    }

    private fun MovieApi.MovieDetailJson.toMovieDetails() = MovieDetails(
            MovieId(this.id),
            MovieTitle(this.title),
            MovieReview(AverageScore(this.voteAverage), ReviewCount(this.voteCount))
    )
}