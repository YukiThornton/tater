package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieDetailPort

class MovieDetailGateway(
        private val movieApi: MovieApi
): MovieDetailPort {
    override fun getDetailsOf(movieId: MovieId): MovieDetails? {
        return try {
            movieApi.getMovie(movieId.value).toMovieDetails()
        } catch (e: MovieApi.NotFoundException) {
            null
        } catch (e: Throwable) {
            throw MovieDetailPort.UnavailableException(e, "Movie(id=${movieId.value}) is unavailable")
        }
    }

    private fun MovieApi.MovieDetailJson.toMovieDetails() = MovieDetails(
            MovieId(this.id),
            MovieTitle(this.title),
            MovieOverview(this.overview),
            MovieReview(AverageScore(this.voteAverage), ReviewCount(this.voteCount))
    )
}