package com.tater.gateway

import com.tater.domain.*
import com.tater.driver.TaterDb
import com.tater.port.ViewingHistoryPort

class ViewingHistoryGateway(
    private val taterDb: TaterDb
): ViewingHistoryPort {

    override fun getViewingHistoriesFor(userId: UserId): ViewingHistories {
        try {
            val histories = taterDb.selectViewingHistoriesByUserId(userId.value)
            val watchedMovieIds = histories.map { MovieId(it.movieId) }.let(::MovieIds)
            return ViewingHistories(userId, watchedMovieIds)
        } catch (e: Throwable) {
            throw ViewingHistoryPort.UnavailableException("Viewing history for user(id=${userId.value}) unavailable", e)
        }
    }
}