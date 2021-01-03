package com.tater.port

import com.tater.domain.Movie
import com.tater.domain.attribute.MovieId

interface MoviePort {
    fun getMovieOf(movieId: MovieId): Movie?
    suspend fun fetchMovieOf(movieId: MovieId): Movie?

    class UnavailableException(override val cause: Throwable, override val message: String): RuntimeException()
}