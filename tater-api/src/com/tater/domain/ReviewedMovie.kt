package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieReview
import com.tater.domain.attribute.MovieTitle

data class ReviewedMovie(
        val id: MovieId,
        val title: MovieTitle,
        val review: MovieReview
)