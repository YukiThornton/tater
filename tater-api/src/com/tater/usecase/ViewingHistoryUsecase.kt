package com.tater.usecase

import com.tater.domain.MovieSummaries
import com.tater.domain.UserId
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort

class ViewingHistoryUsecase(
    private val viewingHistoryPort: ViewingHistoryPort,
    private val movieSummaryPort: MovieSummaryPort
) {

    fun allMoviesWatchedBy(userId: UserId): MovieSummaries {
        val histories = viewingHistoryPort.viewingHistoriesFor(userId)
        return movieSummaryPort.movieSummariesOf(histories.movieIds())
    }
}