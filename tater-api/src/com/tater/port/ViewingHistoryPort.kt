package com.tater.port

import com.tater.domain.UserId
import com.tater.domain.ViewingHistories

interface ViewingHistoryPort {
    fun viewingHistoriesFor(userId: UserId): ViewingHistories
}