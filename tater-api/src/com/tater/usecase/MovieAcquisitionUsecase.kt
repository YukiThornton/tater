package com.tater.usecase

import com.tater.domain.Movie
import com.tater.domain.attribute.MovieId
import com.tater.domain.UserId
import com.tater.port.MoviePort

class MovieAcquisitionUsecase(
        private val userIdChecker: UserIdChecker,
        private val moviePort: MoviePort
) {
    fun getMovieOf(movieId: MovieId, userIdOrNull: UserId?): Movie? {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            moviePort.getMovieOf(movieId)
        } catch (e: MoviePort.UnavailableException) {
            val message = "Movie(id=${movieId.value}) requested by user(id=${userId.value}) is unavailable"
            throw MovieUnavailableException(e, message)
        }
    }
}

class MovieUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()