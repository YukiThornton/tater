package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieSummaryPort

class MovieSummaryGateway(
    private val movieApi: MovieApi
): MovieSummaryPort {

    override fun movieSummariesOf(movieIds: MovieIds): MovieSummaries {
        return movieIds.map { movieId ->
            movieApi.getMovie(movieId.value).let { json ->
                MovieSummary(MovieId(json.id), MovieTitle(json.title))
            }
        }.let(::MovieSummaries)
    }
}