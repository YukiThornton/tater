package com.tater.domain

data class PersonalizedMovie(val userId: UserId, val watched: Boolean, private val movie: Movie) {
    val movieId = movie.id
    val movieTitle = movie.title
    val movieReview = movie.review
}