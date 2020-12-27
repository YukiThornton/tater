package com.tater.usecase

import com.tater.domain.*
import com.tater.port.MoviePort
import com.tater.port.ViewingHistoryPort

class MovieSearchUsecase(
        private val userIdChecker: UserIdChecker,
        private val moviePort: MoviePort,
        private val viewingHistoryPort: ViewingHistoryPort
) {

    companion object {
        private val MIN_REVIEW_COUNT = ReviewCount(1000)
    }

    fun topRatedMovies(userIdOrNull: UserId?): PersonalizedMovies {
        val userId = userIdChecker.makeSureUserIdExists(userIdOrNull)
        return try {
            val movies = moviePort.searchMovies(MovieSearchFilter.withMinimumReviewCount(MIN_REVIEW_COUNT), SortedBy.ReviewAverageDesc)
            if (movies.isEmpty()) return PersonalizedMovies(emptyList())
            val histories = viewingHistoryPort.getViewingHistoriesFor(userId)
            PersonalizedMovies.from(movies, histories)
        } catch (e: MoviePort.SearchUnavailableException) {
            throw createException(e, userId)
        } catch (e: ViewingHistoryPort.UnavailableException) {
            throw createException(e, userId)
        }
    }

    private fun createException(e: Throwable, userId: UserId) =
            TopRatedMoviesUnavailableException(e, "Top rated movies for user(id=${userId.value}) are unavailable")
}

class TopRatedMoviesUnavailableException(override val cause: Throwable, override val message: String?): RuntimeException()