package com.tater.driver

interface TaterDb {

    interface ViewingHistoryDataset {
        val movieId: String
        val userId: String
    }

    fun selectViewingHistoriesByUserId(userId: String): List<ViewingHistoryDataset>
}