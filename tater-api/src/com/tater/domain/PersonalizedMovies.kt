package com.tater.domain

data class PersonalizedMovies(override val values: List<PersonalizedMovie>) : FCC<PersonalizedMovie> {
    companion object {
        fun from(movies: ReviewedMovies, viewingHistories: ViewingHistories): PersonalizedMovies {
            return movies.map { movie ->
                PersonalizedMovie(viewingHistories.userId, viewingHistories.watched(movie.id), movie)
            }.let(::PersonalizedMovies)
        }
    }
}