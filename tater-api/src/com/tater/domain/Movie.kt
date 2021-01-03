package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieOverview
import com.tater.domain.attribute.MovieReview
import com.tater.domain.attribute.MovieTitle

data class Movie(
        val id: MovieId,
        val title: MovieTitle,
        val overview: MovieOverview,
        val review: MovieReview
) {
    fun summarize() = MovieSummary(id, title)
}