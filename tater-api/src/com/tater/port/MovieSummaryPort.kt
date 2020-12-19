package com.tater.port

import com.tater.domain.MovieId
import com.tater.domain.MovieSummary

interface MovieSummaryPort {
    suspend fun movieSummaryOf(movieId: MovieId): MovieSummary?

    class UnavailableException(override val message: String?, override val cause: Throwable?): RuntimeException()
}