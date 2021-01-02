package com.tater.domain

data class MovieDetails(
        val id: MovieId,
        val title: MovieTitle,
        val overview: MovieOverview,
        val review: MovieReview
)