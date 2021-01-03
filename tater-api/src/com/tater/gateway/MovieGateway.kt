package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MoviePort

class MovieGateway(
        private val movieApi: MovieApi
): MoviePort {
    override fun getMovieOf(movieId: MovieId): Movie? {
        return try {
            movieApi.getMovie(movieId.value).toMovie()
        } catch (e: MovieApi.NotFoundException) {
            null
        } catch (e: Throwable) {
            throw MoviePort.UnavailableException(e, "Movie(id=${movieId.value}) is unavailable")
        }
    }

    private fun MovieApi.MovieDetailJson.toMovie() = Movie(
            MovieId(this.id),
            MovieTitle(this.title),
            MovieOverview(this.overview),
            MovieReview(AverageScore(this.voteAverage), ReviewCount(this.voteCount))
    )
}