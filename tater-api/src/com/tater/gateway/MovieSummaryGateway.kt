package com.tater.gateway

import com.tater.domain.*
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle
import com.tater.driver.MovieApi
import com.tater.port.MovieSummaryPort

class MovieSummaryGateway(
    private val movieApi: MovieApi
): MovieSummaryPort {
    override suspend fun fetchMovieSummaryOf(movieId: MovieId): MovieSummary? = try {
        movieApi.fetchMovie(movieId.value).let { json ->
            MovieSummary(MovieId(json.id), MovieTitle(json.title))
        }
    } catch (e: MovieApi.NotFoundException) {
        null
    } catch (e: Throwable) {
        throw MovieSummaryPort.UnavailableException("Movie summary for movie(id=${movieId.value}) unavailable", e)
    }
}