package com.tater.usecase

import com.tater.domain.MovieDetails
import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.port.MovieDetailPort

class MovieDetailUsecase(
        private val userIdChecker: UserIdChecker,
        private val movieDetailPort: MovieDetailPort
) {
    fun detailsOf(movieId: MovieId, userIdOrNull: UserId?): MovieDetails? {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            movieDetailPort.getDetailsOf(movieId)
        } catch (e: MovieDetailPort.UnavailableException) {
            val message = "Movie(id=${movieId.value}) requested by user(id=${userId.value}) is unavailable"
            throw MovieDetailsUnavailableException(e, message)
        }
    }
}

class MovieDetailsUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()