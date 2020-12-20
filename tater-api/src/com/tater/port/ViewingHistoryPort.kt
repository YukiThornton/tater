package com.tater.port

import com.tater.domain.UserId
import com.tater.domain.ViewingHistories

interface ViewingHistoryPort {
    fun getViewingHistoriesFor(userId: UserId): ViewingHistories

    class UnavailableException(override val message: String?, override val cause: Throwable?): RuntimeException()
}