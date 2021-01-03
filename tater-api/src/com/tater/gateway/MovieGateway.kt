package com.tater.gateway

import com.tater.domain.*
import com.tater.domain.attribute.*
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
            throw unavailableException(e, movieId)
        }
    }

    override suspend fun fetchMovieOf(movieId: MovieId): Movie? {
        return try {
            movieApi.fetchMovie(movieId.value).toMovie()
        } catch (e: MovieApi.NotFoundException) {
            null
        } catch (e: Throwable) {
            throw unavailableException(e, movieId)
        }
    }

    private fun unavailableException(e: Throwable, movieId: MovieId) =
            MoviePort.UnavailableException(e, "Movie(id=${movieId.value}) is unavailable")

    private fun MovieApi.MovieDetailJson.toMovie() = Movie(
            MovieId(this.id),
            MovieTitle(this.title),
            MovieOverview(this.overview),
            Runtime(this.runtime),
            MovieReview(AverageScore(this.voteAverage), ReviewCount(this.voteCount))
    )
}