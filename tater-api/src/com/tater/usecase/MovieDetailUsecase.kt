package com.tater.usecase

import com.tater.domain.Movie
import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.port.MoviePort

class MovieDetailUsecase(
        private val userIdChecker: UserIdChecker,
        private val moviePort: MoviePort
) {
    fun detailsOf(movieId: MovieId, userIdOrNull: UserId?): Movie? {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            moviePort.getDetailsOf(movieId)
        } catch (e: MoviePort.UnavailableException) {
            val message = "Movie(id=${movieId.value}) requested by user(id=${userId.value}) is unavailable"
            throw MovieDetailsUnavailableException(e, message)
        }
    }
}

class MovieDetailsUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()