package com.tater.gateway

import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.domain.ViewingHistories
import com.tater.domain.ViewingHistory
import com.tater.driver.TaterDb
import com.tater.port.ViewingHistoryPort

class ViewingHistoryGateway(
    private val taterDb: TaterDb
): ViewingHistoryPort {

    override fun getViewingHistoriesFor(userId: UserId): ViewingHistories {
        try {
            val histories = taterDb.selectViewingHistoriesByUserId(userId.value)
            return histories.map { ViewingHistory(userId, MovieId(it.movieId)) }.let(::ViewingHistories)
        } catch (e: Throwable) {
            throw ViewingHistoryPort.UnavailableException("Viewing history for user(id=${userId.value}) unavailable", e)
        }
    }
}