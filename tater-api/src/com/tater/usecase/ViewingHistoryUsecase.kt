package com.tater.usecase

import com.tater.domain.MovieIds
import com.tater.domain.MovieSummaries
import com.tater.domain.UserId
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort
import kotlinx.coroutines.*

class ViewingHistoryUsecase(
    private val viewingHistoryPort: ViewingHistoryPort,
    private val movieSummaryPort: MovieSummaryPort
) {

    fun allMoviesWatchedBy(userId: UserId?): MovieSummaries {
        if (userId == null) throw UserNotSpecifiedException("UserId is missing")
        val movieIds = movieIdsWatchedBy(userId)
        return runBlocking {
            try {
                movieSummariesOf(movieIds)
            } catch (e: MovieSummaryPort.UnavailableException) {
                throw unavailableException(userId, movieIds, e)
            }
        }
    }

    private fun movieIdsWatchedBy(userId: UserId): MovieIds {
        return try {
            viewingHistoryPort.viewingHistoriesFor(userId).movieIds()
        } catch (e: ViewingHistoryPort.UnavailableException) {
            throw unavailableException(userId, e)
        }
    }

    private suspend fun movieSummariesOf(movieIds: MovieIds): MovieSummaries {
        return coroutineScope {
            val movieTasks = movieIds.map {
                async { movieSummaryPort.movieSummaryOf(it) }
            }
            awaitAll(*movieTasks.toTypedArray()).filterNotNull().let(::MovieSummaries)
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
