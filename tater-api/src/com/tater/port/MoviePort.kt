package com.tater.port

import com.tater.domain.Movie
import com.tater.domain.MovieId

interface MoviePort {
    fun getDetailsOf(movieId: MovieId): Movie?

    class UnavailableException(override val cause: Throwable, override val message: String): RuntimeException()
}