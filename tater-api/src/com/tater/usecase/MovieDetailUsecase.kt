package com.tater.usecase

import com.tater.domain.MovieDetails
import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.port.MovieDetailPort

class MovieDetailUsecase(
        private val userIdChecker: UserIdChecker,
        private val movieDetailPort: MovieDetailPort
) {
    fun detailsOf(movieId: MovieId, userIdOrNull: UserId?): MovieDetails {
        userIdChecker.makeSureUserIdExists(userIdOrNull)
        return movieDetailPort.getDetailsOf(movieId)
    }
}