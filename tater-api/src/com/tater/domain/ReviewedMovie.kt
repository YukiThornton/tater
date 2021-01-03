package com.tater.domain

data class ReviewedMovie(
        val id: MovieId,
        val title: MovieTitle,
        val review: MovieReview
)