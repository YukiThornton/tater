package com.tater.domain

import com.tater.domain.attribute.MovieTitle

data class LocalizedMovie(private val movie: Movie, private val japaneseAttributes: LocalizedMovieAttributes?) {
    fun id() = movie.id
    fun englishTitle() = movie.title
    fun japaneseTitle(): MovieTitle? = japaneseAttributes?.title
    fun overview() = movie.overview
    fun runtime() = movie.runtime
    fun review() = movie.review
}