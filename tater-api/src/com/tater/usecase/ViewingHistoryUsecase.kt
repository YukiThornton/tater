package com.tater.usecase

import com.tater.domain.MovieIds
import com.tater.domain.MovieSummaries
import com.tater.domain.UserId
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort

class ViewingHistoryUsecase(
    private val viewingHistoryPort: ViewingHistoryPort,
    private val movieSummaryPort: MovieSummaryPort
) {

    fun allMoviesWatchedBy(userId: UserId?): MovieSummaries {
        if (userId == null) throw UserNotSpecifiedException("UserId is missing")
        val movieIds = watchedMovieIds(userId)
        return try {
            movieIds.mapNotNull { movieSummaryPort.movieSummaryOf(it) }.let(::MovieSummaries)
        } catch (e: MovieSummaryPort.UnavailableException) {
            throw unavailableException(userId, movieIds, e)
        }
    }

    private fun watchedMovieIds(userId: UserId): MovieIds {
        return try {
            viewingHistoryPort.viewingHistoriesFor(userId).movieIds()
        } catch (e: ViewingHistoryPort.UnavailableException) {
            throw unavailableException(userId, e)
        }
    }

    private fun unavailableException(userId: UserId, cause: Throwable) =
        WatchedMoviesUnavailableException("Movies watched by user(id=${userId.value}) are unavailable", cause)
    private fun unavailableException(userId: UserId, movieIds: MovieIds, cause: Throwable) =
        WatchedMoviesUnavailableException("Movies(ids=[${movieIds.join(",")}]) watched by user(id=${userId.value}) are unavailable", cause)
    private fun MovieIds.join(separator: String) = this.joinToString(separator) { it.value }
}

class UserNotSpecifiedException(override val message: String?) : RuntimeException()
class WatchedMoviesUnavailableException(override val message: String?, override val cause: Throwable?): RuntimeException()