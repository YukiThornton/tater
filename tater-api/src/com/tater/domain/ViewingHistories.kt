package com.tater.domain

data class ViewingHistories(val userId: UserId, val watchedMovieIds: MovieIds) {
    fun watched(id: MovieId) = watchedMovieIds.contains(id)
}