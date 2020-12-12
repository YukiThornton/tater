package com.tater.domain

data class ViewingHistories(override val values: List<ViewingHistory>): FCC<ViewingHistory> {
    fun movieIds() = map { it.movieId }.let(::MovieIds)
}