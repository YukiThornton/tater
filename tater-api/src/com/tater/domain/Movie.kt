package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieOverview
import com.tater.domain.attribute.MovieReview
import com.tater.domain.attribute.MovieTitle
import com.tater.domain.attribute.Runtime

data class Movie(
        val id: MovieId,
        val title: MovieTitle,
        val overview: MovieOverview,
        val runtime: Runtime,
        val review: MovieReview
) {
    fun summarize() = MovieSummary(id, title)
}