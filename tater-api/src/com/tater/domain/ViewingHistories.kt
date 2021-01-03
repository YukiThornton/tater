package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieIds

data class ViewingHistories(val userId: UserId, val watchedMovieIds: MovieIds) {
    fun watched(id: MovieId) = watchedMovieIds.contains(id)
}