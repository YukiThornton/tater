package com.tater.usecase

import com.tater.domain.*
import com.tater.port.MoviePort

class RecommendationUsecase(private val userIdChecker: UserIdChecker, private val moviePort: MoviePort) {

    companion object {
        private val MIN_REVIEW_COUNT = ReviewCount(1000)
    }

    fun recommendedMovies(userId: UserId?): Movies {
        userIdChecker.makeSureUserIdExists(userId)
        return moviePort.searchMovies(MovieSearchFilter.withMinimumReviewCount(MIN_REVIEW_COUNT), SortedBy.ReviewAverageDesc)
    }
}