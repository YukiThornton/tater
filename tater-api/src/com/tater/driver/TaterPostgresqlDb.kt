package com.tater.driver

import com.tater.config.TaterDbConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TaterPostgresqlDb(
    private val config: TaterDbConfig
): TaterDb {

    object User : Table("user") {
        val id = text("id")
        override val primaryKey = PrimaryKey(id)
    }

    object ViewingHistory : Table("viewing_history") {
        val id = text("id")
        val userId = (text("user_id") references User.id)

        override val primaryKey = PrimaryKey(id, userId)
    }

    private class ViewingHistoryResultSet(
        override val movieId: String,
        override val userId: String
    ) : TaterDb.ViewingHistoryDataset

    override fun selectViewingHistoriesByUserId(userId: String): List<TaterDb.ViewingHistoryDataset> {
        Database.connect(config.url(), config.driver(), config.user(), config.password())
        return transaction {
            ViewingHistory
                .select { ViewingHistory.userId eq userId }
                .map { ViewingHistoryResultSet(it[ViewingHistory.id], it[ViewingHistory.userId]) }
        }
    }
}