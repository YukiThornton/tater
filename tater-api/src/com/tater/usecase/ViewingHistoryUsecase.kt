package com.tater.usecase

import com.tater.domain.*
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort
import kotlinx.coroutines.*

class ViewingHistoryUsecase(
    private val viewingHistoryPort: ViewingHistoryPort,
    private val movieSummaryPort: MovieSummaryPort
) {

    fun allMoviesWatchedBy(userIdOrNull: UserId?): MovieSummaries = runBlocking {
        val userId = makeSureUserIdExists(userIdOrNull)
        try {
            val movieIds = getMovieIdsWatchedBy(userId)
            fetchMovieSummariesOf(movieIds)
        } catch (e: DataAccessException) {
            throw e.toUnavailableExceptionWith(userId)
        }
    }

    private fun makeSureUserIdExists(userId: UserId?): UserId {
        if (userId == null) throw UserNotSpecifiedException("UserId is missing")
        return userId
    }

    private fun getMovieIdsWatchedBy(userId: UserId): MovieIds = try {
        viewingHistoryPort.getViewingHistoriesFor(userId).movieIds()
    } catch (e: ViewingHistoryPort.UnavailableException) {
        throw DataAccessException(e)
    }

    private suspend fun fetchMovieSummariesOf(movieIds: MovieIds): MovieSummaries = try {
        coroutineScope {
            startFetchingSummariesOf(movieIds)
                .waitAllToComplete()
                .wrapToSummariesSkippingNull()
        }
    } catch (e: MovieSummaryPort.UnavailableException) {
        throw DataAccessException(e, movieIds)
    }

    private suspend fun startFetchingSummariesOf(movieIds: MovieIds) = coroutineScope {
        movieIds.map { async { movieSummaryPort.fetchMovieSummaryOf(it) } }
    }

    private suspend fun List<Deferred<MovieSummary?>>.waitAllToComplete() = awaitAll(*this.toTypedArray())

    private fun List<MovieSummary?>.wrapToSummariesSkippingNull() = this.filterNotNull().let(::MovieSummaries)

    private class DataAccessException(override val cause: Throwable, val movieIds: MovieIds? = null): RuntimeException() {
        fun toUnavailableExceptionWith(userId: UserId): WatchedMoviesUnavailableException {
            val message = if (movieIds == null) {
                "Movies watched by user(id=${userId.value}) are unavailable"
            } else {
                "Movies(ids=[${movieIds.join(",")}]) watched by user(id=${userId.value}) are unavailable"
            }
            return WatchedMoviesUnavailableException(message, cause)
        }
        private fun MovieIds.join(separator: String) = this.joinToString(separator) { it.value }
    }
}

class UserNotSpecifiedException(override val message: String?) : RuntimeException()
class WatchedMoviesUnavailableException(override val message: String?, override val cause: Throwable?): RuntimeException()
