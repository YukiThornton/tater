package com.tater.usecase

import com.tater.domain.*
import com.tater.port.MoviePort

class RecommendationUsecase(private val userIdChecker: UserIdChecker, private val moviePort: MoviePort) {

    companion object {
        private val MIN_REVIEW_COUNT = ReviewCount(1000)
    }

    fun recommendedMovies(userIdOrNull: UserId?): Movies {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            moviePort.searchMovies(MovieSearchFilter.withMinimumReviewCount(MIN_REVIEW_COUNT), SortedBy.ReviewAverageDesc)
        } catch (e: MoviePort.SearchUnavailableException) {
            throw RecommendedMoviesUnavailableException(e, "Recommended movies for user(id=${userId.value}) are unavailable")
        }
    }
}

class RecommendedMoviesUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()