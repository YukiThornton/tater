package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieSummaryPort

class MovieSummaryGateway(
    private val movieApi: MovieApi
): MovieSummaryPort {
    override fun movieSummaryOf(movieId: MovieId): MovieSummary? {
        return try {
            movieApi.getMovie(movieId.value).let { json ->
                MovieSummary(MovieId(json.id), MovieTitle(json.title))
            }
        } catch (e: MovieApi.NotFoundException) {
            null
        } catch (e: Throwable) {
            throw MovieSummaryPort.UnavailableException("Movie summary for movie(id=${movieId.value}) unavailable", e)
        }
    }
}