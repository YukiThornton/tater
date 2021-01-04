package com.tater.usecase

import com.tater.domain.LocalizedMovie
import com.tater.domain.attribute.MovieId
import com.tater.domain.UserId
import com.tater.port.LocalizedAttributesPort
import com.tater.port.MoviePort

class MovieAcquisitionUsecase(
        private val userIdChecker: UserIdChecker,
        private val moviePort: MoviePort,
        private val localizedAttributesPort: LocalizedAttributesPort
) {
    fun getMovieOf(movieId: MovieId, userIdOrNull: UserId?): LocalizedMovie? {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            moviePort.getMovieOf(movieId)?.let{
                val japaneseAttributes = localizedAttributesPort.getJapaneseAttributesOf(movieId)
                LocalizedMovie(it, japaneseAttributes)
            }
        } catch (e: MoviePort.UnavailableException) {
            val message = "Movie(id=${movieId.value}) requested by user(id=${userId.value}) is unavailable"
            throw MovieUnavailableException(e, message)
        }
    }
}

class MovieUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()